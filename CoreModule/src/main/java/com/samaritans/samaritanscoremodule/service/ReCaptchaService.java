package com.samaritans.samaritanscoremodule.service;

import java.net.URI;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.samaritans.samaritanscoremodule.exception.InvalidReCaptchaException;
import com.samaritans.samaritanscoremodule.exception.ReCaptchaUnavailableException;
import com.samaritans.samaritanscoremodule.responses.ReCaptchaResponse;
import com.samaritans.samaritanscoremodule.security.ReCaptchaSettings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class ReCaptchaService {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ReCaptchaSettings reCaptchaSettings;

	@Autowired
	private RestOperations restTemplate;

	private LoadingCache<String, Integer> attemptsCache;

	private static final int MAX_ATTEMPT = 3;
	private static final String REGISTER_ACTION = "register";
	private static final Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
	private static final String RECAPTCHA_URI = "https://www.google.com/recaptcha/api/siteverify?secret={0}&response={1}&remoteip={2}";

	private static final Logger logger = LoggerFactory.getLogger(ReCaptchaService.class);

	public ReCaptchaService() {
		super();
		attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.HOURS)
				.build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(final String key) {
						return 0;
					}
				});
	}

	/**
	 * Verifies google Recaptcha response
	 * 
	 * @param response
	 * @throws RestClientException | InvalidReCaptchaException
	 */
	public void verifyResponse(String response) {
		String clientIp = getClientIP();
		securityCheck(response, clientIp);
		URI verifyUri = URI
				.create(MessageFormat.format(RECAPTCHA_URI, reCaptchaSettings.getSecret(), response, clientIp));

		ReCaptchaResponse reCaptchaResponse;
		try {
			reCaptchaResponse = restTemplate.getForObject(verifyUri, ReCaptchaResponse.class);
		} catch (RestClientException ex) {
			throw new ReCaptchaUnavailableException("ReCaptcha verification is currently unavailable", ex);
		}

		logger.info("Recaptcha Response: {}", reCaptchaResponse);

		if (!reCaptchaResponse.isSuccess() || !reCaptchaResponse.getAction().equals(REGISTER_ACTION)
				|| reCaptchaResponse.getScore() < reCaptchaSettings.getThreshold()) {
			if (reCaptchaResponse.hasClientError()) {
				reCaptchaFailed(clientIp);
			}
			throw new InvalidReCaptchaException("reCaptcha failed. Check ReCaptcha Response for details");
		}
		reCaptchaSucceeded(clientIp);
	}

	private void reCaptchaFailed(String key) {
		int attempts = attemptsCache.getUnchecked(key);
		attempts++;
		attemptsCache.put(key, attempts);
	}

	private void reCaptchaSucceeded(String key) {
		attemptsCache.invalidate(key);
	}

	private boolean isBlocked(String key) {
		return attemptsCache.getUnchecked(key) >= MAX_ATTEMPT;
	}

	private void securityCheck(String response, String clientIp) {

		if (isBlocked(clientIp)) {
			throw new InvalidReCaptchaException("Client exceeded maximum number of failed attempts");
		}

		if (!responseSanityCheck(response)) {
			throw new InvalidReCaptchaException("Response contains invalid characters");
		}
	}

	private boolean responseSanityCheck(String response) {
		return StringUtils.isNotBlank(response) && RESPONSE_PATTERN.matcher(response).matches();
	}

	private String getClientIP() {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
}