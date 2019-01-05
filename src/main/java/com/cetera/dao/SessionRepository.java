package com.cetera.dao;

import com.cetera.domain.Sessions;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for Session table
 * @author sahilshah
 */
public interface SessionRepository extends PagingAndSortingRepository<Sessions, String> {}
