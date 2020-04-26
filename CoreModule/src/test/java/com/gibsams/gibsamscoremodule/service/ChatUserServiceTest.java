package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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

import com.gibsams.gibsamscoremodule.dao.ChatUserDao;
import com.gibsams.gibsamscoremodule.model.ChatUser;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;

@RunWith(MockitoJUnitRunner.class)
public class ChatUserServiceTest {

	private static final String USERNAME = "username";
	private static final String SECRET = "secret";

	private RegisterRequest registerRequest;

	@Mock
	private ChatUserDao chatUserDao;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
	private ChatUserService chatUserService;

	@Before
	public void setUp() {
		registerRequest = new RegisterRequest();
		registerRequest.setUsername(USERNAME);
		registerRequest.setPassword(SECRET);
	}

	@Test
	public void testRegisterUser() {

		when(passwordEncoder.encode(SECRET)).thenReturn(SECRET);
		chatUserService.registerUser(registerRequest);

		verify(chatUserDao, times(1)).save(Mockito.any(ChatUser.class));
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
