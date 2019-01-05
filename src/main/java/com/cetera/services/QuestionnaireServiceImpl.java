package com.cetera.services;

import com.cetera.dao.BdQaRepository;
import com.cetera.dao.QuestionnaireRepository;
import com.cetera.domain.BdQa;
import com.cetera.domain.BrokerDealer;
import com.cetera.domain.CategoryDetails;
import com.cetera.domain.CurrentSession;
import com.cetera.domain.QuestionDetails;
import com.cetera.domain.Questionnaire;
import com.cetera.domain.QuestionnaireMapping;
import com.cetera.domain.Sessions;
import com.cetera.enums.AnswerType;
import com.cetera.enums.QuestionnaireName;
import com.cetera.enums.Status;
import com.cetera.enums.SystemUser;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by danni on 3/23/16.
 */
@Service
public class QuestionnaireServiceImpl implements QuestionnaireService {
    private static Logger logger = LoggerFactory.getLogger(QuestionnaireServiceImpl.class);

    @Autowired
    private QuestionnaireRepository questionnaireRepository;
    
    @Autowired
    private BdQaRepository bdQaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CurrentSession currentSession;

    @Autowired
    private AsciiValidationService validateAsciiService;

    @Autowired
    private BrokerDealerService brokerDealerService;

    @Autowired
    private ResourcesQaService resourcesQaService;

    /**
     * used by Admin to get all questionnaires with BD_ID as a list
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Questionnaire> getAll() {
        String sql = "SELECT q.*, NVL(to_char(bd.external_id), 'External') as bd_id, " +
            " NVL(bd.external_id, 0) as bd_id_for_api, mappingtable.bd_id_group FROM questionnaire q " +
            " INNER JOIN bd_qa bq ON bq.questionnaire_id=q.id INNER JOIN broker_dealer bd on bd.id=bq.bd_id " +
            " INNER JOIN " +
            " (SELECT bq.questionnaire_id, " +
            " LISTAGG(NVL(to_char(bd.external_id), 'External'), ',') WITHIN GROUP (ORDER BY bd.external_id) AS bd_id_group " +
            " FROM bd_qa bq " +
            " INNER JOIN broker_dealer bd " +
            " ON bd.id=bq.bd_id " +
            " WHERE bq.status = 'ACTIVE' " +
            " GROUP BY bq.questionnaire_id) mappingtable " +
            " ON mappingtable.questionnaire_id = q.id " +
            " WHERE q.status = 'ACTIVE' AND bq.status='ACTIVE' " +
            " ORDER BY to_number(regexp_substr(bd_id,'[0-9]+')) ";
        List<Questionnaire> questionnaireList = jdbcTemplate.query(sql, new BeanPropertyRowMapper(Questionnaire.class));
        return questionnaireList;
    }

    /**
     * get the latest questionnaire
     * @param name
     * @return
     */
    @Override
    public Questionnaire retrieve(String name) {
        Sessions sessions = currentSession.getSession();
        /**
         * return only question count from external questionnaire
         */
        if (sessions == null) {
            return getQa();
        } else {
            return getQa(sessions);
        }
    }

    private Questionnaire getQa(Sessions sessions) {
        List<Questionnaire> questionnaires = new ArrayList<>();
        String sql = "SELECT q.id, q.name, q.questions from questionnaire q inner join bd_qa bq " +
            "on bq.questionnaire_id=q.id " +
            "inner join person p on p.bd_id = bq.bd_id where p.id = ? and q.status = ? and bq.status = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, sessions.getPersonId(), Status.ACTIVE.name(),
            Status.ACTIVE.name());

        for (Map<String, Object> row: rows) {
            Questionnaire questionnaire = new Questionnaire(
                ((BigDecimal) row.get("id")).longValue(),
                (String) row.get("name"),
                (String) row.get("questions")
            );

            questionnaires.add(questionnaire);
        }

        if (questionnaires.size() != 1) {
            throw new PfmException("Wrong questionnaire data.", PfmExceptionCode.QUESTIONNAIRE_API_ERROR);
        }
        return questionnaires.get(0);
    }
    /**
     * get qa count without session
     * only using external qa
     * @return
     */
    private Questionnaire getQa() {
        List<Questionnaire> questionnaires = new ArrayList<>();
        String sql = "SELECT c.questions from questionnaire c inner join bd_qa b " +
            "on b.questionnaire_id=c.id inner join broker_dealer a on a.id = b.bd_id " +
            "where a.external_id is null and c.status = ? and b.status = ?";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, Status.ACTIVE.name(), Status.ACTIVE.name());

        for (Map<String, Object> row: rows) {
            Questionnaire questionnaire = new Questionnaire((String) row.get("questions"));

            questionnaires.add(questionnaire);
        }
        if (questionnaires.size() != 1) {
            throw new PfmException("Wrong questionnaire data.", PfmExceptionCode.QUESTIONNAIRE_API_ERROR);
        }
        Questionnaire questionnaire = questionnaires.get(0);
        QuestionnaireMapping questionnaireMapping;
        try {
            questionnaireMapping = new ObjectMapper().readValue(questionnaire.getQuestions(),
                QuestionnaireMapping.class);
        } catch (IOException e) {
            logger.debug("Questionnaire structure is wrong. {}", e.getMessage());
            throw new PfmException("Questionnaire structure is wrong.", PfmExceptionCode.QUESTIONNAIRE_API_ERROR);
        }
        questionnaireMapping.setCategoryDetails(null);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String questionString;
        try {
            questionString = ow.writeValueAsString(questionnaireMapping);
        } catch (JsonProcessingException e) {
            logger.debug("Exception when generating json string. {}", e.getMessage());
            throw new PfmException("Exception when generating json string.", PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
        }

        questionnaire.setQuestions(questionString);
        return questionnaire;
    }

    private void updateStatus(Status status) {
        jdbcTemplate.update("update questionnaire set status=?, updated_on=?, updated_by=? where status=?",
            status.name(), new Date(), SystemUser.CETERA.name(), Status.ACTIVE.name());
    }

    /**
     * questionnaire seed data generator
     * the code needs to be changes every time we have new questionnaire
     * @param dataSeededAsOfDate
     */
    @Override
    public void create(String dataSeededAsOfDate) {
        logger.debug("creating questionnaire seed data...");
        List<String> versions = Arrays.asList(dataSeededAsOfDate.split("\\s*,\\s*"));

        for (String version : versions) {
            List<Questionnaire> savedQuestionnaire = questionnaireRepository.findByVersion(version);
            if (savedQuestionnaire == null || savedQuestionnaire.size() == 0) {
                switch (version) {
                    case "20160404":
                        logger.debug("creating 20160404");
                        create20160404(version);
                        break;
                    case "20160526":
                        create20160526(version);
                        break;
                    default: throw new PfmException("no such seed data function available: " + version,
                        PfmExceptionCode.SEED_DATA_INVALID_VERSION);
                }
            }
        }
    }

    /**
     * Update Q9 and Q2 for both CIS and the other questionnaire
     * So DB will create two new records in questionnaire table
     * @param dataSeededAsOfDate
     */
    private void create20160526(String dataSeededAsOfDate) {
        String sql = "UPDATE questionnaire SET status = ?, updated_by=?, updated_on = ?";
        jdbcTemplate.update(sql, Status.INACTIVE.name(), SystemUser.CETERA.name(), new Date());
        String q2 = "What percentage of your qualified retirement plan business (401(k), etc.) " +
            "is purely fee based (not commission, 12b-1, group annuity commissions, etc), " +
            "and you currently acknowledge ERISA fiduciary status (using an ERISA plan consulting agreement)?";

        String q9 = "The DOL Rule will increase the complexity of your business and affect your ability to scale " +
            "operations. Which of the following positions do you currently have on staff to " +
            "support this increased complexity?";

        List<Questionnaire> qaList = questionnaireRepository.findByVersion("20160404");
        for (Questionnaire qa: qaList) {

            String question = qa.getQuestions();
            QuestionnaireMapping questionnaireMapping;
            try {
                questionnaireMapping = new ObjectMapper().readValue(question, QuestionnaireMapping.class);
            } catch (IOException  e) {
                logger.debug("question is: {}", question);
                logger.debug("JSON String is not valid. {}", e.getMessage());
                throw new PfmException("JSON String is not valid.", PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
            }
            questionnaireMapping.getCategoryDetails().get(0).getQuestionDetails().get(1).setDescription(q2);
            QuestionDetails qd9 = questionnaireMapping.getCategoryDetails().get(2).getQuestionDetails().get(0);
            qd9.setDescription(q9);
            qd9.setAnswerType(AnswerType.MULTI_SELECT.getContent());
            qd9.setAnswerOptions(new ArrayList<String>() {{
                add("None"); //0
                add("Administrative Assistant"); //1
                add("Licensed Sales Assistant"); //2
                add("Client Service Associate"); //3
                add("Operations Specialist"); //4
                add("Marketing Specialist"); //5
                add("Research Analyst"); //6
                add("Paraplanner"); //7
                add("Junior Advisor"); //8
            }}
            );
            final List<Integer> weight1 = new ArrayList<Integer>() {{
                add(1);
                add(1);
                add(0);
                add(0);
                add(4);
            }};
            final List<Integer> weight2 = new ArrayList<Integer>() {{
                add(1);
                add(2);
                add(1);
                add(8);
                add(4);
            }};
            final List<Integer> weight3 = new ArrayList<Integer>() {{
                add(2);
                add(8);
                add(1);
                add(8);
                add(6);
            }};

            List<List<Integer>> weight = new ArrayList<List<Integer>>() {{
                add(weight1);
                add(weight2);
                add(weight3);
            }};

            qd9.setWeight(weight);

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String newQuestion;
            try {
                newQuestion = ow.writeValueAsString(questionnaireMapping);
            } catch (JsonProcessingException e) {
                logger.debug("Exception when generating json string. {}", e.getMessage());
                throw new PfmException("Exception when generating json string.",
                    PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
            }

            //id = 1 is for all other advisors; new id should be 3
            //id = 2 for CIS advisors; new id should be 4
            if (qa.getId() == 1L) {
                Questionnaire newQuestionnaire = new Questionnaire(3L, QuestionnaireName.DOL.name(),
                    dataSeededAsOfDate, newQuestion);
                questionnaireRepository.save(newQuestionnaire);
            } else if (qa.getId() == 2L) {
                Questionnaire newQuestionnaire = new Questionnaire(4L, QuestionnaireName.DOL.name(),
                    dataSeededAsOfDate, newQuestion);
                questionnaireRepository.save(newQuestionnaire);
            }
        }
    }

    private void create20160404(String dataSeededAsOfDate) {
        List<Questionnaire> savedQuestionnaires = questionnaireRepository.findByVersion(dataSeededAsOfDate);
        if (savedQuestionnaires != null && savedQuestionnaires.size() >0 ) return;

        //update the active one to obsolete first
        updateStatus(Status.INACTIVE);

        //2. insert new one;
        int questionCount = 14;
        String[] cat = {
            "Business Development",
            "Operations",
            "Human Capital",
            "Business Management",
            "Succession Planning"
        };

        //seed data for questionnaire iQuantify
        String[][][] description = {
            //cat 0
            {
                //0
                {"What percentage of your revenue is fee-based?", "dropdown", "0-25%", "25-50%", "50-75%", "75-100%"},
                //1
                {"What percentage of your qualified retirement plan business (401k, etc.)" +
                    " is DOL Fiduciary compliant (level fee, fiduciary process, etc.)?",
                "dropdown", "0-25%", "25-50%", "50-100%", "I need help Understanding the new DOL fiduciary standard",
                    "I do not have any qualified retirement plans"},
                //2
                {"What percentage of your clients receive a formal financial plan developed " +
                    "utilizing comprehensive financial planning software?",
                    "dropdown", "0-25%", "25-50%", "50-75%", "75-100%"},
                //3
                {"How often do you use automated client marketing  platforms to communicate " +
                    "with your clients (client newsletter, campaigns, social media)?",
                    "dropdown", "Never", "Occasionally",
                    "Regularly - I use an automated marketing system with the majority of my clients",
                    "Always - I use an automated marketing system with all of my clients"},
            },
            //cat 1
            {
                //4
                {"Have you formally segmented all of your clients?",
                    "radio button", "Yes", "No"},
                //5
                {"Do you leverage a client service matrix with specific deliverables?",
                "dropdown", "No", "Informally for a few clients", "Only my top clients", "Formally for all clients"},
                //6
                {"Do you have workflows and checklists to assign tasks and efficiently manage business processes?",
                    "dropdown", "No", "Only for specific activities", "All processes are documented"},
                //7
                {"How often do you use a CRM system to document your client interactions?",
                    "dropdown", "Never", "Occasionally", "Frequently", "Always"}
            },
            //cat 2
            {
                //8
                {"Are you adequately staffed to support your business under the DOL Fiduciary rule?",
                "radio button", "Yes", "No"},
                //9
                {"Are you IAR licensed? (Series 65/66)",
                    "radio button", "Yes", "No"},
                //10
                {"Do you have any of the following Professional Designations?",
                    "multi select", "No Designations", "AIF", "CFP", "CWS", "CRC"}
            },
            //cat 3
            {
                //11
                {"Do you scale your investment management business by incorporating advisory Models?",
                    "dropdown", "No", "Yes, but only with specific clients",
                    "Yes, with the majority of my advisory accounts",
                    "Yes, all of my accounts are model-based"}
            },
            //cat 4
            {
                //12
                {"Would you consider teaming with another financial advisor to broaden capabilities and capitalize on DOL?",
                    "radio button", "Yes", "No"},
                //13
                {"Is DOL likely to accelerate your retirement as an advisor and result in your exit from the business?",
                    "radio button", "Yes", "No"}

            }
        };

        //for single select, every element is weight for a question
        //multi select have score in 5-tuple; others have single score in array
        //5 tuple is lower_count, upper_count, lower_index, upper_index, weight
        Integer[][][][] weights = {
            {
                {
                    {2}, {4}, {6}, {8}
                },
                {
                    {2}, {4}, {8}, {0}, {10}
                },
                {
                    {2}, {4}, {6}, {8}
                },
                {
                    {0}, {2}, {4}, {6}
                }

            },
            {
                {
                    {6} ,{0}
                },
                {
                    {0}, {2}, {4}, {6}
                },
                {
                    {0}, {3}, {6}
                },
                {
                    {0}, {2}, {4}, {6}
                }

            },
            {
                {
                    {6}, {4}
                },
                {
                    {12}, {0}
                },
                {
                    {1, 1, 0, 0, 0}, {1, 1, 1, 4, 4}, {2, 4, 1, 4, 6}
                }

            },
            {
                {
                    {0}, {2}, {4}, {8}
                }
            },
            {
                {
                    {4}, {6}
                },
                {
                    {0}, {6}
                }
            }
        };

        //firstly generate base questionnaire
        QuestionnaireMapping questionMapping = new QuestionnaireMapping(questionCount);

        int categoryCount= cat.length;
        for (int i = 0; i < categoryCount; i++) {
            //per cat
            CategoryDetails categoryDetails = new CategoryDetails(cat[i]);
            String[][] desDetail = description[i]; //per cat
            Integer[][][] scoreDetail = weights[i]; //per cat
            List<QuestionDetails> totalQuestionDetails = new ArrayList<QuestionDetails>();

            for (int j = 0; j < desDetail.length; j++) {
                //per question
                String answerType = desDetail[j][1];
                if (answerType.equals("multi select")) {
                    //weight is list of list
                    List<String> answerOptions = new ArrayList<String>();
                    List<List> totalWeight = new ArrayList<List>();

                    for (int m = 2; m < desDetail[j].length; m++) {
                        answerOptions.add(desDetail[j][m]);
                    }
                    int x = 0;
                    for (; x < scoreDetail[j].length; x++) {
                        List<Integer> weight = new ArrayList<Integer>();
                        //5 tuple
                        for (int n = 0; n < 5; n++) {
                            weight.add(scoreDetail[j][x][n]);
                        }
                        totalWeight.add(weight);
                    }
                    totalQuestionDetails.add(
                        new QuestionDetails(desDetail[j][0], desDetail[j][1], answerOptions, totalWeight));
                } else {
                    //weight is list
                    List<String> answerOptions = new ArrayList<String>();
                    List<Integer> weight = new ArrayList<Integer>();
                    int m = 2;
                    for (; m < desDetail[j].length; m++) {
                        answerOptions.add(desDetail[j][m]);
                        weight.add(scoreDetail[j][m-2][0]);
                    }
                    totalQuestionDetails.add(
                        new QuestionDetails(desDetail[j][0], desDetail[j][1], answerOptions, weight));
                }
            }
            categoryDetails.setQuestionDetails(totalQuestionDetails);
            questionMapping.getCategoryDetails().add(categoryDetails);
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String questions;
        try {
            questions = ow.writeValueAsString(questionMapping);
        } catch (JsonProcessingException e) {
            logger.debug("Exception when generating json string. {}", e.getMessage());
            throw new PfmException("Exception when generating json string.", PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
        }


        //external advisor and internal advisor except CIS questionnaire
        //should have id = 1
        Questionnaire questionnaire = new Questionnaire(1L, QuestionnaireName.DOL.name(), dataSeededAsOfDate, questions);
        questionnaireRepository.save(questionnaire);

        //generate qa for iQuantify-CIS
        //total number of questions are the same; but description for some question is different.
        Map<Integer, HashMap<Integer, String>> difference = new HashMap<>();
        difference.put(2, new HashMap<Integer, String>() {{
            put(0, "Is your Financial Institution adequately staffed to support your business under the DOL " +
                "Fiduciary rule?");
        }});
        difference.put(4, new HashMap<Integer, String>() {{
            put(0, "Would you consider teaming with another financial professional within your program to " +
                "broaden capabilities and capitalize on DOL?");
        }});
        for (Map.Entry<Integer, HashMap<Integer, String>> entry : difference.entrySet()) {
            Integer catNumber = entry.getKey();
            HashMap<Integer, String> values = entry.getValue();
            List<QuestionDetails> qd = questionMapping.getCategoryDetails().get(catNumber).getQuestionDetails();

            for (Map.Entry<Integer, String> entry1 :  values.entrySet()) {
                qd.get(entry1.getKey()).setDescription(entry1.getValue());
            }
        }

        String cisQuestions;
        try {
            cisQuestions = ow.writeValueAsString(questionMapping);
        } catch (JsonProcessingException e) {
            logger.debug("Exception when generating json string. {}", e.getMessage());
            throw new PfmException("Exception when generating json string.", PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
        }

        //should have id 2;
        Questionnaire cisQuestionnaire = new Questionnaire(2L, QuestionnaireName.DOL.name(), dataSeededAsOfDate, cisQuestions);
        questionnaireRepository.save(cisQuestionnaire);
    }


    /**
     * admin functions to manage questionnaire data
     */

    /**
     * get the questionnaire
     * @param id
     * @return
     */
    @Override
    public Questionnaire findOne(Long id) {
        return questionnaireRepository.findOne(id);
    }


    private Questionnaire update(Long id, String newQuestionnaire) {
        //1. validate questionnaireId
        Questionnaire questionnaire = findOne(id);
        if (questionnaire == null)
            throw new PfmException("Wrong Questionnaire ID : " + id, PfmExceptionCode.QUESTIONNAIRE_WRONG_ID);

        if (!questionnaire.getStatus().equals(Status.ACTIVE.name())) {
            throw new PfmException("Cannot edit INACTIVE questionnaire.", PfmExceptionCode.QUESTIONNAIRE_WRONG_ID);
        }

        //2. create the new questionnaire record.
        Questionnaire newQuestion = add(newQuestionnaire);

        //3. Setting the previous questionnaire record as INACTIVE
        questionnaire.setStatus(Status.INACTIVE.name());
        questionnaire.setUpdatedOn(new Date());
        questionnaire.setUpdatedBy(SystemUser.ADMIN.name());
        questionnaireRepository.save(questionnaire);

        List<BdQa> bdQas = bdQaRepository.findByQuestionnaireIdAndStatus(questionnaire.getId(), Status.ACTIVE.name());

        //Assumption: the number of entries marked as INACTIVE equals the number you will create new records
        //4. Updating BD_QA records to INACTIVE related to the old questionnaire.
        String sql = "UPDATE bd_qa set status=?, updated_by=?, updated_on=? where questionnaire_id=? AND status=?";

        jdbcTemplate.update(sql, Status.INACTIVE.name(), SystemUser.ADMIN.name(), new Date(),
            questionnaire.getId(), Status.ACTIVE.name());

        //5. insert new BD_QA mappings
        //get the largest id from BD_QA mapping
        sql = "SELECT MAX(id) FROM bd_qa";
        Long index = jdbcTemplate.queryForObject(sql, Long.class);
        Map<Long, Long> bdQaIdChanges = new HashMap<>();
        for (BdQa brokerQues : bdQas) {
            BdQa bdQa = new BdQa();
            bdQa.setStatus(Status.ACTIVE.name());
            Long oldId = brokerQues.getId();
            bdQa.setId(index + 1);
            bdQaIdChanges.put(oldId, bdQa.getId());
            bdQa.setVersion(new SimpleDateFormat("YYYYMMdd").format(new Date()));
            bdQa.setQuestionnaireId(newQuestion.getId());
            bdQa.setCreatedBy(SystemUser.ADMIN.name());
            bdQa.setCreatedOn(new Date());
            bdQa.setBdId(brokerQues.getBdId());
            bdQaRepository.save(bdQa);
            index++;
        }

        //6. update resources_qa mapping
        resourcesQaService.update(bdQaIdChanges);
        return newQuestion;
    }

    /**
     * update existing questionnaire (for all broker dealers with the same questionnaire)
     * stop webservice when managing database
     * @param questionFile
     * @return
     */
    @Override
    public Questionnaire update(Long id, MultipartFile questionFile) {
        //validate questionnaire file
        String newQuestionnaire = validateQaFile(questionFile);
        return update(id, newQuestionnaire);
    }

    private String validateQaFile(MultipartFile file) {
        String newQuestionnaire;
        try {
            newQuestionnaire = new String(file.getBytes());
        } catch (IOException e) {
            logger.debug("Cannot read the file. {}", e.getMessage());
            throw new PfmException("Cannot read the file. " + e.getMessage(), PfmExceptionCode.FILE_PROCESSING_ERROR);
        }

        newQuestionnaire = newQuestionnaire.trim();

        //1. validate ascii chars
        validateAsciiService.validateAscii(newQuestionnaire);

        //2. validate json structure
        QuestionnaireMapping questionnaireMapping;
        try {
            questionnaireMapping = new ObjectMapper().readValue(newQuestionnaire, QuestionnaireMapping.class);
        } catch (IOException e) {
            logger.debug("JSON File is not valid. {}", e.getMessage());
            throw new PfmException("JSON File is not valid.", PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
        }

        for (CategoryDetails cd : questionnaireMapping.getCategoryDetails()) {
            List<QuestionDetails> qdList = cd.getQuestionDetails();
            for (QuestionDetails qd: qdList) {
                String answerType = qd.getAnswerType();
                if (!AnswerType.contains(answerType)) {
                    throw new PfmException("Wrong answerType in new questionnaire: " + answerType
                        + ". AnswerType should be lowercase 'dropdown', 'radio button, or 'multi select'",
                        PfmExceptionCode.QUESTIONNAIRE_WRONG_ANSWER_TYPE);
                }
            }
        }
        return newQuestionnaire;
    }

    /**
     * add new questionnaire
     * externalBdId is the real broker dealer id in cetera's system;
     * it is not the primary key in IQuantify database Broker_dealer table.
     * If this is for external advisors, Admin should pass 0 as bdId
     * expand means apply this new questionnaire to all bd_id in the same group
     * @param externalBdId
     * @param questionFile
     * @param expand
     * @return
     */
    @Override
    public Questionnaire add(Integer externalBdId, MultipartFile questionFile, Boolean expand) {
        //1. check bd id exist
        if (externalBdId == 0) {
            externalBdId = null;
        }
        BrokerDealer bd = brokerDealerService.findByExternalId(externalBdId);
        if (bd == null) {
            throw new PfmException("Invalid broker dealer id.", PfmExceptionCode.BROKER_DEALER_INVALID_ID);
        }
        Long bdId = bd.getId();

        //2. validate questionnaire json file
        String newQuestionnaire = validateQaFile(questionFile);

        BdQa bdQa = bdQaRepository.findByBdIdAndStatus(bdId, Status.ACTIVE.name());
        if (bdQa == null) {
            /**
             * since this requires user to enter resources_qa mapping, we do not have that function yet
             * so disable this situation
             */
            throw new PfmException("We do not support adding questionnaire for a bd id " +
                "which does not have mapping in BD_QA table.");
            // if no questionnaire id bound to this bdid, (it is not happening in the current flow)
            // add questionnaire, and add bd_qa mapping

            //Questionnaire questionnaire = add(bdId, newQuestionnaire);

            //add resources_QA mapping; it will need user to enter all info in resources_qa table
            //we do not provide this function for now.

            //return questionnaire;
        } else {
            Long questionnaireId = bdQa.getQuestionnaireId();
            /**
             * if expand is true, using update function for this questionnaire
             * else only update for this bdId
             */
            if (expand) {
                return update(questionnaireId, newQuestionnaire);
            } else {
                /**
                 * do not expand to other bd_id using the same questionnaire
                 * 1. add new entry in questionnaire table;
                 * 2. add mapping
                 */
                Questionnaire qa = add(newQuestionnaire);
                BdQa newBdQa = addBdQaMapping(bdId, qa);

                // 3. check if there is any other bd_id using the old questionnaire,
                // if not, mark the old questionnaire as INACTIVE
                List<BdQa> bdQaList = bdQaRepository.findByQuestionnaireIdAndStatus(questionnaireId, Status.ACTIVE.name());
                if (bdQaList.size() == 1) {
                    //only this bd id is using this questionnaire
                    //mark that questionnaire as INACTIVE
                    String sql = "UPDATE questionnaire set status = ?, updated_by = ?, updated_on = ? WHERE id = ?";
                    jdbcTemplate.update(sql, Status.INACTIVE.name(), SystemUser.ADMIN.name(), new Date(), questionnaireId);
                }

                // 4. mark previous mapping as INACTIVE
                bdQa.setStatus(Status.INACTIVE.name());
                bdQa.setUpdatedBy(SystemUser.ADMIN.name());
                bdQa.setUpdatedOn(new Date());
                bdQaRepository.save(bdQa);

                //5. update resources_qa table
                Map<Long, Long> bdQaIdChanges = new HashMap<>();
                bdQaIdChanges.put(bdQa.getId(), newBdQa.getId());
                resourcesQaService.update(bdQaIdChanges);
                return qa;
            }
        }
    }

    private BdQa addBdQaMapping(Long bdId, Questionnaire newQa) {
        //add bd_qa mapping
        String sql = "SELECT MAX(id) FROM bd_qa";
        Long index = jdbcTemplate.queryForObject(sql, Long.class);
        BdQa bdQa = new BdQa();
        bdQa.setStatus(Status.ACTIVE.name());
        bdQa.setId(index + 1);
        bdQa.setVersion(new SimpleDateFormat("YYYYMMdd").format(new Date()));
        bdQa.setQuestionnaireId(newQa.getId());
        bdQa.setCreatedBy(SystemUser.ADMIN.name());
        bdQa.setCreatedOn(new Date());
        bdQa.setBdId(bdId);
        return bdQaRepository.save(bdQa);
    }

    private Questionnaire add(String newQuestionnaire) {
        String sql = "SELECT MAX(id) FROM questionnaire";
        Long index = jdbcTemplate.queryForObject(sql, Long.class);

        Questionnaire newQuestion = new Questionnaire(index + 1, QuestionnaireName.DOL.name(),
            new SimpleDateFormat("YYYYMMdd").format(new Date()), newQuestionnaire);
        newQuestion.setCreatedBy(SystemUser.ADMIN.name());
        return questionnaireRepository.save(newQuestion);
    }
}
