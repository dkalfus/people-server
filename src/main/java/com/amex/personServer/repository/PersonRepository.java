package com.amex.personServer.repository;
	 
import org.springframework.data.domain.Page;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.amex.personServer.domain.Person;

@Repository
@RepositoryRestResource(exported=false)
@Transactional
public interface PersonRepository extends PagingAndSortingRepository<Person, Long>{	
		Optional<Person> findById(Long id);
		Optional<Person> findByEmailAddressIgnoreCase(String emailAddress);
	 
	}