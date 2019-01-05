package com.cetera.services;

import com.cetera.dao.BdQaRepository;
import com.cetera.domain.BdQa;
import com.cetera.enums.Status;
import com.cetera.enums.SystemUser;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by danni on 4/10/16.
 */
@Service
public class BdQaServiceImpl implements BdQaService {
    private static Logger logger = LoggerFactory.getLogger(BdQaServiceImpl.class);

    @Autowired
    private BdQaRepository bdQaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void create(String dataSeededAsOfDate) {
        List<String> versions = Arrays.asList(dataSeededAsOfDate.split("\\s*,\\s*"));

        for (String version : versions) {
            List<BdQa> savedBdQas = bdQaRepository.findByVersion(version);
            if (savedBdQas == null || savedBdQas.size() == 0) {
                switch (version) {
                    case "20160406":
                        create20160406(version);
                        break;
                    case "20160526":
                        create20160526(version);
                        break;
                    default:
                        throw new PfmException("no such seed data function available: " + version,
                            PfmExceptionCode.SEED_DATA_INVALID_VERSION);
                }
            }
        }
    }

    /**
     * The first seed data
     * @param dataSeededAsOfDate
     */
    private void create20160406(String dataSeededAsOfDate) {
        //currently we only have two questionnaire, one for internal and the other for external advisors

        //bd id = 8 means CIS for internal advisors
        //bd qa id = 2
        //all others should use qa id = 1

        BdQa bdQa;
        for (long i = 1L; i <= 12L; i++) {
            if (i == 8L) {
                bdQa = new BdQa(i, i, 2L, dataSeededAsOfDate, Status.ACTIVE.name());
                bdQaRepository.save(bdQa);
            } else {
                bdQa = new BdQa(i, i, 1L, dataSeededAsOfDate, Status.ACTIVE.name());
                bdQaRepository.save(bdQa);
            }

        }
    }

    private void create20160526(String dataSeededAsOfDate) {

        //1. mark all previous records as inactive since both questionnaire are changed
        String sql = "UPDATE bd_qa SET status=?, updated_by=?, updated_on=? where version='20160406'";
        jdbcTemplate.update(sql, Status.INACTIVE.name(), SystemUser.CETERA.name(), new Date());

        //2. repeat the step in function create20160406
        BdQa bdQa;
        for (long i = 13L; i <= 24L; i++) {
            if (i == 20L) {
                bdQa = new BdQa(i, i - 12L, 4L, dataSeededAsOfDate, Status.ACTIVE.name());
                bdQaRepository.save(bdQa);
            } else {
                //logger.debug("id is {}", i);
                //logger.debug("bdid is {}", i - 12L);
                bdQa = new BdQa(i, i - 12L, 3L, dataSeededAsOfDate, Status.ACTIVE.name());
                bdQaRepository.save(bdQa);
            }

        }

    }
}
