package com.cetera.dao;

import com.cetera.domain.BdQa;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for Bd_Qa table
 * Created by danni on 4/10/16.
 */
public interface BdQaRepository extends PagingAndSortingRepository<BdQa, Long> {
    List<BdQa> findByVersion(String version);
    List<BdQa> findByQuestionnaireIdAndStatus(Long id, String status);
    BdQa findByBdIdAndStatus(Long bdId, String status);
}
