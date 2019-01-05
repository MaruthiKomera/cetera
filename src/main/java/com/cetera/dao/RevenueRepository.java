package com.cetera.dao;

import com.cetera.domain.Revenue;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for Revenue table
 * Created by danni on 3/23/16.
 */
public interface RevenueRepository extends PagingAndSortingRepository<Revenue, Long> {
}
