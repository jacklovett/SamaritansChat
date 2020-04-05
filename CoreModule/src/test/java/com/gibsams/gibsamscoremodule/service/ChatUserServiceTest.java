package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
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
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.Role;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.utils.RoleEnum;

@RunWith(MockitoJUnitRunner.class)
public class ChatUserServiceTest {

	private static final String USERNAME = "username";
	private static final String SECRET = "secret";

	private Role chatRole;
	private RegisterRequest registerRequest;

	@Mock
	private RoleDao roleDao;
	@Mock
	private UserDao userDao;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
	private ChatUserService chatUserService;

	@Before
	public void setUp() {

		registerRequest = new RegisterRequest();
		registerRequest.setUsername(USERNAME);
		registerRequest.setPassword(SECRET);

		chatRole = new Role(RoleEnum.CHAT.getName());
	}

	@Test
	public void testRegisterUser() {

		when(passwordEncoder.encode(SECRET)).thenReturn(SECRET);
		when(roleDao.findRoleById(RoleEnum.CHAT.getId())).thenReturn(chatRole);

		chatUserService.registerUser(registerRequest);

		verify(userDao, times(1)).save(Mockito.any(User.class));
	}

	@Test(expected = GibSamsException.class)
	public void testRegisterUserWhenRoleNotFound() {

		when(passwordEncoder.encode(SECRET)).thenReturn(SECRET);
		doThrow(new ResourceNotFoundException("Role not found")).when(roleDao).findRoleById(RoleEnum.CHAT.getId());

		chatUserService.registerUser(registerRequest);
	}

	@Test
	public void testBuildChatUserRequest() {

		RegisterRequest registerRequest = chatUserService.buildChatUserRequest();

		assertNotNull(registerRequest);
		assertNotNull(registerRequest.getPassword());
		assertNotNull(registerRequest.getUsername());
		assertTrue(registerRequest.getUsername().length() <= 20);
	}

}
