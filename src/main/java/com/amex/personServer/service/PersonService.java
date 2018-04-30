package com.amex.personServer.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amex.personServer.domain.Person;
import com.amex.personServer.repository.PersonRepository;

@Service
@Transactional
/**
 * 
 * Service providing transactional business logic for modifying the Person table.  Any unchecked
 * repository exceptions will be passed back to the caller.
 *
 * @author David Kalfus
 *
 */
public class PersonService {

	@Autowired
	PersonRepository personRepository;
	
	/**
	 * 
	 * @param person the new person to be created.  Its values  is validated and it will
	 *  be saved in the Person table.  Any supplied id will be ignored and overwritten with the new id.
	 *  @return the same Person object reference passed in as a parameter (for convenience).
	 */
	public Person create(Person person) {
		validatePerson(person);
		
		// Check that the email address does not duplicate an existing Person's:
		checkEmailIsNotDuplicate(person.getEmailAddress());;	

		personRepository.save(person);
		return person;
	}
	
	/**
	 *  Updates a Person.  Attempts to locate a Person in the Person table with the same id as that of 
	 *  the parameter passed in.  If successful, it validates the new values and saves them into
	 *  the new Person object.  Null is returned if the Person could not be found.
	 * 
	 * @param person the Person to be updated
	 * @return If the person was found and updated, the same Person reference passed in as a parameter; null if
	 *   the Person could not be found.
	 */
	public Person update(Person person) {
		validatePerson(person);
		
		Person existingPerson = personRepository.findById(person.getId()).orElse(null);
		if (null==existingPerson) {
			return null;
		}

		if (!existingPerson.getEmailAddress().toUpperCase().equals( person.getEmailAddress().toUpperCase())) {
			// This update involves changing an email address.  Make sure the new
			// address does not already exist in the DB:
			checkEmailIsNotDuplicate(person.getEmailAddress());
		}
		
		personRepository.save(person);
		return person;
	}
	
	// Throws an IllegalArgumentException if the specified email address already exists
	// on the DB in a Person entry.
	private void checkEmailIsNotDuplicate(String emailAddress) {
		if (personRepository.findByEmailAddressIgnoreCase(emailAddress).isPresent()) {
			throw new IllegalArgumentException("Duplicate email address: " + emailAddress);
		}
	}
	
	/**
	 * Validates all non-id fields in a Person object.  If contents are structurally invalid,
	 * creates a message summarize all errors and throws an IllegalArgumentException
	 * containing all errors.  
	 * 
	 * @param person
	 * @throws IllegalArgumentException if any data in Person object is structurally
	 * invalid.
	 */
	public static void validatePerson(Person person) {	
		List<String> errorMessages = new ArrayList<String>(8);
		
		// Age
		if (null==person.getAge()) {
			errorMessages.add("Missing age");
		} else if (person.getAge() < 0) {
			errorMessages.add("Age is negative: " + person.getAge());
		}
		
		// Email
		if (null==person.getEmailAddress() || person.getEmailAddress().isEmpty()) {
			errorMessages.add("Email Address is missing or blank");
		} else if (!person.getEmailAddress().contains("@")) {
				// Todo: Add more robust email format validations:
			errorMessages.add("Badly formatted email address");
		}
		
		// Name
		if (null==person.getName() || person.getName().isEmpty()) {
			errorMessages.add("Name is missing or blank");
		}

		if (0==errorMessages.size()) {
			return;
		}
		
		StringBuilder errorSummaryBuilder = new StringBuilder();
		errorSummaryBuilder.append(errorMessages.get(0));
		int i=1;
		while (i < errorMessages.size()) {
			errorSummaryBuilder.append("; ");
			errorSummaryBuilder.append(errorMessages.get(i));
			i++;
		}
		throw new IllegalArgumentException(errorSummaryBuilder.toString());		
	}

}
