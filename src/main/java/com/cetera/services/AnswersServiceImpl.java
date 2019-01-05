package com.cetera.services;

import com.cetera.aspects.EmailUtils;
import com.cetera.dao.AnswersRepository;
import com.cetera.dao.IQuantifyRepository;
import com.cetera.domain.Answers;
import com.cetera.domain.CategoryDetails;
import com.cetera.domain.CurrentSession;
import com.cetera.domain.IQuantify;
import com.cetera.domain.JoinUtils;
import com.cetera.domain.Performances;
import com.cetera.domain.Person;
import com.cetera.domain.QuestionDetails;
import com.cetera.domain.Questionnaire;
import com.cetera.domain.QuestionnaireMapping;
import com.cetera.domain.ResourceDetail;
import com.cetera.domain.Sessions;
import com.cetera.enums.Status;
import com.cetera.enums.SystemUser;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import com.cetera.model.AnswersRequest;
import com.cetera.model.AnswersResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by danni on 3/23/16.
 */
@Service
public class AnswersServiceImpl implements AnswersService {
    private static Logger logger = LoggerFactory.getLogger(AnswersServiceImpl.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AnswersRepository answersRepository;

    @Autowired
    private PerformancesService performancesService;

    @Autowired
    private IQuantifyRepository iQuantifyRepository;

    @Autowired
    private CurrentSession currentSession;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private EmailUtils emailUtils;

    private static final int DIVISION_SCALE = 0;

    /**
     * retrieve survey results from current session
     * @return
     */
    @Override
    public AnswersResponse retrieve() {
        Sessions session = currentSession.getSession();
        if (session == null) {
            throw new PfmException("Missing session.", PfmExceptionCode.SESSION_INVALID);
        }
        return retrieve(session.getPersonId());
    }

    /**
     * retrieve survey results for a person
     * @param personId
     * @return
     */
    @Override
    public AnswersResponse retrieve(String personId) {
        logger.debug("RETRIEVE: inside retrieve results function...");
        sessionService.validateByPerson(personId);
        IQuantify iQuantify = iQuantifyRepository.findTop1ByPersonIdOrderByCreatedOnDesc(personId);

        /*
         * Returning null when there are no answer entries for a person so that
         * the function can be used also to find out whether the survey was taken
         * previously or not.
         */
        AnswersResponse answersResponse;
        if (iQuantify == null) {
            logger.debug("RETRIEVE: no iquantify found...");
            answersResponse = null; // Need not assign null again, but done just to emphasize.
        } else {
            logger.debug("RETRIEVE: finding the latest iQuantify id: {}", iQuantify.getId());

            answersResponse = new AnswersResponse(iQuantify.getiQuantify(), iQuantify.getPerformance());
            List<ResourceDetail> resources = calculateResources(iQuantify.getQuestionnaireId(), iQuantify.getId(),
                personService.findOne(personId).getBdId());
            logger.debug("RETRIEVE: resources is {}", resources);
            answersResponse.setAdvisorResources(resources);
        }
        return answersResponse;
    }

    /**
     * create new AnswersResponse after user submit questionnaire
     * @param answersRequest
     * @return
     */
    @Override
    public AnswersResponse create(AnswersRequest answersRequest) {
        logger.debug("CREATE: inside create function...");
        //1. validate person id
        String personId = answersRequest.getPersonId();
        if (personId == null) {
            throw new PfmException("Missing personId.", PfmExceptionCode.PERSON_MISSING_ID);
        }

        Person person = personService.findOne(personId);
        if (person == null) {
            throw new PfmException("Wrong personId", PfmExceptionCode.PERSON_INVALID_ID);
        }

        sessionService.validateByPerson(personId);

        //2. validate latest qa version
        String sql = "select a.id, a.questions from questionnaire a inner join bd_qa b " +
            "on b.questionnaire_id = a.id " +
            "inner join broker_dealer c on c.id = b.bd_id inner join person d on d.bd_id = c.id " +
            "where d.id = ? and a.status = ? and b.status = ?";
        logger.debug("CREATE: sql is {}", sql);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, personId, Status.ACTIVE.name(), Status.ACTIVE.name());

        List<Questionnaire>  qas = new ArrayList<>();
        for (Map<String, Object> row: rows) {
            qas.add(new Questionnaire(((BigDecimal) row.get("id")).longValue(), (String) row.get("questions")));
        }
        if (qas.size() != 1) {
            throw new PfmException("Wrong questionnaire seed data.", PfmExceptionCode.QUESTIONNAIRE_API_ERROR);
        }

        Questionnaire qa = qas.get(0);

        if (!qas.get(0).getId().equals(answersRequest.getQuestionnaireId())) {
            throw new PfmException("Please use the latest questionnaire.", PfmExceptionCode.QUESTIONNAIRE_WRONG_ID);
        }

        //3. logic to calculate performance, iQuantify and resources
        List answers = answersRequest.getAnswer();

        QuestionnaireMapping questions;
        try {
            questions = new ObjectMapper().readValue(qa.getQuestions(), QuestionnaireMapping.class);
        } catch (IOException e) {
            logger.debug("Questionnaire structure is wrong. {}", e.getMessage());
            throw new PfmException("Questionnaire structure is wrong.", PfmExceptionCode.QUESTIONNAIRE_API_ERROR);
        }

        IQuantify iQuantify = new IQuantify(answersRequest);

        List<Answers> newAnswers = calculateAnswers(questions, answers, iQuantify);

        iQuantifyRepository.save(iQuantify);

        for (Answers as: newAnswers) {
            as.setiQuantifyId(iQuantify.getId());
            as.setCreatedBy(personId);
        }
        answersRepository.save(newAnswers);

        AnswersResponse answersResponse = new AnswersResponse(iQuantify.getiQuantify(), iQuantify.getPerformance());
        List<ResourceDetail> resources = calculateResources(qa.getId(), iQuantify.getId(), person.getBdId());
        answersResponse.setAdvisorResources(resources);
        sessionService.updateSurveyStatus();
        emailUtils.mailAssessmentResults(person, newAnswers, iQuantify);
        return answersResponse;
    }

    /**
     * new functions to calculate resources based on weight
     */
    private List<ResourceDetail> calculateResources(Long questionId, Long iQuantifyId, Long bdId) {

        logger.debug("inside calculate resources function....");
        logger.debug("questionnaire id is {}", questionId);
        logger.debug("iQuantifyId is {}", iQuantifyId);
        logger.debug("bdid is {}", bdId);

        List<ResourceDetail> resources = new ArrayList<>();
        String sql = "SELECT A.LINKS FROM RESOURCES A INNER JOIN RESOURCES_QA B " +
            "ON A.ID = B.RES_ID INNER JOIN ANSWERS C ON C.QUESTION_ID = B.QUESTION_ID " +
            "AND C.WEIGHT BETWEEN B.LOWER_LIMIT " +
            "AND B.upper_limit INNER JOIN BD_QA BQ ON BQ.ID = B.BD_QA_ID " +
            "INNER JOIN BROKER_DEALER BD ON BD.ID = BQ.BD_ID " +
            "INNER JOIN QUESTIONNAIRE Q ON Q.ID = BQ.QUESTIONNAIRE_ID " +
            "WHERE Q.ID = ? AND BD.ID = ? AND C.I_QUANTIFY_ID = ? " +
            "ORDER BY b.ranking";
        logger.debug("calculate rsources sql is {}", sql);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, questionId, bdId, iQuantifyId);

        List<String> resourceLinks = new ArrayList<>();

        for (Map<String, Object> row: rows) {
            String resourceString = (String) row.get("LINKS");
            logger.debug("string is {}", resourceString);
            resourceLinks.add(resourceString);
        }

        logger.debug("resourceLinks is {}", resourceLinks);
        for (String resourceString: resourceLinks) {
            ResourceDetail resourceDetail;
            try {
                logger.debug("inside loop to get json object with string: {}", resourceString);
                resourceDetail = new ObjectMapper().readValue(resourceString, ResourceDetail.class);
                logger.debug("mapping to json successfully");
            } catch (IOException e) {
                logger.debug("Resource Details structure is wrong. {}", e.getMessage());
                throw new PfmException("Resource Details structure is wrong.", PfmExceptionCode.RESOURCE_API_ERROR);
            }

            resources.add(resourceDetail);
        }
        logger.debug("mapping to json without any problem");
        return resources;
    }

    /**
     * calculate iQuantify value and performance for the answer
     * @param qaMapping
     * @param answers array of indexes to answerOptions
     * @param iQuantify
     */
    private List<Answers> calculateAnswers(QuestionnaireMapping qaMapping, List answers, IQuantify iQuantify) {
        //1. validate answers count is correct
        int size = answers.size();
        if (size != qaMapping.getQuestionCount()) {
            throw new PfmException("Wrong answers count.", PfmExceptionCode.QUESTIONNAIRE_WRONG_ANSWER_COUNT);
        }

        //2. get questions
        List<CategoryDetails> qa = qaMapping.getCategoryDetails();

        int questionId = 0;
        int iq = 0;
        List<Answers> newAnswers = new ArrayList<>();

        for (CategoryDetails cd: qa) {

            List<QuestionDetails> qdList = cd.getQuestionDetails();
            for (QuestionDetails qd: qdList) {

                //multi select, parse array
                if (qd.getAnswerType().equals("multi select")) {

                    //answers.get(i) is like [1,2,3]
                    //validate answer should be array of indexes
                    if (!(answers.get(questionId) instanceof List)) {
                        throw new PfmException("Please send List for multi select.",
                            PfmExceptionCode.QUESTIONNAIRE_WRONG_ANSWER_TYPE);
                    }
                    //answer is like [1,2,3]
                    List answer = (List) answers.get(questionId); //one dimensional array

                    List weights = qd.getWeight();

                    boolean matched = false;
                    for (Object weight: weights) { //5 tuple list [1,1,1,4,6]
                        List weightList = (List) weight;
                        int ansSize = answer.size();
                        //if size is correct
                        boolean matchValue = false;

                        //check if number of answer indexes falling in this region [weightList.get(0), weightList.get(1)]
                        if (ansSize <= (Integer) weightList.get(1) && ansSize >= (Integer) weightList.get(0)) {
                            //check if value is in the range [weightList.get(2), weightList.get(3)]
                            for (Object eachAns: answer) {
                                if (!(eachAns instanceof Integer)) {
                                    throw new PfmException(
                                        "Answers should be list of integers for multiselect questions",
                                        PfmExceptionCode.QUESTIONNAIRE_WRONG_ANSWER_TYPE);
                                }

                                if (!((Integer) eachAns <= (Integer) weightList.get(3)
                                    && (Integer) eachAns >= (Integer) weightList.get(2))) {
                                    matchValue = false;
                                } else {
                                    matchValue = true;
                                }
                            }
                            if (matchValue) {
                                int wt = (Integer) weightList.get(4);
                                //add to total iQuantify
                                iq += wt;
                                //create Answer object
                                List<String> answerString = new ArrayList<>();
                                for (Object as: answer) {
                                    answerString.add(qd.getAnswerOptions().get((Integer) as));
                                }
                                //change it to string

                                Answers answers1 = new Answers(questionId, qd.getDescription(),
                                    JoinUtils.joins(answerString), wt);
                                newAnswers.add(answers1);
                                matched = true;
                                break;
                            }
                        }
                    }
                    if (!matched) {
                        throw new PfmException("Wrong answers for question #"+ questionId,
                            PfmExceptionCode.QUESTIONNAIRE_WRONG_ANSWER_TYPE);
                    }

                } else {
                    //other types except multiselect
                    if (!(answers.get(questionId) instanceof Integer)) {
                        throw new PfmException("Only numeric value acceptable.",
                            PfmExceptionCode.QUESTIONNAIRE_WRONG_ANSWER_TYPE);
                    }
                    int answer = (Integer) answers.get(questionId);

                    if (answer >= qd.getAnswerOptions().size() || answer < 0) {
                        throw new PfmException("Invalid answer for question: " + qd.getDescription(),
                            PfmExceptionCode.QUESTIONNAIRE_WRONG_ANSWER_TYPE);
                    }
                    int wt = (Integer) qd.getWeight().get(answer);
                    iq += wt;
                    Answers answers1 = new Answers(questionId, qd.getDescription(), qd.getAnswerOptions().get(answer), wt);
                    newAnswers.add(answers1);
                }
                questionId++;
            }
        }

        if (iq > 100) {
            throw new PfmException("Wrong calculation for iQuantify; should be 0-100.",
                PfmExceptionCode.QUESTIONNAIRE_API_ERROR);
        }

        iQuantify.setiQuantify(iq);
        iQuantify.setPerformance(calculatePerformance(iq));
        return newAnswers;
    }

    /**
     * calculate performance for a iQuantify value, and update performance table
     * @param iQuantify
     * @return
     */
    private BigDecimal calculatePerformance(Integer iQuantify) {
        List<Performances> performances = performancesService.findAll();
        if (performances.size()!= 1) {
            throw new PfmException("Wrong performance table data.", PfmExceptionCode.PERFORMANCES_API_ERROR);
        }

        Performances performance = performances.get(0);

        BigDecimal performanceValue = performance.getPerformance();
        Integer userCount = performance.getUserCount();
        BigDecimal userCountBig = new BigDecimal(userCount);
        Integer newUserCount = userCount + 1;

        BigDecimal newPerformance = performanceValue.multiply(userCountBig).add(new BigDecimal(iQuantify))
            .divide(new BigDecimal(newUserCount), DIVISION_SCALE,  BigDecimal.ROUND_HALF_EVEN);

        jdbcTemplate.update("update performances set user_count=?, performance=?, updated_by=?, updated_on=?" +
                " where user_count=? and performance =?",
            newUserCount, newPerformance, SystemUser.CETERA.name(), new Date(), userCount, performanceValue);

        return newPerformance;
    }
}
