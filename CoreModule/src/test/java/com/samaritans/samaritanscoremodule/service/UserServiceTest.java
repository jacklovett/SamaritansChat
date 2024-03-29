package com.samaritans.samaritanscoremodule.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.samaritans.samaritanscoremodule.dao.BoUserDao;
import com.samaritans.samaritanscoremodule.dao.ChatUserDao;
import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.model.ChatUser;
import com.samaritans.samaritanscoremodule.model.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	private static final Long ID = 1L;
	private static final String USERNAME = "username";

	;
	private BoUser boUser;
	private ChatUser chatUser;

	@Mock
	private BoUserDao boUserDao;
	@Mock
	private ChatUserDao chatUserDao;
	@InjectMocks
	private UserService userService;

	@BeforeEach
	void setUp() {

		chatUser = new ChatUser();
		chatUser.setId(ID);
		chatUser.setUsername(USERNAME);

		boUser = new BoUser();
		boUser.setId(ID);
		boUser.setUsername(USERNAME);
	}

	@Test
	void testGetUserByUsernameForBoUser() {

		when(boUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(boUser));
		User user = userService.getUserByUsername(USERNAME);

		assertNotNull(user);
		assertEquals(USERNAME, user.getUsername());
	}

	@Test
	void testGetUserByUsernameForChatUser() {

		when(chatUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(chatUser));
		User user = userService.getUserByUsername(USERNAME);

		assertNotNull(user);
		assertEquals(USERNAME, user.getUsername());
	}

	@Test
	void testGetUserByUsernameWhenNoUserFound() {
		userService.getUserByUsername(USERNAME);

		assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(USERNAME));
	}

	@Test
	void testUpdateLastActiveForBoUser() {
		when(boUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(boUser));
		userService.updateLastActive(USERNAME);
		verify(boUserDao, times(1)).save(Mockito.any(BoUser.class));
	}

	@Test
	void testUpdateLastActiveForChatUser() {
		when(chatUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(chatUser));
		userService.updateLastActive(USERNAME);
		verify(chatUserDao, times(1)).save(Mockito.any(ChatUser.class));
	}
}
