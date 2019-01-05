package com.cetera.dao;

import com.cetera.domain.Resources;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for Resources table
 * Created by danni on 4/5/16.
 */
public interface ResourcesRepository extends PagingAndSortingRepository<Resources, Long> {
    List<Resources> findByVersion(String version);
}
