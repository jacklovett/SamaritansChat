package com.gibsams.gibsamscoremodule.service;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import com.gibsams.gibsamscoremodule.exception.InvalidReCaptchaException;
import com.gibsams.gibsamscoremodule.responses.ReCaptchaResponse;
import com.gibsams.gibsamscoremodule.security.ReCaptchaSettings;

@RunWith(MockitoJUnitRunner.class)
public class ReCaptchaServiceTest {

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

	@Before
	public void setUp() {

		reCaptchaResponse = new ReCaptchaResponse();
		reCaptchaResponse.setSuccess(true);
		reCaptchaResponse.setAction(REGISTER_ACTION);
		reCaptchaResponse.setScore(0.6);

		when(request.getHeader("X-Forwarded-For")).thenReturn(CLIENT_IP);
	}

	@Test(expected = InvalidReCaptchaException.class)
	public void testVerifyResponseWhenSanityCheckFails() throws Exception {
		reCaptchaService.verifyResponse("");
	}

	@Test
	public void testVerifyResponseSuccess() {
		when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(reCaptchaResponse);
		reCaptchaService.verifyResponse(TOKEN);
	}

	@Test(expected = InvalidReCaptchaException.class)
	public void testVerifyResponseSuccessFalse() throws Exception {
		reCaptchaResponse.setSuccess(false);
		when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(reCaptchaResponse);
		reCaptchaService.verifyResponse(TOKEN);
	}

	@Test(expected = InvalidReCaptchaException.class)
	public void testVerifyResponseWhenScoreBelowThreshold() throws Exception {
		when(reCaptchaSettings.getThreshold()).thenReturn(0.8);
		when(restTemplate.getForObject(Mockito.any(), Mockito.any())).thenReturn(reCaptchaResponse);
		reCaptchaService.verifyResponse(TOKEN);
	}

	@Test(expected = InvalidReCaptchaException.class)
	public void testVerifyResponseWhenRestCallFails() throws Exception {
		doThrow(new RestClientException("Rest call failed!")).when(restTemplate).getForObject(Mockito.any(),
				Mockito.any());
		reCaptchaService.verifyResponse(TOKEN);
	}

}
