package com.gibsams.gibsamscoremodule.utils;

public enum NotificationTypeEnum {

	USER_DISCONNECTED(1, "USER_DISCONNECTED", AppConstants.USER_DISCONNECTED_CONTENT,
			AppConstants.USER_DISCONNECTED_CTA, AppConstants.USER_DISCONNECTED_PROCESSED_CTA),
	NEW_USER_CONNECTED(2, "NEW_USER_CONNECTED", AppConstants.NEW_USER_CONNECTED_CONTENT,
			AppConstants.NEW_USER_CONNECTED_CTA, AppConstants.NEW_USER_CONNECTED_PROCESSED_CTA);

	private int id;

	private String type;

	private String content;

	private String cta;

	private String processedCTA;

	NotificationTypeEnum(int id, String type, String content, String cta, String processedCTA) {
		this.id = id;
		this.type = type;
		this.content = content;
		this.cta = cta;
		this.processedCTA = processedCTA;
	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public String getCta() {
		return cta;
	}

	public String getProcessedCTA() {
		return processedCTA;
	}

}
