package com.cetera.services;

import com.cetera.dao.ResourcesRepository;
import com.cetera.domain.BrokerDealer;
import com.cetera.domain.CurrentSession;
import com.cetera.domain.Person;
import com.cetera.domain.ResourceDetail;
import com.cetera.domain.ResourceItem;
import com.cetera.domain.ResourceStep;
import com.cetera.domain.Resources;
import com.cetera.domain.Sessions;
import com.cetera.enums.ResourceType;
import com.cetera.enums.Status;
import com.cetera.enums.SystemUser;
import com.cetera.enums.YesOrNo;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import com.cetera.model.SsoRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by danni on 4/5/16.
 */
@Service
public class ResourcesServiceImpl implements ResourcesService {
    private static Logger logger = LoggerFactory.getLogger(ResourcesServiceImpl.class);

    @Autowired
    private ResourcesRepository resRepository;

    @Value("${idp.cetera}")
    private String idp;

    @Value("${bd.code.url1}")
    private String codeUrl1;

    @Value("${bd.code.url2}")
    private String codeUrl2;

    @Value("${resources.url}")
    private String resourceUrl;

    @Value("${resources.url.path}")
    private String resourceUrlPath;

    @Value("${sso.post.url}")
    private String url;

    @Value("${sso.expire.time.ticks}")
    private Long ssoExpireTime;

    @Value("${epoch.ticks}")
    private Long epochTicks;

    @Autowired
    private AsciiValidationService validateAsciiService;

    @Autowired
    private CurrentSession currentSession;

    @Autowired
    private PersonService personService;

    @Autowired
    private BrokerDealerService brokerDealerService;

    @Autowired
    private HmacSha256Service hmacSha256Service;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * create seed data for the first time
     * keep adding stuff to res table, no need to update status
     * @param dataSeededAsOfDate
     */
    @Override
    public void create(String dataSeededAsOfDate) {
        List<String> versions = Arrays.asList(dataSeededAsOfDate.split("\\s*,\\s*"));

        for (String version : versions) {
            if (version.equals("20160526")) {
                List<Resources> savedResources = resRepository.findByVersion(version);
                if (savedResources == null || savedResources.size() == 0) {
                    create20160526(version);
                }
            }
        }
    }

    /**
     * need to update resources in place:
     * Q3-FASI
     * Q4-A, Q4-FASI, Q4-CIS
     * Q5-A, Q5-FASI, Q5-CIS
     * Q6-A, Q6-FASI, Q6-CIS
     * Q7-A, Q7-CIS,
     * Q8-A, Q8-FASI, Q8-CIS
     * Q9-A, Q9-FASI,
     * Q10-A, Q10-FASI, Q10-CIS
     */
    private void create20160526(String newVersion) {
        List<Resources> resourcesList = resRepository.findByVersion("20160402");
        if (resourcesList == null || resourcesList.size() == 0) {
            create20160402();
        }
        resourcesList = resRepository.findByVersion("20160402");
        for (Resources res: resourcesList) {
            String description = res.getDescription();
            String links = res.getLinks();
            ResourceDetail resourceDetail;
            try {
                resourceDetail = new ObjectMapper().readValue(links, ResourceDetail.class);
            } catch (IOException e) {
                throw new PfmException("Resource Details structure is wrong.", PfmExceptionCode.RESOURCE_API_ERROR);
            }

            List<ResourceStep> resourceSteps = resourceDetail.getResourceStepList();

            boolean update = false;
            switch(description) {
                case "Q3-FASI":
                    ResourceItem resourceItem = resourceSteps.get(0).getResourceItems().remove(3);
                    resourceSteps.get(3).getResourceItems().add(resourceItem);
                    update = true;
                    break;
                case "Q4-A":
                case "Q4-FASI":
                case "Q4-CIS":
                    resourceSteps.get(1).getResourceItems().get(0).setType(ResourceType.VIDEO.getContent());
                    resourceSteps.get(2).getResourceItems().get(0).setType(ResourceType.VIDEO.getContent());
                    resourceSteps.get(3).getResourceItems().get(1).setType(ResourceType.PDF.getContent());
                    update = true;
                    break;
                case "Q5-A":
                case "Q5-FASI":
                case "Q5-CIS":
                    resourceSteps.get(2).getResourceItems().get(0).setType(ResourceType.WEB.getContent());
                    resourceSteps.get(2).getResourceItems().get(0).setSso("yes");
                    resourceSteps.get(2).getResourceItems().get(0).setUrl("~/pentameter/file/client-segmentation-tool");
                    resourceSteps.get(2).getResourceItems().get(1).setType(ResourceType.PDF.getContent());
                    update = true;
                    break;
                case "Q6-A":
                case "Q6-FASI":
                case "Q6-CIS":
                    resourceSteps.get(3).getResourceItems().get(0).setType(ResourceType.PDF.getContent());
                    resourceSteps.get(5).getResourceItems().get(0).setUrl("~/pentameter/contact");
                    update = true;
                    break;
                case "Q7-A":
                case "Q7-CIS":
                    resourceSteps.get(4).getResourceItems().get(0).setUrl("~/pentameter/contact");
                    update = true;
                    break;
                case "Q8-A":
                case "Q8-FASI":
                case "Q8-CIS":
                    resourceSteps.get(2).getResourceItems().get(2).setType(ResourceType.EXCEL.getContent());
                    resourceSteps.get(3).getResourceItems().get(0).setUrl("~/pentameter/contact");
                    update = true;
                    break;
                case "Q9-FASI-NO":
                    resourceSteps.get(5).getResourceItems().get(1).setUrl("http://sp.calipercorp.com/cfg/");
                    update = true;
                    break;
                case "Q10-A":
                case "Q10-FASI":
                case "Q10-CIS":
                    resourceSteps.get(0).getResourceItems().get(0).setUrl("https://myceterasmartworks.com/Public/Portal/Content.aspx?ContentId=29922210");
                    resourceSteps.get(0).getResourceItems().get(0).setType(ResourceType.VIDEO.getContent());
                    resourceSteps.get(0).getResourceItems().get(0).setDescription("Watch: Benefits of Becoming an IAR");
                    update = true;
                    break;
                default: break;
            }

            if (update) {
                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String newLinks;
                try {
                    newLinks = ow.writeValueAsString(resourceDetail);
                } catch (JsonProcessingException e) {
                    logger.debug("Exception when generating json string. {}", e.getMessage());
                    throw new PfmException("Exception when generating json string.",
                        PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
                }
                res.setLinks(newLinks);
                res.setUpdatedBy(SystemUser.CETERA.name());
                res.setUpdatedOn(new Date());
                res.setVersion(newVersion);
                resRepository.save(res);
            }
        }
    }

    private void create20160402() {
        String version = "20160402";
        List<Resources> savedRes = resRepository.findByVersion(version);
        if (savedRes != null && savedRes.size() > 0) {
            return;
        }

        Map<String, Integer> nameIdMapping = new HashMap<String, Integer>() {{
            put("NA", 0);
            put("A", 1);
            put("FASI", 2);
            put("CIS", 3);
        }};

        Map<String, Integer> Q9Mapping = new HashMap<String, Integer>() {{
            put("YES", 1);
            put("NO", 0);
        }};

        String pattern = "classpath:links/*.json";

        try {
            Resource[] resources = ResourcePatternUtils.getResourcePatternResolver(resourceLoader).getResources(pattern);
            for (Resource resource: resources) {
                InputStream resourceInputStream = resource.getInputStream();
                String fileName = resource.getFilename().replace(".json", "");
                String parseFileName = fileName.replace("Q", "");
                String links = StreamUtils.copyToString(resourceInputStream, Charset.forName("UTF-8"));

                //at most contains three parts:
                //0 question id
                //1 bd type
                //2 for q9 only
                String[] nameContents = parseFileName.split("-");
                int qId = Integer.parseInt(nameContents[0]);
                int resourceId;
                if (qId < 9) {
                    resourceId = (qId - 1) * 4 + nameIdMapping.get(nameContents[1]);
                } else if (qId == 9) {
                    resourceId = (qId - 1 + Q9Mapping.get(nameContents[2])) * 4 + nameIdMapping.get(nameContents[1]);
                } else {
                    resourceId = (qId) * 4 + nameIdMapping.get(nameContents[1]);
                }

                Resources res = new Resources((long) resourceId, links, fileName, version);
                resRepository.save(res);
            }
        } catch (IOException e) {
            logger.debug("Exception when create seed data for resources table. {}", e.getMessage());
            throw new PfmException("Cannot create seeddata for resources table.", PfmExceptionCode.SEEDDATA_API_ERROR);
        }
    }

    /**
     * Get form value for SSO links
     * @param target
     * @return SsoRequest
     */
    @Override
    public SsoRequest getSsoValue(String target) {

        Sessions session = currentSession.getSession();
        Person person = personService.findOne(session.getPersonId());
        if (person == null)
            throw new PfmException("Cannot find such person info.", PfmExceptionCode.SESSION_INVALID);
        if (person.getIsInternal().equals(YesOrNo.N.name())) {
            //not an internal user
            throw new PfmException("Not an internal advisor.", PfmExceptionCode.SESSION_INVALID);
        }

        BrokerDealer bd = brokerDealerService.findOne(person.getBdId());

        String code = getCode(bd, person);
        String targetUrl = getTarget(target);
        Long timestamp = getTimeStampForSso();
        String hValue = getHValue(idp, code, person.getId(), timestamp, targetUrl);

        return new SsoRequest(idp, person.getId(), code, timestamp, hValue, targetUrl, url);
    }

    private String getCode(BrokerDealer bd, Person person) {
        if (bd.getDomainName() == null) {
            throw new PfmException("There is no domain name available for this broker dealer id.",
                PfmExceptionCode.SSO_RESOURCES_UNAVAILABLE);
        }
        String code = codeUrl1 + "=" + bd.getDomainName() + "&";
        code += codeUrl2 + "=" + person.getId();
        return Base64.getEncoder().encodeToString(code.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * return 2 mins after current time
     * @return
     */
    private Long getTimeStampForSso() {
        return System.currentTimeMillis() * 10000L + ssoExpireTime + epochTicks;
    }

    private String getTarget(String target) {
        return resourceUrl + resourceUrlPath + "=" + target;
    }

    private String getHValue(String idp, String code, String nameId, Long timestamp, String target) {

        String tempHValue = "idp=" + idp + "&" + "code=" + code + "&" + "nameid=" + nameId + "&" + "timestamp="
            + timestamp + "&" + "target=" + target;
        return hmacSha256Service.hash(tempHValue);
    }

    /**
     * Admin function to manage resource data
     */

    /**
     * add resources, not in use now
     * @param resources
     * @return
     */
    @Override
    public Resources add(Resources resources) {
        //todo will add this function in the tool if needed
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Resources> getAll() {
        String sql =
            "SELECT * FROM resources WHERE id IN (SELECT DISTINCT res_id FROM resources_qa " +
                " WHERE status = 'ACTIVE') ORDER BY to_number(regexp_substr(description,'[0-9]+'))";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(Resources.class));
    }

    @Override
    public Resources findOne(Long id) {
        return resRepository.findOne(id);
    }

    /**
     * update resource links
     * @param id
     * @param resourceFile
     * @return
     */
    @Override
    public Resources update(Long id, MultipartFile resourceFile) {
        String newResourceLink;

        try {
            newResourceLink = new String(resourceFile.getBytes());
        } catch (IOException e) {
            logger.debug("Cannot read the file. {}", e.getMessage());
            throw new PfmException("Cannot read the file. " + e.getMessage(), PfmExceptionCode.FILE_PROCESSING_ERROR);
        }

        newResourceLink = newResourceLink.trim();
        //1. validate ascii chars
        validateAsciiService.validateAscii(newResourceLink);

        //2. validate json structure
        ResourceDetail resourceDetail;
        try {
            resourceDetail = new ObjectMapper().readValue(newResourceLink, ResourceDetail.class);
        } catch (IOException  e) {
            logger.debug("JSON File is not valid. {}", e.getMessage());
            throw new PfmException("JSON File is not valid.", PfmExceptionCode.JSON_PROCESSING_EXCEPTION);
        }

        for (ResourceStep rs: resourceDetail.getResourceStepList()) {
            for (ResourceItem resourceItem: rs.getResourceItems()) {
                if (resourceItem.getType() != null && !ResourceType.contains(resourceItem.getType())) {
                    throw new PfmException("Type is wrong: " + resourceItem.getType()
                        + ". Type should be lowercase 'pdf', 'ical', 'email', 'phone', 'video', 'web', 'excel', or 'word'.",
                        PfmExceptionCode.RESOURCE_WRONG_TYPE);
                }
                String sso = resourceItem.getSso();
                if (sso != null && !sso.equals("yes") && !sso.equals("no")) {
                    throw new PfmException("sso field should be lowercase 'yes' or 'no'.",
                        PfmExceptionCode.RESOURCE_WRONG_SSO);
                }
            }
        }


        //3. validate id, no need to check status in resource_qa table
        Resources resource = resRepository.findOne(id);
        if(resource == null)
            throw new PfmException("Wrong Resource ID: " + id, PfmExceptionCode.RESOURCE_INVALID_ID);

        //4. save new link
        resource.setLinks(newResourceLink);
        resource.setUpdatedBy(SystemUser.ADMIN.name());
        resource.setUpdatedOn(new Date());
        
        return resRepository.save(resource);
    }

    /**
     * delete resource: make INACTIVE inside mapping table
     * @param id
     * @return
     */
    @Override
    public void delete(Long id) {
        String sql = "UPDATE resources_qa SET status = ?, updated_by=?, updated_on=? WHERE res_id =  ?";
        jdbcTemplate.update(sql, Status.INACTIVE.name(), SystemUser.ADMIN.name(), new Date(), id);
    }
}