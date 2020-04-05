package com.gibsams.gibsamscoremodule.requests;

import javax.validation.constraints.NotBlank;

/**
 * Login request we receive to authenticate the user.
 * @author jackl
 *
 */
public class LoginRequest {

	@NotBlank
	private String usernameOrEmail;

	@NotBlank
	private String password;
	
	public LoginRequest() {}
	
	public LoginRequest(String usernameOrEmail, String password) {
		this.usernameOrEmail = usernameOrEmail;
		this.password = password;
	}

	public String getUsernameOrEmail() {
		return usernameOrEmail;
	}

	public void setUsernameOrEmail(String usernameOrEmail) {
		this.usernameOrEmail = usernameOrEmail;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((usernameOrEmail == null) ? 0 : usernameOrEmail.hashCode());
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
		LoginRequest other = (LoginRequest) obj;
		if (password == null) {
			if (other.password != null) {
				return false;
			}
		} else if (!password.equals(other.password)) {
			return false;
		}
		if (usernameOrEmail == null) {
			if (other.usernameOrEmail != null) {
				return false;
			}
		} else if (!usernameOrEmail.equals(other.usernameOrEmail)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "LoginRequest [usernameOrEmail=" + usernameOrEmail + ", password=" + password + "]";
	}
	
}
