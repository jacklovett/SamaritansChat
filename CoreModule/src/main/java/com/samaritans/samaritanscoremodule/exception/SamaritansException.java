package com.samaritans.samaritanscoremodule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class SamaritansException extends RuntimeException {

	/**
	 * Generated Serial Version UID
	 */
	private static final long serialVersionUID = -6085089771014506431L;

	public SamaritansException(String message) {
		super(message);
	}

	public SamaritansException(String message, Throwable cause) {
		super(message, cause);
	}
}
