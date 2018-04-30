package com.amex.service;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.amex.personServer.PersonServerApplication;
import com.amex.personServer.domain.Person;
import com.amex.personServer.dto.PersonDto;
import com.amex.personServer.service.PersonService;

public class PersonServiceTest {
	
	@Test 
	public void testValidPersonIsNotRejected() {
		PersonService.validatePerson(getValidPerson());
	}
	
	@Test  (expected=IllegalArgumentException.class)
	public void testNegativeAge() {
		Person person = getValidPerson();
		person.setAge(-4);
		PersonService.validatePerson(person);	
	}
	
	@Test  (expected=IllegalArgumentException.class)
	public void testBlankEmail() {
		Person person = new Person();
		person.setEmailAddress("");
		PersonService.validatePerson(person);	
	}
	
	@Test  (expected=IllegalArgumentException.class)
	public void testBadEmail() {
		Person person = new Person();
		person.setEmailAddress("20th Street");
		PersonService.validatePerson(person);	
	}
	
	private Person getValidPerson() {
		Person person=new Person();
		person.setAge(20);
		person.setDateOfBirth(LocalDate.parse("1982-05-22"));
		person.setEmailAddress("Jon.Smith@gmail.com");
		person.setName("John Smith");
		return person;
	}
	
	
}
