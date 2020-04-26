package com.gibsams.gibsamscoremodule.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "bo_users", uniqueConstraints = { @UniqueConstraint(columnNames = { "username" }),
		@UniqueConstraint(columnNames = { "email" }) })
public class BoUser extends User {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -9091290516728939223L;

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

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "bo_user_roles", joinColumns = @JoinColumn(name = "bo_user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@OneToMany(mappedBy = "boUser")
	private Set<Notification> notification;

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Notification> getNotification() {
		return notification;
	}

	public void setNotification(Set<Notification> notification) {
		this.notification = notification;
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
