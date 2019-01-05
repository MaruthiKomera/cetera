package com.cetera.dao;

import com.cetera.domain.IQuantify;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for I_Quantify table
 * Created by danni on 4/6/16.
 */
public interface IQuantifyRepository extends PagingAndSortingRepository<IQuantify, Long> {
    IQuantify findOne(Long id);
    
    IQuantify findTop1ByPersonIdOrderByCreatedOnDesc(String personId);
    
    IQuantify findByPersonId(String personId);
}
