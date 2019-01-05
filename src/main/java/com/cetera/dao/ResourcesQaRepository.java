package com.cetera.dao;

import com.cetera.domain.ResourcesQa;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Set;

/**
 * Repository for Resources_Qa table
 * Created by danni on 4/5/16.
 */
public interface ResourcesQaRepository extends PagingAndSortingRepository<ResourcesQa, Long> {
    List<ResourcesQa> findByVersion(String version);
    List<ResourcesQa> findByBdQaIdInAndStatus(Set<Long> bdQaIdList, String status);
}
