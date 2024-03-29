package com.samaritans.samaritanscoremodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.samaritans.samaritanscoremodule.dao.BoUserDao;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.requests.RegisterRequest;
import com.samaritans.samaritanscoremodule.requests.UserDetailsRequest;
import com.samaritans.samaritanscoremodule.requests.UserRequest;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.UserResponse;
import com.samaritans.samaritanscoremodule.service.BoUserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit tests for UserController
 * 
 * @author jackl
 *
 */

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private static final Long ID = 1L;
	private static final String USERNAME = "username";
	private static final String FIRST_NAME = "Jack";
	private static final String LAST_NAME = "Lovett";
	private static final String EMAIL = "a@b.com";
	private static final String SECRET = "secret";
	private static final String NEW_USERNAME_EMAIL = "newUsernameOrEmail";
	private static final String USER_URI = "http://localhost/api/users/username";

	private List<BoUser> users;
	private List<UserResponse> userResponses;

	private BoUser boUser;
	private BoUser boUser2;

	private UserResponse userResponse;

	private UserRequest userRequest;
	private RegisterRequest registerRequest;
	private UserDetailsRequest userDetailsRequest;

	private Gson gson;

	private MockMvc mockMvc;

	@Mock
	private BoUserDao boUserDao;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private BoUserService boUserService;
	@InjectMocks
	private UserController userController;

	@BeforeEach
	void setUp() throws Exception {

		gson = new GsonBuilder().serializeNulls().create();

		boUser = new BoUser();
		boUser.setId(ID);
		boUser.setUsername(USERNAME);
		boUser.setEmail(EMAIL);
		boUser.setFirstName(FIRST_NAME);
		boUser.setLastName(LAST_NAME);

		boUser2 = new BoUser();
		boUser2.setId(2L);
		boUser2.setUsername(USERNAME);
		boUser2.setEmail(EMAIL);

		users = new ArrayList<>();
		users.add(boUser);
		users.add(boUser2);

		userResponse = new UserResponse(boUser);

		userResponses = new ArrayList<>();
		userResponses.add(userResponse);
		userResponses.add(new UserResponse(boUser2));

		userRequest = new UserRequest();
		userRequest.setId(ID);

		registerRequest = new RegisterRequest();
		registerRequest.setUsername(USERNAME);
		registerRequest.setPassword(SECRET);

		userDetailsRequest = new UserDetailsRequest();
		userDetailsRequest.setUserId(ID);

		this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

	}

	@Test
	void testGetUsers() throws Exception {

		when(boUserDao.findAllUsers()).thenReturn(users);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/").contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(userResponses), response.getContentAsString());
	}

	@Test
	void testGetUsersReturnsNoUsers() throws Exception {

		users.clear();

		when(boUserDao.findAllUsers()).thenReturn(users);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/").contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[]", response.getContentAsString());

	}

	@Test
	void testGetUserById() throws Exception {

		when(boUserDao.findUserById(ID)).thenReturn(boUser);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(userResponse), response.getContentAsString());
	}

	@Test
	void testGeUserByIdNotFound() throws Exception {

		when(boUserDao.findUserById(ID)).thenReturn(null);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}

	@Test
	void testRegisterUser() throws Exception {

		String json = gson.toJson(registerRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		assertEquals(USER_URI, response.getHeader("Location"));
		assertEquals(gson.toJson(new ApiResponse(true, "User registered successfully")), response.getContentAsString());
	}

	@Test
	void testRegisterUserWhenUsernameAlreadyExists() throws Exception {

		when(boUserDao.existsByUsername(registerRequest.getUsername())).thenReturn(true);

		String json = gson.toJson(registerRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Username is already taken!")), response.getContentAsString());

	}

	@Test
	void testRegisterUserWhenEmailAlreadyExists() throws Exception {

		when(boUserDao.existsByEmail(registerRequest.getEmail())).thenReturn(true);

		String json = gson.toJson(registerRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Email Address already in use!")), response.getContentAsString());
	}

	@Test
	void testUpdateUser() throws Exception {

		String json = gson.toJson(userRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/users/edit").accept(MediaType.APPLICATION_JSON)
				.content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(true, "User updated successfully")), response.getContentAsString());
	}

	@Test
	void testDeleteUser() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/users/delete/" + ID);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(true, "User deleted successfully")), response.getContentAsString());
	}

	@Test
	void testUpdatePassword() throws Exception {

		userDetailsRequest.setValue(SECRET);

		String json = gson.toJson(userDetailsRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/users/updatePassword")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(true, "Password changed successfully!")), response.getContentAsString());
	}

	@Test
	void testUpdatePasswordWhenNoUserId() throws Exception {

		String json = gson.toJson(new UserDetailsRequest());

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/users/updatePassword")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Unknown user. Unable to change password")),
				response.getContentAsString());

	}

	@Test
	void testUpdatePasswordWhenNoPassword() throws Exception {

		userDetailsRequest.setValue(null);

		String json = gson.toJson(userDetailsRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/users/updatePassword")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "No new password provided. Unable to change password")),
				response.getContentAsString());

	}

	@Test
	void testCheckCurrentPassword() throws Exception {

		userDetailsRequest.setValue(SECRET);

		ApiResponse expectedResponse = new ApiResponse(true, "Password Match");

		when(boUserService.checkCurrentPassword(userDetailsRequest)).thenReturn(expectedResponse);

		String json = gson.toJson(userDetailsRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/users/checkCurrentPassword")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(expectedResponse), response.getContentAsString());
	}

	@Test
	void testCheckIsUsernameAvailableWhenAlreadyExists() throws Exception {

		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(boUserDao.findUserById(ID)).thenReturn(boUser);
		when(boUserDao.existsByUsername(NEW_USERNAME_EMAIL)).thenReturn(true);

		ResponseEntity<ApiResponse> response = userController.isUsernameAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertFalse(response.getBody().getSuccess());
		assertEquals("This username is unavailable. Please choose another.", response.getBody().getMessage());

	}

	@Test
	void testCheckIsUsernameAvailableWhenUserIdIsNull() {

		userDetailsRequest.setUserId(null);
		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(boUserDao.existsByUsername(NEW_USERNAME_EMAIL)).thenReturn(false);

		ResponseEntity<ApiResponse> response = userController.isUsernameAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This username is available", response.getBody().getMessage());
	}

	@Test
	void testCheckIsUsernameAvailableWhenTheSameAsUsers() {

		userDetailsRequest.setValue(USERNAME);
		when(boUserDao.findUserById(ID)).thenReturn(boUser);

		ResponseEntity<ApiResponse> response = userController.isUsernameAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This is the same username", response.getBody().getMessage());
	}

	@Test
	void testCheckIsEmailAvailableWhenAlreadyExists() throws Exception {

		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(boUserDao.findUserById(ID)).thenReturn(boUser);
		when(boUserDao.existsByEmail(NEW_USERNAME_EMAIL)).thenReturn(true);

		ResponseEntity<ApiResponse> response = userController.isEmailAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertFalse(response.getBody().getSuccess());
		assertEquals("This email is unavailable. Please choose another.", response.getBody().getMessage());

	}

	@Test
	void testCheckIsEmailAvailableWhenUserIdIsNull() {

		userDetailsRequest.setUserId(null);
		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(boUserDao.existsByEmail(NEW_USERNAME_EMAIL)).thenReturn(false);

		ResponseEntity<ApiResponse> response = userController.isEmailAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This email is available", response.getBody().getMessage());
	}

	@Test
	void testCheckIsEmailAvailableWhenTheSameAsUsers() {

		userDetailsRequest.setValue(EMAIL);

		when(boUserDao.findUserById(ID)).thenReturn(boUser);

		ResponseEntity<ApiResponse> response = userController.isEmailAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This is the same email", response.getBody().getMessage());

	}

}
