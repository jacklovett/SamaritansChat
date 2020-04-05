package com.gibsams.gibsamscoremodule.utils;

public class AppConstants {

	private AppConstants() {
		throw new IllegalStateException("Utility class");
	}

	public static final String GIB_SAMS_USERNAME = "Sam";

	public static final String CHAT_ACCESS_ERROR_MESSAGE = "An error occurred during login. "
			+ "If the problem persists then please try calling the GibSams hotline on +350116123 instead.";

	public static final String DEFAULT_PAGE_NUMBER = "0";
	public static final String DEFAULT_PAGE_SIZE = "30";

	public static final int MAX_PAGE_SIZE = 50;

	// ChatAvailaibilityEnum Constants
	public static final String AVAILABLE_MESSAGE = "Chat is available";
	public static final String NO_VOLUNTEERS_MESSAGE = "Chat is currently unavailable while we wait for volunteers to connect.";
	public static final String UNAVAILABLE_TIME_MESSAGE = "Chat service is available between {0} and {1}. Please try again then.";

	// NotificationTypeEnum Constants
	public static final String USER_DISCONNECTED_CONTENT = "{0} has disconnected from chat";
	public static final String USER_DISCONNECTED_CTA = "Save & Review Chat Log";
	public static final String USER_DISCONNECTED_PROCESSED_CTA = "Chat Log Already Saved";

	public static final String NEW_USER_CONNECTED_CONTENT = "{0} has connected to chat";
	public static final String NEW_USER_CONNECTED_CTA = "Start conversation";
	public static final String NEW_USER_CONNECTED_PROCESSED_CTA = "Conversation already in progress";

}
