package com.samaritans.samaritanscoremodule.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.samaritans.samaritanscoremodule.dao.BoUserDao;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.requests.RegisterRequest;
import com.samaritans.samaritanscoremodule.requests.UserDetailsRequest;
import com.samaritans.samaritanscoremodule.requests.UserRequest;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.UserResponse;
import com.samaritans.samaritanscoremodule.service.BoUserService;

/**
 * User REST controller
 * 
 * @author jackl
 *
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private BoUserDao boUserDao;

	@Autowired
	private BoUserService boUserService;

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	/**
	 * Endpoint to get users for BackOffice application
	 * 
	 * @return List of Users
	 */
	@GetMapping()
	public ResponseEntity<List<UserResponse>> getUsers() {
		logger.info("UserController - getUsers - init");

		List<UserResponse> users = boUserDao.findAllUsers().stream().map(UserResponse::new)
				.collect(Collectors.toList());

		if (users.isEmpty()) {
			logger.info("No users found in database");
		}

		return ResponseEntity.ok(users);
	}

	/**
	 * Get user by id endpoint
	 * 
	 * @param id
	 * @return User
	 */
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
		logger.info("UserController - getUserById - init");
		UserResponse userResponse = null;
		BoUser user = boUserDao.findUserById(id);
		if (user != null) {
			userResponse = new UserResponse(user);
		}

		return ResponseEntity.of(Optional.ofNullable(userResponse));
	}

	/**
	 * User update endpoint
	 * 
	 * @param userRequest
	 * @return ApiResponse
	 */
	@PutMapping("/edit")
	public ResponseEntity<ApiResponse> updateUser(@Valid @RequestBody UserRequest userRequest) {
		logger.info("UserController - updateUser - init");
		boUserService.updateUser(userRequest);
		return ResponseEntity.ok(new ApiResponse(true, "User updated successfully"));
	}

	/**
	 * User registration endpoint.
	 * 
	 * @param registerRequest
	 * @return ApiResponse
	 */
	@PostMapping("/register")
	public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
		logger.info("UserController - registerUser - init");
		if (boUserDao.existsByUsername(registerRequest.getUsername())) {
			return new ResponseEntity<>(new ApiResponse(false, "Username is already taken!"), HttpStatus.BAD_REQUEST);
		}

		if (boUserDao.existsByEmail(registerRequest.getEmail())) {
			return new ResponseEntity<>(new ApiResponse(false, "Email Address already in use!"),
					HttpStatus.BAD_REQUEST);
		}

		boUserService.registerUser(registerRequest);

		URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{username}")
				.buildAndExpand(registerRequest.getUsername()).toUri();

		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
	}

	/**
	 * Delete user endpoint
	 * 
	 * @param id
	 * @return Api response
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
		logger.info("UserController - deleteUser - init");
		boUserDao.deleteUserById(id);
		return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
	}

	/**
	 * Update bo users password endpoint
	 * 
	 * @param userDetailsRequest
	 * @return ApiResponse
	 */
	@PutMapping("/updatePassword")
	public ResponseEntity<ApiResponse> updatePassword(@Valid @RequestBody UserDetailsRequest userDetailsRequest) {
		logger.info("UserController - updatePassword - init");

		if (userDetailsRequest.getUserId() == null) {
			return new ResponseEntity<>(new ApiResponse(false, "Unknown user. Unable to change password"),
					HttpStatus.BAD_REQUEST);
		}

		if (StringUtils.isBlank(userDetailsRequest.getValue())) {
			return new ResponseEntity<>(new ApiResponse(false, "No new password provided. Unable to change password"),
					HttpStatus.BAD_REQUEST);
		}

		boUserService.updatePassword(userDetailsRequest);
		return ResponseEntity.ok(new ApiResponse(true, "Password changed successfully!"));
	}

	/**
	 * Endpoint to check if value matches the authenticated users current password
	 * 
	 * @param userDetailsRequest
	 * @return ApiResponse
	 */
	@PostMapping("/checkCurrentPassword")
	public ResponseEntity<ApiResponse> checkCurrentPassword(@Valid @RequestBody UserDetailsRequest userDetailsRequest) {
		logger.info("UserController - checkCurrentPassword - init");
		return ResponseEntity.ok(boUserService.checkCurrentPassword(userDetailsRequest));
	}

	/**
	 * Check whether username is available. Returns false if username is already
	 * taken and cannot be used.
	 * 
	 * @param userDetailsRequest
	 * @return ApiResponse
	 */
	@PostMapping("/checkUsernameAvailability")
	public ResponseEntity<ApiResponse> isUsernameAvailable(@Valid @RequestBody UserDetailsRequest userDetailsRequest) {
		logger.info("UserController - isUsernameAvailabile - init");

		String username = userDetailsRequest.getValue();

		if (userDetailsRequest.getUserId() != null) {
			BoUser user = boUserDao.findUserById(userDetailsRequest.getUserId());
			if (user.getUsername().equals(username))
				return ResponseEntity.ok(new ApiResponse(true, "This is the same username"));
		}

		if (!boUserDao.existsByUsername(username))
			return ResponseEntity.ok(new ApiResponse(true, "This username is available"));
		else
			return ResponseEntity.ok(new ApiResponse(false, "This username is unavailable. Please choose another."));
	}

	/**
	 * Check whether email is available. Returns false if email is already taken and
	 * cannot be used.
	 * 
	 * @param userDetailsRequest
	 * @return ApiResponse
	 */
	@PostMapping("/checkEmailAvailablilty")
	public ResponseEntity<ApiResponse> isEmailAvailable(@Valid @RequestBody UserDetailsRequest userDetailsRequest) {
		logger.info("UserController - isEmailAvailabile - init");

		String email = userDetailsRequest.getValue();

		if (userDetailsRequest.getUserId() != null) {
			BoUser user = boUserDao.findUserById(userDetailsRequest.getUserId());
			if (user.getEmail().equals(email))
				return ResponseEntity.ok(new ApiResponse(true, "This is the same email"));
		}

		if (!boUserDao.existsByEmail(email))
			return ResponseEntity.ok(new ApiResponse(true, "This email is available"));
		else
			return ResponseEntity.ok(new ApiResponse(false, "This email is unavailable. Please choose another."));
	}

}
