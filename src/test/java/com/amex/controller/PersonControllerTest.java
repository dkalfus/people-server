package com.amex.controller;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.amex.personServer.PersonServerApplication;
import com.amex.personServer.dto.PersonDto;
import com.amex.personServer.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PersonServerApplication.class)
@AutoConfigureMockMvc

/*
 * Test Suite for for PersonController.  This may function as a unit test because it runs
 * against an in-memory DB, but is in effect written as an integration test suite.  It does not
 * respect any existing data integrity of the test DB so care must be taken if it is converted
 * in future to an integration test suite.
 * 
 * Author: David Kalfus
 */
public class PersonControllerTest {
	
	private String SERVICE_ENTITY_NAME="/people/";
	 
	private static final Integer TEST_AGE = 33;
	private static final Integer TEST_AGE2 = 66;
	private static final String TEST_NAME = "John Smith";
	private static final String TEST_DATE_OF_BIRTH = "1985-04-14";
	
	// Error messages from server:
	private static final String MSG_DUPLICATE_EMAIL="Duplicate email address";
	
	// Counts how many unique email addresses have been created within this class for tesing purposes.
	// Used to fabricate unique address values:
	private static int emailAddrCounter=1;
	    
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	PersonRepository personRepository;

	private static ObjectMapper objectMapper=new ObjectMapper();
    
	@Test
	public void testPostAndGet() throws Exception {
	    	
	 	final PersonDto testPerson1 = getPersonDto();

	   	String person1Location=this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
    	    .content( objectMapper.writeValueAsString(testPerson1))
    	    .contentType(MediaType.APPLICATION_JSON_UTF8))
    		.andExpect(status().isCreated()).andDo(print())
    		.andReturn().getResponse().getHeader("Location");

	    // Location header will be in form "http://localhost/transactions/{newid}.  
	    // Strip off the trailing id and check that the newly posted object can be
	   	// retrieved with the expected values:
	   	String person1Id = person1Location.substring(person1Location.lastIndexOf('/')+1);
    	    
    	// Add a 2nd person; make age different to distinguish the entities if debugging is needed:
    	
	 	final PersonDto testPerson2 = getPersonDto();
	 	testPerson2.setAge(TEST_AGE2);

    	this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
    	    .content( objectMapper.writeValueAsString(testPerson2))
    	    .contentType(MediaType.APPLICATION_JSON_UTF8))
    		.andExpect(status().isCreated()).andDo(print());
	    		     
    	// Retrieve the first person and verify values are as expected:
    	this.mockMvc.perform(get(person1Location))
    		.andExpect(status().isOk())
    		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
    		.andExpect(jsonPath("$.id").value(person1Id))
    		.andExpect(jsonPath("$.age").value(testPerson1.getAge()))
    		.andExpect(jsonPath("$.emailAddress").value(testPerson1.getEmailAddress()))
    		.andExpect(jsonPath("$.name").value(testPerson1.getName()));
	}
	
	@Test
	public void testGetAll() throws Exception {
		// Warning: This test deletes all entries in the DB.  Beware
		// of converting this to an integration test against a real DB!

		final int NUM_PERSONS_TO_LOAD=4;
		
		personRepository.deleteAll();
		
    	for (int i=0; i<NUM_PERSONS_TO_LOAD; i++) {
    		this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
        	    .content( objectMapper.writeValueAsString(getPersonDto()))
        	    .contentType(MediaType.APPLICATION_JSON_UTF8))	
    			.andExpect(status().isCreated()).andDo(print());
    	}
    	
      	this.mockMvc.perform(get(SERVICE_ENTITY_NAME))
      		.andExpect(status().isOk())
      		.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
      		.andExpect(jsonPath("$.length()").value(NUM_PERSONS_TO_LOAD));	
	}
	
	
	@Test
	public void testPut() throws Exception {

		PersonDto testPerson=getPersonDto();
		
		// Add a test person:
		String personLocation=this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
				.content( objectMapper.writeValueAsString(testPerson))
				.contentType(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isCreated()).andDo(print())
				.andReturn().getResponse().getHeader("Location");
   	
		// Change the age and put the new value:
		Integer newAge=testPerson.getAge()+1;
		testPerson.setAge(newAge);
   	
		this.mockMvc.perform(put(personLocation)
    	    .content( objectMapper.writeValueAsString(testPerson))
    	    .contentType(MediaType.APPLICATION_JSON_UTF8))
    		.andExpect(status().isOk())
    		.andDo(print());
   	
		// Retrieve the person and check age reflects new value:  	
		this.mockMvc.perform(get(personLocation))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
			.andExpect(jsonPath("$.age").value(newAge));
	}
	
	
	@Test
	public void testDuplicateEmailCheck() throws Exception {
	 	final PersonDto testPerson = getPersonDto();    
	    
	   	// Test post prevents dupes.  Post a new Person and attempt to repost it with same email:
	 	this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
	    	.content( objectMapper.writeValueAsString(testPerson))
	    	.contentType(MediaType.APPLICATION_JSON_UTF8))
	    	.andExpect(status().isCreated()).andDo(print());

    	// Test re-post:
	   	this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
        	.content( objectMapper.writeValueAsString(testPerson))
        	.contentType(MediaType.APPLICATION_JSON_UTF8))
        	.andExpect(status().isUnprocessableEntity())
        	.andExpect(jsonPath("$.message").value(containsString(MSG_DUPLICATE_EMAIL)))
           	.andExpect(status().isUnprocessableEntity())
        	.andDo(print());
	   	
	   	PersonDto testPerson2 = getPersonDto();
	   	
	   	// Test that puts prevent dupes:  Post a 2nd person and attempt to update it to same email as first person:
	   	String person2Location=this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
	        .content( objectMapper.writeValueAsString(testPerson2))
	        .contentType(MediaType.APPLICATION_JSON_UTF8))
	    	.andExpect(status().isCreated()).andDo(print())
	    	.andReturn().getResponse().getHeader("Location");

    	// Test put with a duplicate email
	   	this.mockMvc.perform(put(person2Location)
            .content( objectMapper.writeValueAsString(testPerson))
            .contentType(MediaType.APPLICATION_JSON_UTF8))
        	.andExpect(status().isUnprocessableEntity())
        	.andDo(print());
	}
	
	@Test
	// This tests that validations are done; exhaustive validation tests should be done elsewhere
	public void testValidationsArePerformedForPost() throws Exception {
		final PersonDto testPersonDto = getPersonDto();
		testPersonDto.setDateOfBirth("XXXX");
		
		// Post an invalid object and check that return is unprocessable:
	   	this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
	    	    .content( objectMapper.writeValueAsString(testPersonDto))
	    	    .contentType(MediaType.APPLICATION_JSON_UTF8))
	    		.andExpect(status().isUnprocessableEntity()).andDo(print())
	    		.andReturn().getResponse().getHeader("Location");
	}
	
	@Test
	// This tests that validations are done; exhaustive validation tests should be done elsewhere
	public void testValidationsArePerformedForPut() throws Exception {
		
		// Post a valid object:
	 	final PersonDto testPersonDto = getPersonDto();
		
	   	String personLocation=this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
	    	    .content( objectMapper.writeValueAsString(testPersonDto))
	    	    .contentType(MediaType.APPLICATION_JSON_UTF8))
	    		.andExpect(status().isCreated()).andDo(print())
	    		.andReturn().getResponse().getHeader("Location");
	   	
	   	// Put an invalid change and check that return is "UnprocessableEntity":
		testPersonDto.setDateOfBirth("XXXX");
		
		// Post an invalid object and check that return is unprocessable:
	   	this.mockMvc.perform(put(personLocation)
	    	    .content( objectMapper.writeValueAsString(testPersonDto))
	    	    .contentType(MediaType.APPLICATION_JSON_UTF8))
	    		.andExpect(status().isUnprocessableEntity()).andDo(print());
	}
	
	// Test delete
	@Test
	public void testDeleteAndGetNotFound() throws Exception {
		// Add a new person, delete it, and verify get returns not found:
		PersonDto testPerson = getPersonDto();
	   	String personLocation=this.mockMvc.perform(post(SERVICE_ENTITY_NAME)
		        .content( objectMapper.writeValueAsString(testPerson))
		        .contentType(MediaType.APPLICATION_JSON_UTF8))
		    	.andExpect(status().isCreated()).andDo(print())
		    	.andReturn().getResponse().getHeader("Location");
	   	
	   	mockMvc.perform(delete(personLocation).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());
	   	
		// Verify person can't be found: 	
		this.mockMvc.perform(get(personLocation))
			.andExpect(status().isNotFound());
 	
		// Verify that delete of non-existent rec succeeds;
	   	
	   	mockMvc.perform(delete(personLocation).contentType(MediaType.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());
	   	
		
	}

	// For testing purposes, creates a valid personDTO with a unique email address.
	private PersonDto getPersonDto() {
		PersonDto personDto = new PersonDto();
		personDto.setAge(TEST_AGE);
		personDto.setDateOfBirth(TEST_DATE_OF_BIRTH);
		personDto.setEmailAddress("John.Doe" + emailAddrCounter++ + "@gmail.com");
		personDto.setName(TEST_NAME);
		return personDto;
	}
	
}

