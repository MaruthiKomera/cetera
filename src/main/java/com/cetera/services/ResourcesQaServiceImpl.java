package com.cetera.services;

import com.cetera.dao.ResourcesQaRepository;
import com.cetera.domain.ResourcesQa;
import com.cetera.enums.Status;
import com.cetera.enums.SystemUser;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by danni on 4/5/16.
 */
@Service
public class ResourcesQaServiceImpl implements ResourcesQaService {
    private static Logger logger = LoggerFactory.getLogger(ResourcesQaServiceImpl.class);

    @Autowired
    private ResourcesQaRepository resQaRepository;

    @Value("${broker.dealer.count}")
    private Integer bdCount;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    //bdQaId for CIS, FASI and NA
    private static final int CIS = 8;
    private static final int FASI = 3;
    private static final int NA = 1;

    private static final Map<String, Integer> NAME_ID_MAPPING = new HashMap<String, Integer>() {{
        put("NA", 0);
        put("FASI", 2);
        put("CIS", 3);
        put("A", 1);
    }};

    /**
     * id is 8 for Q9, we start from 0
     * 6 and 4 are upper limit, we check upper limit and know which resource id we should return
     */
    private static final Map<Integer, Integer> Q9_MAPPING = new HashMap<Integer, Integer>() {{
        put(6, 1);
        put(4, 0);
    }};

    @Override
    public void create(String dataSeededAsOfDate) {
        List<String> versions = Arrays.asList(dataSeededAsOfDate.split("\\s*,\\s*"));

        for (String version : versions) {
            List<ResourcesQa> savedResourcesQa = resQaRepository.findByVersion(version);
            if (savedResourcesQa == null || savedResourcesQa.size() == 0) {
                switch (version) {
                    case "20160401":
                        create20160401(version);
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

    private void create20160526(String dataSeededAsOfDate) {
        //1. marke all records as INACTIVE, since we changed both questionnaire
        String sql = "UPDATE resources_qa set status=?, updated_by=?, updated_on=?";
        jdbcTemplate.update(sql, Status.INACTIVE.name(), SystemUser.CETERA.name(), new Date());

        //2. insert data like create20160401
        generate(dataSeededAsOfDate, 13);

    }

    private void create20160401(String dataSeededAsOfDate) {
        generate(dataSeededAsOfDate, 1);
    }

    private void generate(String dataSeededAsOfDate, int startingIndex) {
        /**
         * CIS : bdQaId = 8; CIS has its own qa: 2, CIS has no resources for q14
         * all other bds have same qa : 1
         *
         * NA : bdQaId = 1
         * FASI: bdQaId = 3
         *
         * calculate resId from bdQaId
         */
        // {bdQaId, questionId, resId, ranking, lowerLimit, upperLimit}
        // for qa 1
        // {questionId, ranking, lowerLimit, upperLimit}
        Integer[][] rankingAndLimit1 = {
            {0, 2, 2, 4},
            {1, 3, 0, 4},
            {2, 4, 2, 6},
            {3, 10, 0, 2},
            {4, 7, 0, 0},
            {5, 8, 0, 4},
            {6, 9, 0, 3},
            {7, 6, 0, 2},
            {8, 5, 6, 6},
            {8, 5, 4, 4},
            {9, 1, 0, 0},
            {10, 11, 0, 0},
            {12, 12, 4, 4},
            {13, 13, 0, 0}
        };

        // for qa 2
        // {questionId, ranking, lowerLimit, upperLimit}
        Integer[][] rankingAndLimit2 = {
            {0, 2, 2, 4},
            {1, 3, 0, 4},
            {2, 4, 2, 6},
            {3, 10, 0, 2},
            {4, 7, 0, 0},
            {5, 8, 0, 4},
            {6, 9, 0, 3},
            {7, 6, 0, 2},
            {8, 5, 6, 6},
            {8, 5, 4, 4},
            {9, 1, 0, 0},
            {10, 11, 0, 0},
            {12, 12, 4, 4}
        };

        String nameKey;
        //12 entries in bd_qa table
        for (int bdQaId = startingIndex; bdQaId <= startingIndex + 11; bdQaId++) {
            /**
             * parameter to ResourcesQa constructor
             * {Long bdQaId, Integer questionId, Long resId, Integer ranking, Integer lowerLimit,
             * Integer upperLimit, String version}
             */
            if (bdQaId == CIS + startingIndex - 1) {
                nameKey = "CIS";
                for (Integer[] resForEachQ: rankingAndLimit2) {
                    ResourcesQa resQa = new ResourcesQa((long) bdQaId, resForEachQ[0],
                        getResourceId(nameKey, resForEachQ[0], resForEachQ[3]),
                        resForEachQ[1], resForEachQ[2], resForEachQ[3], dataSeededAsOfDate);
                    resQaRepository.save(resQa);
                }
            } else {
                if (bdQaId == FASI + startingIndex - 1) {
                    nameKey = "FASI";
                } else if (bdQaId == NA + startingIndex - 1) {
                    nameKey = "NA";
                } else {
                    nameKey = "A";
                }

                for (Integer[] resForEachQ: rankingAndLimit1) {
                    ResourcesQa resQa = new ResourcesQa((long) bdQaId, resForEachQ[0],
                        getResourceId(nameKey, resForEachQ[0], resForEachQ[3]),
                        resForEachQ[1], resForEachQ[2], resForEachQ[3], dataSeededAsOfDate);
                    resQaRepository.save(resQa);
                }
            }
        }
    }

    /**
     * calculate resourceId from questionId and bd info
     * similar to seed data inside ResourceService
     * @param nameKey
     * @param qId
     * @param upperLimit
     * @return
     */
    private Long getResourceId(String nameKey, int qId, int upperLimit) {
        int resourceId;
        if (qId < 8) {
            resourceId = (qId) * 4 + NAME_ID_MAPPING.get(nameKey);
        } else if (qId == 8) {
            resourceId = (qId + Q9_MAPPING.get(upperLimit)) * 4 + NAME_ID_MAPPING.get(nameKey);
        } else {
            resourceId = (qId + 1) * 4 + NAME_ID_MAPPING.get(nameKey);
        }
        return (long) resourceId;
    }

    @Override
    public void update(Map<Long, Long> bdQaIdChanges) {
        Set<Long> oldBdQaIds = bdQaIdChanges.keySet();

        List<ResourcesQa> resourcesQaList = resQaRepository.findByBdQaIdInAndStatus(oldBdQaIds, Status.ACTIVE.name());
        List<Long> ids = new ArrayList<>();
        for (ResourcesQa resourcesQa :  resourcesQaList) {
            ids.add(resourcesQa.getId());
        }

        for (ResourcesQa rq: resourcesQaList) {
            ResourcesQa newRq = new ResourcesQa(rq);
            newRq.setBdQaId(bdQaIdChanges.get(rq.getBdQaId()));
            resQaRepository.save(newRq);

            rq.setStatus(Status.INACTIVE.name());
            rq.setUpdatedBy(SystemUser.ADMIN.name());
            rq.setUpdatedOn(new Date());
            resQaRepository.save(rq);
        }
    }


}
