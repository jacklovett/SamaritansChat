package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.gibsams.gibsamscoremodule.dao.BoUserDao;
import com.gibsams.gibsamscoremodule.dao.ChatUserDao;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.BoUser;
import com.gibsams.gibsamscoremodule.model.ChatUser;
import com.gibsams.gibsamscoremodule.model.User;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

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

	@Before
	public void setUp() {

		chatUser = new ChatUser();
		chatUser.setId(ID);
		chatUser.setUsername(USERNAME);

		boUser = new BoUser();
		boUser.setId(ID);
		boUser.setUsername(USERNAME);
	}

	@Test
	public void testGetUserByUsernameForBoUser() {

		when(boUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(boUser));
		User user = userService.getUserByUsername(USERNAME);

		assertNotNull(user);
		assertEquals(USERNAME, user.getUsername());
	}

	@Test
	public void testGetUserByUsernameForChatUser() {

		when(chatUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(chatUser));
		User user = userService.getUserByUsername(USERNAME);

		assertNotNull(user);
		assertEquals(USERNAME, user.getUsername());
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testGetUserByUsernameWhenNoUserFound() {
		userService.getUserByUsername(USERNAME);
	}

	@Test
	public void testUpdateLastActiveForBoUser() {
		when(boUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(boUser));
		userService.updateLastActive(USERNAME);
		verify(boUserDao, times(1)).save(Mockito.any(BoUser.class));
	}

	@Test
	public void testUpdateLastActiveForChatUser() {
		when(chatUserDao.findUserByUsernameOrEmail(USERNAME)).thenReturn(Optional.of(chatUser));
		userService.updateLastActive(USERNAME);
		verify(chatUserDao, times(1)).save(Mockito.any(ChatUser.class));
	}
}
