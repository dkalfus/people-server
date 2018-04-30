package com.amex.personServer.controller;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.amex.personServer.domain.Person;
import com.amex.personServer.dto.PersonDto;
import com.amex.personServer.repository.PersonRepository;
import com.amex.personServer.service.PersonService;

/**
 * 
 * @author David Kalfus
 *
 * This controller's end-points expose Person entities as DTO's.  It makes direct use of
 * the PersonRepository where no business logic is involved.  If business logic is involved,
 * it delegates the work to the PersonService.
 *
 */
@RestController
@RequestMapping("/people")
public class PersonController extends AmexBaseController  {
	
	@Autowired
	PersonRepository personRepository;
	
	@Autowired
	PersonService personService;
	
	// Defines the format for setting and parsing the DTO's date of birth:
	private static DateTimeFormatter DATE_OF_BIRTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	@RequestMapping(method = RequestMethod.GET, value = "/{id}")
	public PersonDto getPerson(@PathVariable Long id) {
		return personRepository.findById(id)
			.map(thePerson-> personToPersonDto(thePerson))
			.orElseThrow(() -> 
				new ResourceNotFoundException("Person: id=" + id.toString() + " not found."));	
	}
	
	@GetMapping
	ResponseEntity<List<PersonDto>> getAllPeople() {
		return ResponseEntity.ok(StreamSupport.stream(personRepository.findAll().spliterator(), false)
			.map(person-> personToPersonDto(person))
			.collect(Collectors.toList()));
	}
 
	@PostMapping
	public ResponseEntity<Person> createPerson(@RequestBody PersonDto personDto) {			
		Person person = personDtoToPerson(personDto);
		personService.create(person);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(person.getId()).toUri();

		return ResponseEntity.created(location).body(person);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/{id}")
	public ResponseEntity<Person> updatePerson(@PathVariable Long id, @RequestBody PersonDto personDto) {
		Person person = personDtoToPerson(personDto);
		person.setId(id);
		Person updatedPerson=personService.update(person);
		if (null==updatedPerson) {
			return ResponseEntity.notFound().build();
		}		
		
		return ResponseEntity.ok().body(person);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	public ResponseEntity<Person> deletePerson(@PathVariable Long id) {
		if (personRepository.findById(id).isPresent()) {
			personRepository.deleteById(id);
			return ResponseEntity.noContent().build();	
		} 
		
		// Consider returning "404 Not Found" in this case:
		return ResponseEntity.noContent().build();
	}

	// For more complex dto's, Dozer might be a better way to perform such mapping
	private static PersonDto personToPersonDto(Person person) {
		PersonDto personDto = new PersonDto();	
		personDto.setId(person.getId());
		personDto.setName(person.getName());
		personDto.setAge(person.getAge());
		personDto.setEmailAddress(person.getEmailAddress());
		personDto.setDateOfBirth( person.getDateOfBirth().format(DATE_OF_BIRTH_FORMATTER));   
		
		return personDto;
	}
	
	private static Person personDtoToPerson(PersonDto personDto) {
		Person person = new Person();
		person.setId(personDto.getId());
		person.setName(personDto.getName());
		person.setAge(personDto.getAge());
		person.setEmailAddress(personDto.getEmailAddress());
		try {
			person.setDateOfBirth(LocalDate.parse(personDto.getDateOfBirth(),DATE_OF_BIRTH_FORMATTER));
		} catch (DateTimeParseException e) {
			throw new IllegalArgumentException("dateOfBirth not in YYYY-MM-DD format: '" + personDto.getDateOfBirth() + "'");
		}
		
		return person;		
	}		
}

