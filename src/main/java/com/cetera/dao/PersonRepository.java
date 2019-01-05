package com.cetera.dao;

import com.cetera.domain.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for Person table
 */
public interface PersonRepository extends PagingAndSortingRepository<Person, String> {

	List<Person> findByLastName(@Param("name") String name);
}
