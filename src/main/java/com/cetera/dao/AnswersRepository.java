package com.cetera.dao;

import com.cetera.domain.Answers;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Repository for Answers table
 * Created by danni on 3/23/16.
 */
public interface AnswersRepository extends PagingAndSortingRepository<Answers, Long> {
    List<Answers> findByIQuantifyId(Long iQuantifyId);
}
