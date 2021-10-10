package com.samaritans.samaritanscoremodule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.samaritans.samaritanscoremodule.dao.ChatUserDao;
import com.samaritans.samaritanscoremodule.model.ChatUser;
import com.samaritans.samaritanscoremodule.requests.RegisterRequest;

@ExtendWith(MockitoExtension.class)
class ChatUserServiceTest {

	private static final String USERNAME = "username";
	private static final String SECRET = "secret";

	private RegisterRequest registerRequest;

	@Mock
	private ChatUserDao chatUserDao;
	@Mock
	private PasswordEncoder passwordEncoder;
	@InjectMocks
	private ChatUserService chatUserService;

	@BeforeEach
	void setUp() {
		registerRequest = new RegisterRequest();
		registerRequest.setUsername(USERNAME);
		registerRequest.setPassword(SECRET);
	}

	@Test
	void testRegisterUser() {

		when(passwordEncoder.encode(SECRET)).thenReturn(SECRET);
		chatUserService.registerUser(registerRequest);

		verify(chatUserDao, times(1)).save(Mockito.any(ChatUser.class));
	}

	@Test
	void testBuildChatUserRequest() {

		RegisterRequest registerRequest = chatUserService.buildChatUserRequest();

		assertNotNull(registerRequest);
		assertNotNull(registerRequest.getPassword());
		assertNotNull(registerRequest.getUsername());
		assertTrue(registerRequest.getUsername().length() <= 20);
	}

}
