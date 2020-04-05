package com.gibsams.gibsamscoremodule.controller;

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

import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.requests.LoginRequest;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.responses.JwtAuthenticationResponse;
import com.gibsams.gibsamscoremodule.security.JwtTokenProvider;
import com.gibsams.gibsamscoremodule.service.AuthenticationService;
import com.gibsams.gibsamscoremodule.service.ChatUserService;
import com.gibsams.gibsamscoremodule.utils.AppConstants;
import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class AuthControllerTest {

	private static final Long ID = 1l;
	private static final String USERNAME = "username";
	private static final String SECRET = "secret";
	private static final String TOKEN = "token";

	private User user;
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
	private UserDao userDao;
	@Mock
	private JwtTokenProvider jwtTokenProvider;
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

		user = new User();
		user.setId(ID);

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

		chatLoginRequestBuilder = MockMvcRequestBuilders.post("/api/auth/chat/login");
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

		doThrow(new GibSamsException("Unable to authenticate user")).when(authenticationService)
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
	public void testChatLoginUnableToRegisterUser() throws Exception {

		when(chatUserService.buildChatUserRequest()).thenReturn(registerRequest);

		doThrow(new GibSamsException(AppConstants.CHAT_ACCESS_ERROR_MESSAGE)).when(chatUserService)
				.registerUser(registerRequest);

		MvcResult result = mockMvc.perform(chatLoginRequestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

}
