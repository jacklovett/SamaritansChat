package com.gibsams.gibsamscoremodule.requests;

public class ConversationRequest {

	private String gibSamsUser;
	
	private String chatUser;
	
	public ConversationRequest() { }
	
	public ConversationRequest(String gibSamsUser, String chatUser) {
		this.gibSamsUser = gibSamsUser;
		this.chatUser = chatUser;
	}
	
	public String getGibSamsUser() {
		return gibSamsUser;
	}

	public void setGibSamsUser(String gibSamsUser) {
		this.gibSamsUser = gibSamsUser;
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
		result = prime * result + ((gibSamsUser == null) ? 0 : gibSamsUser.hashCode());
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
		if (gibSamsUser == null) {
			if (other.gibSamsUser != null) {
				return false;
			}
		} else if (!gibSamsUser.equals(other.gibSamsUser)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ConversationRequest [gibSamsUser=" + gibSamsUser + ", chatUser=" + chatUser + "]";
	}	
	
}
