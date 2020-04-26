package com.gibsams.gibsamscoremodule.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.requests.LoginRequest;
import com.gibsams.gibsamscoremodule.responses.JwtAuthenticationResponse;
import com.gibsams.gibsamscoremodule.security.JwtTokenProvider;

/**
 * 
 * @author jackl
 *
 */
@Service
public class AuthenticationService {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtTokenProvider tokenProvider;

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	public ResponseEntity<JwtAuthenticationResponse> authenticate(LoginRequest loginRequest) {
		JwtAuthenticationResponse jwtResponse = null;
		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
					loginRequest.getUsernameOrEmail(), loginRequest.getPassword()));
		} catch (AuthenticationException ex) {
			throw new GibSamsException("You have entered an invalid username or password", ex);
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken(authentication);

		if (!StringUtils.isEmpty(jwt)) {
			logger.info("Authentication token created successfully");
			updateLastActive(jwt);
			jwtResponse = new JwtAuthenticationResponse(jwt);
		}

		return ResponseEntity.of(Optional.ofNullable(jwtResponse));
	}

	/**
	 * Updates the users last active date
	 * 
	 * @param jwt
	 */
	private void updateLastActive(String jwt) {
		String username = tokenProvider.getUsernameFromJWT(jwt);
		try {
			userService.updateLastActive(username);
		} catch (Exception e) {
			logger.error("Unable to update users last active date: ", e);
		}
	}
}
