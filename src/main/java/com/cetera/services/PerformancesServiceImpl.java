package com.cetera.services;

import com.cetera.dao.PerformancesRepository;
import com.cetera.domain.Performances;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by danni on 3/29/16.
 */
@Service
public class PerformancesServiceImpl implements PerformancesService {

    @Autowired
    private PerformancesRepository performancesRepository;

    /**
     * seed data generator
     */
    @Override
    public void create() {
        List<Performances> performances = performancesRepository.findAll();
        if (performances != null && performances.size() > 0) {
            return;
        }
        Performances newPerformances = new Performances(1L, 20, new BigDecimal(60));
        performancesRepository.save(newPerformances);
    }

    @Override
    public List<Performances> findAll() {
        return performancesRepository.findAll();
    }
}
