package com.samaritans.samaritanscoremodule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.samaritans.samaritanscoremodule.exception.InvalidReCaptchaException;
import com.samaritans.samaritanscoremodule.exception.ReCaptchaUnavailableException;
import com.samaritans.samaritanscoremodule.responses.ReCaptchaResponse;
import com.samaritans.samaritanscoremodule.security.ReCaptchaSettings;

@ExtendWith(MockitoExtension.class)
class ReCaptchaServiceTest {

	private static final String CLIENT_IP = "127.0.0.1";
	private static final String TOKEN = "token";
	private static final String REGISTER_ACTION = "register";

	private ReCaptchaResponse reCaptchaResponse;

	@Mock
	private HttpServletRequest request;
	@Mock
	private ReCaptchaSettings reCaptchaSettings;
	@Mock
	private RestOperations restTemplate;
	@InjectMocks
	private ReCaptchaService reCaptchaService;

	@BeforeEach
	void setUp() {

		reCaptchaResponse = new ReCaptchaResponse();
		reCaptchaResponse.setSuccess(true);
		reCaptchaResponse.setAction(REGISTER_ACTION);
		reCaptchaResponse.setScore(0.6);

		when(request.getHeader("X-Forwarded-For")).thenReturn(CLIENT_IP);
	}

	@Test
	void testVerifyResponseWhenSanityCheckFails() throws Exception {
		reCaptchaService.verifyResponse("");

		assertThrows(InvalidReCaptchaException.class, () -> reCaptchaService.verifyResponse(""));
	}

	@Test
	void testVerifyResponseSuccess() {
		when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(reCaptchaResponse);
		reCaptchaService.verifyResponse(TOKEN);
	}

	@Test
	void testVerifyResponseSuccessFalse() throws Exception {
		reCaptchaResponse.setSuccess(false);
		when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(reCaptchaResponse);
		reCaptchaService.verifyResponse(TOKEN);

		assertThrows(InvalidReCaptchaException.class, () -> reCaptchaService.verifyResponse(TOKEN));
	}

	@Test
	void testVerifyResponseWhenScoreBelowThreshold() throws Exception {
		when(reCaptchaSettings.getThreshold()).thenReturn(0.8);
		when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(reCaptchaResponse);
		reCaptchaService.verifyResponse(TOKEN);

		assertThrows(InvalidReCaptchaException.class, () -> reCaptchaService.verifyResponse(TOKEN));
	}

	@Test
	void testVerifyResponseWhenRestCallFails() throws Exception {
		doThrow(new RestClientException("Rest call failed!")).when(restTemplate).getForObject(Mockito.any(), Mockito.any());
		reCaptchaService.verifyResponse(TOKEN);

		assertThrows(ReCaptchaUnavailableException.class, () -> reCaptchaService.verifyResponse(TOKEN));
	}

}
