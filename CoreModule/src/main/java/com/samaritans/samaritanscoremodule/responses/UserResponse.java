package com.samaritans.samaritanscoremodule.responses;

import java.time.Instant;

import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.utils.RoleEnum;

/**
 * Response return when requesting user details
 * 
 * @author jackl
 *
 */
public class UserResponse {

	private Long id;

	private String firstName;

	private String lastName;

	private String username;

	private String contactNumber;

	private String email;

	private boolean admin;

	private Instant lastActive;

	public UserResponse() {
	}

	public UserResponse(BoUser user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.admin = user.getRoles().stream().anyMatch(x -> x.getId() == RoleEnum.ADMIN.getId());
		this.lastActive = user.getLastActive();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.contactNumber = user.getContactNumber();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Instant getLastActive() {
		return lastActive;
	}

	public void setLastActive(Instant lastActive) {
		this.lastActive = lastActive;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (admin ? 1231 : 1237);
		result = prime * result + ((contactNumber == null) ? 0 : contactNumber.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((lastActive == null) ? 0 : lastActive.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		UserResponse other = (UserResponse) obj;
		if (admin != other.admin) {
			return false;
		}
		if (contactNumber == null) {
			if (other.contactNumber != null) {
				return false;
			}
		} else if (!contactNumber.equals(other.contactNumber)) {
			return false;
		}
		if (email == null) {
			if (other.email != null) {
				return false;
			}
		} else if (!email.equals(other.email)) {
			return false;
		}
		if (firstName == null) {
			if (other.firstName != null) {
				return false;
			}
		} else if (!firstName.equals(other.firstName)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (lastActive == null) {
			if (other.lastActive != null) {
				return false;
			}
		} else if (!lastActive.equals(other.lastActive)) {
			return false;
		}
		if (lastName == null) {
			if (other.lastName != null) {
				return false;
			}
		} else if (!lastName.equals(other.lastName)) {
			return false;
		}
		if (username == null) {
			if (other.username != null) {
				return false;
			}
		} else if (!username.equals(other.username)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "UserResponse [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", username="
				+ username + ", contactNumber=" + contactNumber + ", email=" + email + ", admin=" + admin
				+ ", lastActive=" + lastActive + "]";
	}

}
