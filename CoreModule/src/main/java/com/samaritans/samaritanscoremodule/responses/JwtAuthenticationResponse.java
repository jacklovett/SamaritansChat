package com.samaritans.samaritanscoremodule.responses;

/**
 * Response class for JWT Authentication.
 * @author jackl
 *
 */
public class JwtAuthenticationResponse {

	private String token;
	private String tokenType = "Bearer";

	public JwtAuthenticationResponse(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((token == null) ? 0 : token.hashCode());
		result = prime * result + ((tokenType == null) ? 0 : tokenType.hashCode());
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
		JwtAuthenticationResponse other = (JwtAuthenticationResponse) obj;
		if (token == null) {
			if (other.token != null) {
				return false;
			}
		} else if (!token.equals(other.token)) {
			return false;
		}
		if (tokenType == null) {
			if (other.tokenType != null) {
				return false;
			}
		} else if (!tokenType.equals(other.tokenType)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "JwtAuthenticationResponse [token=" + token + ", tokenType=" + tokenType + "]";
	}
	
}