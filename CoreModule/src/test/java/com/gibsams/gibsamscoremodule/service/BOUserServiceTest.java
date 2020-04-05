package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.gibsams.gibsamscoremodule.dao.RoleDao;
import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.Role;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.model.UserInfo;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.requests.UserDetailsRequest;
import com.gibsams.gibsamscoremodule.requests.UserRequest;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.utils.RoleEnum;

@RunWith(MockitoJUnitRunner.class)
public class BOUserServiceTest {

	private static final Long ID = 1L;
	private static final String USERNAME = "username";
	private static final String FIRST_NAME = "Jack";
	private static final String LAST_NAME = "Lovett";
	private static final String EMAIL = "a@b.com";
	private static final String SECRET = "secret";
	private static final String ENCODED_SECRET = "fgdhsdvergsdvvsdf";
	private static final String CONTACT_NUMBER = "07777777777";

	private User user;
	private UserInfo userInfo;
	private Role userRole;
	private Role adminRole;
	private UserRequest userRequest;
	private RegisterRequest registerRequest;
	private UserDetailsRequest userDetailsRequest;

	@Mock
	private UserDao userDao;
	@Mock
	private RoleDao roleDao;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
	private BOUserService boUserService;

	@Before
	public void setUp() {

		userRequest = new UserRequest();
		userRequest.setFirstName(FIRST_NAME);
		userRequest.setLastName(LAST_NAME);
		userRequest.setEmail(EMAIL);
		userRequest.setContactNumber(CONTACT_NUMBER);
		userRequest.setAdmin(true);

		registerRequest = new RegisterRequest();
		registerRequest.setUsername(USERNAME);
		registerRequest.setPassword(SECRET);
		registerRequest.setFirstName(FIRST_NAME);
		registerRequest.setLastName(LAST_NAME);
		registerRequest.setEmail(EMAIL);
		registerRequest.setContactNumber(CONTACT_NUMBER);
		registerRequest.setAdmin(true);

		userInfo = new UserInfo();
		userInfo.setFirstName(FIRST_NAME);
		userInfo.setLastName(LAST_NAME);

		user = new User();
		user.setId(ID);
		user.setUsername(USERNAME);
		user.setEmail(EMAIL);
		user.setUserInfo(userInfo);
		user.setPassword(ENCODED_SECRET);

		userDetailsRequest = new UserDetailsRequest();
		userDetailsRequest.setUserId(ID);
		userDetailsRequest.setValue(SECRET);
	}

	@Test
	public void testRegisterUser() {

		when(passwordEncoder.encode(SECRET)).thenReturn(ENCODED_SECRET);
		when(roleDao.findRoleById(RoleEnum.USER.getId())).thenReturn(userRole);
		when(roleDao.findRoleById(RoleEnum.ADMIN.getId())).thenReturn(adminRole);

		boUserService.registerUser(registerRequest);

		verify(userDao, times(1)).save(Mockito.any(User.class));
	}

	@Test
	public void testUpdateUser() {

		userRequest.setId(ID);
		userRequest.setAdmin(false);

		when(userDao.findUserById(ID)).thenReturn(user);

		when(roleDao.findRoleById(RoleEnum.USER.getId())).thenReturn(userRole);

		boUserService.updateUser(userRequest);

		verify(userDao, times(1)).save(Mockito.any(User.class));
	}

	@Test
	public void testUpdatePassword() {

		when(userDao.findUserById(ID)).thenReturn(user);
		when(passwordEncoder.encode(SECRET)).thenReturn(ENCODED_SECRET);

		boUserService.updatePassword(userDetailsRequest);

		user.setPassword(ENCODED_SECRET);
		verify(userDao, times(1)).save(user);

	}

	@Test(expected = ResourceNotFoundException.class)
	public void testUpdatePasswordWhenUserNotFound() {

		when(userDao.findUserById(ID)).thenReturn(null);
		boUserService.updatePassword(userDetailsRequest);
	}

	@Test
	public void checkCurrentPassword() {

		when(userDao.findUserById(ID)).thenReturn(user);
		when(passwordEncoder.matches(SECRET, ENCODED_SECRET)).thenReturn(true);

		ApiResponse response = boUserService.checkCurrentPassword(userDetailsRequest);

		assertEquals(new ApiResponse(true, "Password Match"), response);
	}

	@Test
	public void checkCurrentPasswordWhenWrong() {

		when(userDao.findUserById(ID)).thenReturn(user);
		when(passwordEncoder.matches(SECRET, ENCODED_SECRET)).thenReturn(false);

		ApiResponse response = boUserService.checkCurrentPassword(userDetailsRequest);

		assertEquals(new ApiResponse(false, "Incorrect Password"), response);
	}

	@Test
	public void checkCurrentPasswordWhenUserNotFound() {

		when(userDao.findUserById(ID)).thenReturn(null);

		ApiResponse response = boUserService.checkCurrentPassword(userDetailsRequest);

		assertEquals(new ApiResponse(false, "No user found with id " + ID + ". Unable to check password"), response);
	}
}
