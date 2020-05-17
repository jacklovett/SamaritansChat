package com.samaritans.samaritanscoremodule.requests;

public class ConversationRequest {

	private String samaritansUser;
	
	private String chatUser;
	
	public ConversationRequest() { }
	
	public ConversationRequest(String samaritansUser, String chatUser) {
		this.samaritansUser = samaritansUser;
		this.chatUser = chatUser;
	}
	
	public String getSamaritansUser() {
		return samaritansUser;
	}

	public void setSamaritansUser(String samaritansUser) {
		this.samaritansUser = samaritansUser;
	}

		public String getChatUser() {
		return chatUser;
	}

	public void setChatUser(String chatUser) {
		this.chatUser = chatUser;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chatUser == null) ? 0 : chatUser.hashCode());
		result = prime * result + ((samaritansUser == null) ? 0 : samaritansUser.hashCode());
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
		ConversationRequest other = (ConversationRequest) obj;
		if (chatUser == null) {
			if (other.chatUser != null) {
				return false;
			}
		} else if (!chatUser.equals(other.chatUser)) {
			return false;
		}
		if (samaritansUser == null) {
			if (other.samaritansUser != null) {
				return false;
			}
		} else if (!samaritansUser.equals(other.samaritansUser)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ConversationRequest [samaritansUser=" + samaritansUser + ", chatUser=" + chatUser + "]";
	}	
	
}
