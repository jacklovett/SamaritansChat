package com.samaritans.samaritanscoremodule.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.exception.InvalidReCaptchaException;
import com.samaritans.samaritanscoremodule.requests.LoginRequest;
import com.samaritans.samaritanscoremodule.requests.RegisterRequest;
import com.samaritans.samaritanscoremodule.responses.JwtAuthenticationResponse;
import com.samaritans.samaritanscoremodule.security.JwtTokenProvider;
import com.samaritans.samaritanscoremodule.service.AuthenticationService;
import com.samaritans.samaritanscoremodule.service.ChatUserService;
import com.samaritans.samaritanscoremodule.service.ReCaptchaService;
import com.samaritans.samaritanscoremodule.utils.AppConstants;
import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

	private static final String USERNAME = "username";
	private static final String SECRET = "secret";
	private static final String TOKEN = "token";
	private static final String RECAPTCHA_TOKEN = "recaptcha_test_token";

	private Gson gson;
	private String json;
	private MockMvc mockMvc;
	private RegisterRequest registerRequest;
	private LoginRequest loginRequest;
	private RequestBuilder authUserRequestBuilder;
	private RequestBuilder chatLoginRequestBuilder;
	private JwtAuthenticationResponse jwtResponse;

	@Mock
	private Authentication authentication;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private ReCaptchaService reCaptchaService;
	@Mock
	private ChatUserService chatUserService;
	@Mock
	private AuthenticationService authenticationService;
	@InjectMocks
	private AuthController authController;

	@Before
	public void setUp() {

		this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

		gson = new Gson();

		loginRequest = new LoginRequest();
		loginRequest.setUsernameOrEmail(USERNAME);
		loginRequest.setPassword(SECRET);

		registerRequest = new RegisterRequest();
		registerRequest.setUsername(USERNAME);
		registerRequest.setPassword(SECRET);

		json = gson.toJson(loginRequest);

		jwtResponse = new JwtAuthenticationResponse(TOKEN);

		authUserRequestBuilder = MockMvcRequestBuilders.post("/api/auth/login").accept(MediaType.APPLICATION_JSON)
				.content(json).contentType(MediaType.APPLICATION_JSON);

		chatLoginRequestBuilder = MockMvcRequestBuilders.post("/api/auth/chat/login").accept(MediaType.APPLICATION_JSON)
				.content(RECAPTCHA_TOKEN).contentType(MediaType.APPLICATION_JSON);
	}

	@Test
	public void testAuthenticateUser() throws Exception {

		when(authenticationService.authenticate(loginRequest))
				.thenReturn(ResponseEntity.of(Optional.ofNullable(jwtResponse)));

		MvcResult result = mockMvc.perform(authUserRequestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(jwtResponse), response.getContentAsString());
	}

	@Test
	public void testAuthenticateUserThrowsException() throws Exception {

		doThrow(new SamaritansException("Unable to authenticate user")).when(authenticationService)
				.authenticate(loginRequest);

		MvcResult result = mockMvc.perform(authUserRequestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());

	}

	@Test
	public void testChatLogin() throws Exception {

		when(chatUserService.buildChatUserRequest()).thenReturn(registerRequest);
		when(authenticationService.authenticate(Mockito.any(LoginRequest.class)))
				.thenReturn(ResponseEntity.of(Optional.ofNullable(jwtResponse)));

		MvcResult result = mockMvc.perform(chatLoginRequestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	@Test
	public void testChatLoginWhenReCaptchaFails() throws Exception {

		doThrow(new InvalidReCaptchaException("Unable to validate response")).when(reCaptchaService)
				.verifyResponse(RECAPTCHA_TOKEN);
		MvcResult result = mockMvc.perform(chatLoginRequestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());

	}

	@Test
	public void testChatLoginUnableToRegisterUser() throws Exception {

		when(chatUserService.buildChatUserRequest()).thenReturn(registerRequest);

		doThrow(new SamaritansException(AppConstants.CHAT_ACCESS_ERROR_MESSAGE)).when(chatUserService)
				.registerUser(registerRequest);

		MvcResult result = mockMvc.perform(chatLoginRequestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

}
