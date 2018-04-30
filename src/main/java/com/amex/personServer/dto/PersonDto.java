package com.amex.personServer.dto;

public class PersonDto {
	/**
	 * DTO for Person entities.  Date field is string-formatted as expected by consumer of service.
	 */
	Long id;
	private String name;
	private Integer age;
	private String dateOfBirth;
	private String emailAddress;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getDateOfBirth() {
		return this.dateOfBirth;	
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	@Override
	public String toString() {
		return "PersonDto [id=" + id + ", name=" + name + ", age=" + age + ", dateOfBirth=" + dateOfBirth
				+ ", emailAddress=" + emailAddress + "]";
	}	

}
