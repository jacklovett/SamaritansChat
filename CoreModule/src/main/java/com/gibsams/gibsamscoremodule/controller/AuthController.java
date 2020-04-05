package com.gibsams.gibsamscoremodule.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gibsams.gibsamscoremodule.requests.LoginRequest;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.responses.JwtAuthenticationResponse;
import com.gibsams.gibsamscoremodule.service.AuthenticationService;
import com.gibsams.gibsamscoremodule.service.ChatUserService;

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
	 * @param registerRequest
	 * @return ApiResponse
	 */
	@PostMapping("/chat/login")
	public ResponseEntity<JwtAuthenticationResponse> chatLogin() {
		logger.info("AuthController - chatLogin - init");
		RegisterRequest chatUserRequest = chatUserService.buildChatUserRequest();
		chatUserService.registerUser(chatUserRequest);
		LoginRequest loginRequest = new LoginRequest(chatUserRequest.getUsername(), chatUserRequest.getPassword());

		return authenticationService.authenticate(loginRequest);
	}

}