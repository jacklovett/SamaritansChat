package com.samaritans.samaritanscoremodule.responses;

public class ChatUserResponse {
	
	private String username;
	
	private int unreadMessageCount;
	
	public ChatUserResponse(String username, int unreadMessageCount) {
		this.username = username;
		this.unreadMessageCount = unreadMessageCount;
	}

	protected String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	protected int getUnreadMessageCount() {
		return unreadMessageCount;
	}

	protected void setUnreadMessageCount(int unreadMessageCount) {
		this.unreadMessageCount = unreadMessageCount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + unreadMessageCount;
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
		ChatUserResponse other = (ChatUserResponse) obj;
		if (unreadMessageCount != other.unreadMessageCount) {
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
		return "ChatUserResponse [username=" + username + ", unreadMessageCount=" + unreadMessageCount + "]";
	}
	
}
