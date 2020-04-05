package com.gibsams.gibsamscoremodule.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
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

import com.gibsams.gibsamscoremodule.dao.RoleDao;
import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.model.UserInfo;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.requests.UserDetailsRequest;
import com.gibsams.gibsamscoremodule.requests.UserRequest;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.responses.UserResponse;
import com.gibsams.gibsamscoremodule.service.BOUserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Unit tests for UserController
 * 
 * @author jackl
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

	private static final Long ID = 1L;
	private static final String USERNAME = "username";
	private static final String FIRST_NAME = "Jack";
	private static final String LAST_NAME = "Lovett";
	private static final String EMAIL = "a@b.com";
	private static final String SECRET = "secret";
	private static final String NEW_USERNAME_EMAIL = "newUsernameOrEmail";
	private static final String USER_URI = "http://localhost/api/users/username";

	private List<User> users;
	private List<UserResponse> userResponses;

	private User user;
	private User user2;

	private UserInfo userInfo;

	private UserResponse userResponse;

	private UserRequest userRequest;
	private RegisterRequest registerRequest;
	private UserDetailsRequest userDetailsRequest;

	private Gson gson;

	private MockMvc mockMvc;

	@Mock
	private UserDao userDao;
	@Mock
	private RoleDao roleDao;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private BOUserService boUserService;
	@InjectMocks
	private UserController userController;

	@Before
	public void setUp() throws Exception {

		gson = new GsonBuilder().serializeNulls().create();

		userInfo = new UserInfo();
		userInfo.setFirstName(FIRST_NAME);
		userInfo.setLastName(LAST_NAME);

		user = new User();
		user.setId(ID);
		user.setUsername(USERNAME);
		user.setEmail(EMAIL);
		user.setUserInfo(userInfo);

		user2 = new User();
		user2.setId(2L);
		user2.setUserInfo(userInfo);

		users = new ArrayList<>();
		users.add(user);
		users.add(user2);

		userResponse = new UserResponse(user, userInfo);

		userResponses = new ArrayList<>();
		userResponses.add(userResponse);
		userResponses.add(new UserResponse(user2, userInfo));

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
	public void testGetUsers() throws Exception {

		when(userDao.findAllUserResponses()).thenReturn(userResponses);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(userResponses), response.getContentAsString());
	}

	@Test
	public void testGetUsersReturnsNoUsers() throws Exception {

		userResponses.clear();

		when(userDao.findAllUserResponses()).thenReturn(userResponses);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[]", response.getContentAsString());

	}

	@Test
	public void testGetUserById() throws Exception {

		when(userDao.findUserById(ID)).thenReturn(user);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(userResponse), response.getContentAsString());
	}

	@Test
	public void testGeUserByIdNotFound() throws Exception {

		when(userDao.findUserById(ID)).thenReturn(null);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/users/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}

	@Test
	public void testRegisterUser() throws Exception {

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
	public void testRegisterUserWhenUsernameAlreadyExists() throws Exception {

		when(userDao.existsByUsername(registerRequest.getUsername())).thenReturn(true);

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
	public void testRegisterUserWhenEmailAlreadyExists() throws Exception {

		when(userDao.existsByEmail(registerRequest.getEmail())).thenReturn(true);

		String json = gson.toJson(registerRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Email Address already in use!")),
				response.getContentAsString());
	}

	@Test
	public void testUpdateUser() throws Exception {

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
	public void testDeleteUser() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/users/delete/" + ID);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(true, "User deleted successfully")), response.getContentAsString());
	}

	@Test
	public void testUpdatePassword() throws Exception {

		userDetailsRequest.setValue(SECRET);

		String json = gson.toJson(userDetailsRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/users/updatePassword")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(true, "Password changed successfully!")),
				response.getContentAsString());
	}

	@Test
	public void testUpdatePasswordWhenNoUserId() throws Exception {

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
	public void testUpdatePasswordWhenNoPassword() throws Exception {

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
	public void testCheckCurrentPassword() throws Exception {

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
	public void testCheckIsUsernameAvailableWhenAlreadyExists() throws Exception {

		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(userDao.findUserById(ID)).thenReturn(user);
		when(userDao.existsByUsername(NEW_USERNAME_EMAIL)).thenReturn(true);

		ResponseEntity<ApiResponse> response = userController.isUsernameAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertFalse(response.getBody().getSuccess());
		assertEquals("This username is unavailable. Please choose another.", response.getBody().getMessage());

	}

	@Test
	public void testCheckIsUsernameAvailableWhenUserIdIsNull() {

		userDetailsRequest.setUserId(null);
		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(userDao.existsByUsername(NEW_USERNAME_EMAIL)).thenReturn(false);

		ResponseEntity<ApiResponse> response = userController.isUsernameAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This username is available", response.getBody().getMessage());
	}

	@Test
	public void testCheckIsUsernameAvailableWhenTheSameAsUsers() {

		userDetailsRequest.setValue(USERNAME);
		when(userDao.findUserById(ID)).thenReturn(user);

		ResponseEntity<ApiResponse> response = userController.isUsernameAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This is the same username", response.getBody().getMessage());
	}

	@Test
	public void testCheckIsEmailAvailableWhenAlreadyExists() throws Exception {

		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(userDao.findUserById(ID)).thenReturn(user);
		when(userDao.existsByEmail(NEW_USERNAME_EMAIL)).thenReturn(true);

		ResponseEntity<ApiResponse> response = userController.isEmailAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertFalse(response.getBody().getSuccess());
		assertEquals("This email is unavailable. Please choose another.", response.getBody().getMessage());

	}

	@Test
	public void testCheckIsEmailAvailableWhenUserIdIsNull() {

		userDetailsRequest.setUserId(null);
		userDetailsRequest.setValue(NEW_USERNAME_EMAIL);

		when(userDao.existsByEmail(NEW_USERNAME_EMAIL)).thenReturn(false);

		ResponseEntity<ApiResponse> response = userController.isEmailAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This email is available", response.getBody().getMessage());
	}

	@Test
	public void testCheckIsEmailAvailableWhenTheSameAsUsers() {

		userDetailsRequest.setValue(EMAIL);

		when(userDao.findUserById(ID)).thenReturn(user);

		ResponseEntity<ApiResponse> response = userController.isEmailAvailable(userDetailsRequest);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
		assertEquals("This is the same email", response.getBody().getMessage());

	}

}
