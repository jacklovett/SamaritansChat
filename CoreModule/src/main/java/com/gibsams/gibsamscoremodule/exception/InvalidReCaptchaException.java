package com.gibsams.gibsamscoremodule.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InvalidReCaptchaException extends RuntimeException {

    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = -3888591446465357429L;

    public InvalidReCaptchaException(String message) {
        super(message);
    }

    public InvalidReCaptchaException(String message, Throwable cause) {
        super(message, cause);
    }
}