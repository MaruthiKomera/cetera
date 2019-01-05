package com.cetera.services;

import com.cetera.domain.Performances;

import java.util.List;

/**
 * This service is used to create performance seed data and retrieve performance data
 * Created by danni on 3/29/16.
 */
public interface PerformancesService {
    void create();
    List<Performances> findAll();
}
