package com.gibsams.gibsamscoremodule.responses;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "success", "score", "action", "challenge_ts", "hostname", "error-codes" })
public class ReCaptchaResponse {

	enum ErrorCode {
		MISSING_SECRET, INVALID_SECRET, MISSING_RESPONSE, INVALID_RESPONSE;

		private static Map<String, ErrorCode> errorsMap = new HashMap<>(4);

		static {
			errorsMap.put("missing-input-secret", MISSING_SECRET);
			errorsMap.put("missing-input-response", MISSING_RESPONSE);
			errorsMap.put("invalid-input-secret", INVALID_SECRET);
			errorsMap.put("invalid-input-response", INVALID_RESPONSE);
		}

		@JsonCreator
		public static ErrorCode forValue(String value) {
			return errorsMap.get(value.toLowerCase());
		}
	}

	@JsonProperty("success")
	private boolean success;

	@JsonProperty("score")
	private double score;

	@JsonProperty("action")
	private String action;

	@JsonProperty("challenge_ts")
	private String challengeTs;

	@JsonProperty("hostname")
	private String hostname;

	@JsonProperty("error-codes")
	private ErrorCode[] errorCodes;

	@JsonIgnore
	public boolean hasClientError() {
		ErrorCode[] errors = getErrorCodes();
		if (errors == null) {
			return false;
		}
		for (ErrorCode error : errors) {
			switch (error) {
				case INVALID_RESPONSE:
				case MISSING_RESPONSE:
					return true;
				default:
					break;
			}
		}
		return false;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getChallengeTs() {
		return challengeTs;
	}

	public void setChallengeTs(String challengeTs) {
		this.challengeTs = challengeTs;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public ErrorCode[] getErrorCodes() {
		return errorCodes;
	}

	public void setErrorCodes(ErrorCode[] errorCodes) {
		this.errorCodes = errorCodes;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@Override
	public String toString() {
		return "ReCaptchaResponse [action=" + action + ", challengeTs=" + challengeTs + ", errorCodes="
				+ Arrays.toString(errorCodes) + ", hostname=" + hostname + ", score=" + score + ", success=" + success
				+ "]";
	}

}