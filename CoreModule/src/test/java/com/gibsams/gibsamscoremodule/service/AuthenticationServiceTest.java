package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.requests.LoginRequest;
import com.gibsams.gibsamscoremodule.responses.JwtAuthenticationResponse;
import com.gibsams.gibsamscoremodule.security.JwtTokenProvider;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationServiceTest {

	private static final Long ID = 1l;
	private static final String USERNAME = "username";
	private static final String SECRET = "secret";
	private static final String TOKEN = "token";

	private User user;
	private LoginRequest loginRequest;
	private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

	@Mock
	private Authentication authentication;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private UserDao userDao;
	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private AuthenticationService authenticationService;

	@Before
	public void setUp() {

		user = new User();
		user.setId(ID);

		loginRequest = new LoginRequest();
		loginRequest.setUsernameOrEmail(USERNAME);
		loginRequest.setPassword(SECRET);

		usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),
				loginRequest.getPassword());

	}

	@Test
	public void testAuthenticate() {

		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn(TOKEN);
		when(jwtTokenProvider.getUserIdFromJWT(TOKEN)).thenReturn(ID);
		when(userDao.findUserById(ID)).thenReturn(user);

		ResponseEntity<JwtAuthenticationResponse> response = authenticationService.authenticate(loginRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertEquals(TOKEN, response.getBody().getToken());
	}

	@Test
	public void testAuthenticateUserWhenTokenIsNull() throws Exception {

		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn(null);

		ResponseEntity<JwtAuthenticationResponse> response = authenticationService.authenticate(loginRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
		assertNull(response.getBody());
	}

	@Test
	public void testAuthenticateUnableToUpdateLastActiveDate() {

		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn(TOKEN);
		when(jwtTokenProvider.getUserIdFromJWT(TOKEN)).thenReturn(0L);
		doThrow(new ResourceNotFoundException("User not found")).when(userDao).findUserById(0L);

		ResponseEntity<JwtAuthenticationResponse> response = authenticationService.authenticate(loginRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
	}

	@Test(expected = GibSamsException.class)
	public void testAuthenticateUserThrowsException() throws Exception {

		doThrow(new BadCredentialsException("Invalid")).when(authenticationManager)
				.authenticate(usernamePasswordAuthenticationToken);

		authenticationService.authenticate(loginRequest);

	}
}
