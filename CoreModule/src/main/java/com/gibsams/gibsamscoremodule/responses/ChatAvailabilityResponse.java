package com.gibsams.gibsamscoremodule.responses;

import com.gibsams.gibsamscoremodule.utils.ChatAvailabilityEnum;

/**
 * Response class containing information on whether the chat is available
 * for public users
 * @author jackl
 *
 */
public class ChatAvailabilityResponse {
	
	private boolean available;
	
	private String type;
	
	private String message;
	
	public ChatAvailabilityResponse(ChatAvailabilityEnum chatAvailabilityEnum) {
		this.available = chatAvailabilityEnum.isAvailable();
		this.type = chatAvailabilityEnum.getType();
		this.message = chatAvailabilityEnum.getMessage();
	}
	
	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (available ? 1231 : 1237);
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		ChatAvailabilityResponse other = (ChatAvailabilityResponse) obj;
		if (available != other.available) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ChatAvailabilityResponse [available=" + available + ", type=" + type + ", message=" + message + "]";
	}
	
}
