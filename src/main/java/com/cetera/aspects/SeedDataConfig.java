package com.cetera.aspects;

import com.cetera.services.BdQaService;
import com.cetera.services.BrokerDealerService;
import com.cetera.services.PerformancesService;
import com.cetera.services.QuestionnaireService;
import com.cetera.services.ResourcesQaService;
import com.cetera.services.ResourcesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by danni on 3/23/16.
 */
@Component
public class SeedDataConfig extends OncePerRequestFilter {

    private static Logger logger = LoggerFactory.getLogger(SeedDataConfig.class);

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private PerformancesService performancesService;

    @Autowired
    private ResourcesQaService resQaService;

    @Autowired
    private BrokerDealerService brokerDealerService;

    @Autowired
    private BdQaService bdQaService;

    @Autowired
    private ResourcesService resService;

    @Value("${data.questionnaire.seeded.as.of.date}")
    private String dataQuestionnaireSeededAsOfDate;

    @Value("${data.res.seeded.as.of.date}")
    private String dataResSeededAsOfDate;

    @Value("${data.resqa.seeded.as.of.date}")
    private String dataResQaSeededAsOfDate;

    @Value("${data.broker.dealer.seeded.as.of.date}")
    private String dataBDSeededAsOfDate;

    @Value("${data.bd.qa.seeded.as.of.date}")
    private String dataBdQaSeededAsOfDate;

    private boolean questionnaireProcessed;
    private boolean performanceProcessed;
    private boolean resProcessed;
    private boolean bdProcessed;
    private boolean bdQaProcessed;
    private boolean resQaProcessed;


    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/api")) {
            //sequence does matter!
            //bd table has to be process first
            //questionnaire has to be process before resources table!

            if (!bdProcessed) {
                logger.debug("checking for active seed bd data");
                brokerDealerService.create(dataBDSeededAsOfDate);
                bdProcessed = true;
            }

            if (!questionnaireProcessed) {
                logger.debug("checking for active seed questionnaire data");
                questionnaireService.create(dataQuestionnaireSeededAsOfDate);
                questionnaireProcessed = true;
                logger.debug("update questionnaire successfully!");
            }

            if (!bdQaProcessed) {
                logger.debug("checking for active seed bd qa data");
                bdQaService.create(dataBdQaSeededAsOfDate);
                bdQaProcessed = true;
                logger.debug("update bdqa successfully!");
            }

            if (!performanceProcessed) {
                logger.debug("checking for active seed performances data");
                performancesService.create();
                performanceProcessed = true;
                logger.debug("update performance successfully");
            }

            if (!resProcessed) {
                logger.debug("checking for active seed res data");
                resService.create(dataResSeededAsOfDate);
                resProcessed = true;
                logger.debug("update resources successfully!");
            }

            if (!resQaProcessed) {
                logger.debug("checking for active seed res qa data");
                resQaService.create(dataResQaSeededAsOfDate);
                resQaProcessed = true;
                logger.debug("update res qa successfully!");
            }
        }

        filterChain.doFilter(request, response);
    }
}

