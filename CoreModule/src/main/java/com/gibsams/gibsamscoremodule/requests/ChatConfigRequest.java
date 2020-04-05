package com.gibsams.gibsamscoremodule.requests;

import com.gibsams.gibsamscoremodule.model.ChatConfig;

public class ChatConfigRequest {
	
	private boolean timeRestricted;
	
	private int availableFrom;
	
	private int availableUntil;
	
	public ChatConfigRequest() { }
	
	public ChatConfigRequest(ChatConfig config) {
		this.timeRestricted = config.isTimeRestricted();
		this.availableFrom = config.getAvailableFrom();
		this.availableUntil = config.getAvailableUntil();
	}
	
	public boolean isTimeRestricted() {
		return timeRestricted;
	}

	public void setTimeRestricted(boolean timeRestricted) {
		this.timeRestricted = timeRestricted;
	}

	public int getAvailableFrom() {
		return availableFrom;
	}

	public void setAvailableFrom(int availableFrom) {
		this.availableFrom = availableFrom;
	}

	public int getAvailableUntil() {
		return availableUntil;
	}

	public void setAvailableUntil(int availableUntil) {
		this.availableUntil = availableUntil;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + availableFrom;
		result = prime * result + availableUntil;
		result = prime * result + (timeRestricted ? 1231 : 1237);
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
		ChatConfigRequest other = (ChatConfigRequest) obj;
		if (availableFrom != other.availableFrom) {
			return false;
		}
		if (availableUntil != other.availableUntil) {
			return false;
		}
		if (timeRestricted != other.timeRestricted) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ChatConfigRequest [timeRestricted=" + timeRestricted + ", availableFrom=" + availableFrom
				+ ", availableUntil=" + availableUntil + "]";
	}

}
