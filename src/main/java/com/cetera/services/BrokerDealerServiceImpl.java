package com.cetera.services;

import com.cetera.dao.BrokerDealerRepository;
import com.cetera.domain.BrokerDealer;
import com.cetera.enums.SystemUser;
import com.cetera.exceptions.PfmException;
import com.cetera.exceptions.PfmExceptionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by danni on 4/10/16.
 */
@Service
public class BrokerDealerServiceImpl implements BrokerDealerService {

    @Value("${broker.dealer.count}")
    private Integer bdCount;

    @Autowired
    private BrokerDealerRepository brokerDealerRepository;

    /**
     * dataSeededAsOfDate can never be null, means versions contain at least one element
     * @param dataSeededAsOfDate
     */
    @Override
    public void create(String dataSeededAsOfDate) {
        List<String> versions = Arrays.asList(dataSeededAsOfDate.split("\\s*,\\s*"));

        for (String version : versions) {
            List<BrokerDealer> savedBrokerDealers = brokerDealerRepository.findByVersion(version);
            if (savedBrokerDealers == null || savedBrokerDealers.size() == 0) {
                switch (version) {
                    case "20160406":
                        create20160406(version);
                        break;
                    default: throw new PfmException("no such seed data function available: " + version,
                        PfmExceptionCode.SEED_DATA_INVALID_VERSION);
                }
            }
        }
    }

    private void create20160406(String version) {

        String[] name = {
            "Cetera Advisor Networks",
            "First Allied Securities",
            "Legend",
            "Cetera Advisors",
            "Investors Capital",
            "Summit",
            "Cetera Financial Institutions",
            "Cetera Financial Specialists",
            null,
            "Girard",
            "VSR"
        };

        String[] domainName = {
            "ceteraadvisornetworks.com",
            "firstallied.com",
            "legendgroup.com",
            "ceteraadvisors.com",
            "investorscapital.com",
            "summitbrokerage.com",
            "ceterainvestmentservices.com",
            "ceterafinancialspecialists.com",
            null,
            "girardsecurities.com",
            "vsrfs.com"
        };

        /**
         * id = 1 for external advisors
         */
        BrokerDealer bd = new BrokerDealer();
        bd.setId(1L);
        bd.setCreatedOn(new Date());
        bd.setCreatedBy(SystemUser.CETERA.name());
        bd.setVersion(version);
        brokerDealerRepository.save(bd);

        //internal broker dealer id 2-12
        //for internal broker dealer 11 (VSR, BD_ID as 12), use profile_id, all others use profileid
        for (int i = 0; i < bdCount; i++) {
            bd = new BrokerDealer(i + 2L, i + 1, name[i], domainName[i], version);
            brokerDealerRepository.save(bd);
        }
    }

    @Override
    public BrokerDealer findByExternalId(Integer externalId) {
        return brokerDealerRepository.findByExternalId(externalId);
    }

    @Override
    public BrokerDealer findOne(Long id) {
        return brokerDealerRepository.findOne(id);
    }
}
