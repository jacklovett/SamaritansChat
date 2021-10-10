package com.samaritans.samaritanscoremodule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.requests.LoginRequest;
import com.samaritans.samaritanscoremodule.responses.JwtAuthenticationResponse;
import com.samaritans.samaritanscoremodule.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

	private static final String USERNAME = "username";
	private static final String SECRET = "secret";
	private static final String TOKEN = "token";

	private LoginRequest loginRequest;
	private UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

	@Mock
	private Authentication authentication;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private UserService userService;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@InjectMocks
	private AuthenticationService authenticationService;

	@BeforeEach
	void setUp() {

		loginRequest = new LoginRequest();
		loginRequest.setUsernameOrEmail(USERNAME);
		loginRequest.setPassword(SECRET);

		usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),
				loginRequest.getPassword());

	}

	@Test
	void testAuthenticate() {

		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn(TOKEN);
		when(jwtTokenProvider.getUsernameFromJWT(TOKEN)).thenReturn(USERNAME);

		ResponseEntity<JwtAuthenticationResponse> response = authenticationService.authenticate(loginRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
		assertEquals(TOKEN, response.getBody().getToken());
		verify(userService, times(1)).updateLastActive(USERNAME);
		;
	}

	@Test
	void testAuthenticateUserWhenTokenIsNull() throws Exception {

		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn(null);

		ResponseEntity<JwtAuthenticationResponse> response = authenticationService.authenticate(loginRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
		assertNull(response.getBody());
	}

	@Test
	void testAuthenticateUnableToUpdateLastActiveDate() {

		when(authenticationManager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(authentication);
		when(jwtTokenProvider.generateToken(authentication)).thenReturn(TOKEN);
		when(jwtTokenProvider.getUsernameFromJWT(TOKEN)).thenReturn(USERNAME);

		ResponseEntity<JwtAuthenticationResponse> response = authenticationService.authenticate(loginRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
	}

	@Test
	void testAuthenticateUserThrowsException() throws Exception {

		doThrow(new BadCredentialsException("Invalid")).when(authenticationManager)
				.authenticate(usernamePasswordAuthenticationToken);

		authenticationService.authenticate(loginRequest);

		assertThrows(SamaritansException.class, () -> authenticationService.authenticate(loginRequest));
	}
}
