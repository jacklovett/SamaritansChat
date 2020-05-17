package com.samaritans.samaritanscoremodule.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.exception.InvalidReCaptchaException;
import com.samaritans.samaritanscoremodule.exception.ReCaptchaUnavailableException;
import com.samaritans.samaritanscoremodule.requests.LoginRequest;
import com.samaritans.samaritanscoremodule.requests.RegisterRequest;
import com.samaritans.samaritanscoremodule.responses.JwtAuthenticationResponse;
import com.samaritans.samaritanscoremodule.service.AuthenticationService;
import com.samaritans.samaritanscoremodule.service.ChatUserService;
import com.samaritans.samaritanscoremodule.service.ReCaptchaService;

/**
 * Authentication REST endpoint to handle user login
 * 
 * @author jackl
 *
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private ChatUserService chatUserService;

	@Autowired
	private ReCaptchaService reCaptchaService;

	@Autowired
	private AuthenticationService authenticationService;

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	@PostMapping("/login")
	public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		logger.info("AuthController - authenticateUser - init");
		return authenticationService.authenticate(loginRequest);
	}

	/**
	 * User authentication/registration endpoint for public facing application.
	 * 
	 * @param token - reCaptcha response
	 * @return ApiResponse
	 */
	@PostMapping("/chat/login")
	public ResponseEntity<JwtAuthenticationResponse> chatLogin(@Valid @RequestBody String token) {
		logger.info("AuthController - chatLogin - init");
		try {
			reCaptchaService.verifyResponse(token);
		} catch (ReCaptchaUnavailableException | InvalidReCaptchaException ex) {
			throw new SamaritansException("Login failed: Blocked by ReCaptcha", ex);
		}
		RegisterRequest chatUserRequest = chatUserService.buildChatUserRequest();
		chatUserService.registerUser(chatUserRequest);
		LoginRequest loginRequest = new LoginRequest(chatUserRequest.getUsername(), chatUserRequest.getPassword());
		return authenticationService.authenticate(loginRequest);
	}

}