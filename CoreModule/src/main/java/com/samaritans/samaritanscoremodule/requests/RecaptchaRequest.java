package com.samaritans.samaritanscoremodule.requests;

import javax.validation.constraints.NotBlank;

/**
 * Recaptcha verification request
 * 
 * @author jackl
 */
public class RecaptchaRequest {

    @NotBlank
    private String response;

    @NotBlank
    private String secret;

    public RecaptchaRequest(@NotBlank String response, @NotBlank String secret) {
        this.response = response;
        this.secret = secret;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((response == null) ? 0 : response.hashCode());
        result = prime * result + ((secret == null) ? 0 : secret.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RecaptchaRequest other = (RecaptchaRequest) obj;
        if (response == null) {
            if (other.response != null)
                return false;
        } else if (!response.equals(other.response))
            return false;
        if (secret == null) {
            if (other.secret != null)
                return false;
        } else if (!secret.equals(other.secret))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RecaptchaRequest [response=" + response + ", secret=" + secret + "]";
    }

}