package com.cetera.dao;

import com.cetera.domain.Performances;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for Performances table
 * Created by danni on 3/29/16.
 */
public interface PerformancesRepository extends PagingAndSortingRepository<Performances, Long> {
    List<Performances> findAll();
}
