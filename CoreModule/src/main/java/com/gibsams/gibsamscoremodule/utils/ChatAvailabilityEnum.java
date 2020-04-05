package com.gibsams.gibsamscoremodule.utils;

public enum ChatAvailabilityEnum {

	AVAILABLE(1, true, "AVAILABLE", AppConstants.AVAILABLE_MESSAGE),
	NO_VOLUNTEERS(2, false, "NO_VOLUNTEERS", AppConstants.NO_VOLUNTEERS_MESSAGE),
	UNAVAILABLE_TIME(3, false, "UNAVAILABLE_TIME", AppConstants.UNAVAILABLE_TIME_MESSAGE);

	ChatAvailabilityEnum(int id, boolean available, String type, String message) {
		this.id = id;
		this.available = available;
		this.type = type;
		this.message = message;
	}

	private int id;

	private boolean available;

	private String type;

	private String message;

	public int getId() {
		return id;
	}

	public boolean isAvailable() {
		return available;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

}
