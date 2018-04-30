package com.amex.personServer.startup;

import java.time.LocalDate;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amex.personServer.domain.Person;
import com.amex.personServer.repository.PersonRepository;

/**
 * For demo purposes only-- adds a few recs to the DB at startup
 * 
 * @author David
 *
 */
@Component
public class StartupInit {

	@Autowired
	PersonRepository personRepository;
	
	@PostConstruct
	public void initializeDB (){

		Person person = new Person();
		
		person.setName("John Smith");
		person.setAge(45);
		person.setDateOfBirth(LocalDate.parse("1972-05-12"));
		person.setEmailAddress("John.Smith@yahoo.com");
		personRepository.save(person);
		
		person = new Person();
		
		person.setName("Paul Jones");
		person.setAge(36);
		person.setDateOfBirth(LocalDate.parse("1982-01-12"));
		person.setEmailAddress("Paul.Jones@gmail.com");
		personRepository.save(person);
		
		person.setName("Mark Miller");
		person.setAge(35);
		person.setDateOfBirth(LocalDate.parse("1982-05-12"));
		person.setEmailAddress("Mark.Miller@yahoo.com");
		personRepository.save(person);

	}
	
}