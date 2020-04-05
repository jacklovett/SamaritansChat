package com.gibsams.gibsamscoremodule.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "user_info")
public class UserInfo extends Audit {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = 1435499911823253337L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(mappedBy = "userInfo")
	private User user;

	@NotBlank
	@Size(max = 40)
	@Column(name = "first_name")
	private String firstName;

	@NotBlank
	@Size(max = 40)
	@Column(name = "last_name")
	private String lastName;

	@Size(min = 9, max = 14)
	@Column(name = "contact_number")
	private String contactNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

}
