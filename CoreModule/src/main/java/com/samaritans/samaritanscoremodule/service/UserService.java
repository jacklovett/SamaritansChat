package com.samaritans.samaritanscoremodule.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samaritans.samaritanscoremodule.dao.BoUserDao;
import com.samaritans.samaritanscoremodule.dao.ChatUserDao;
import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.model.ChatUser;
import com.samaritans.samaritanscoremodule.model.User;

@Service
public class UserService {

	@Autowired
	private ChatUserDao chatUserDao;

	@Autowired
	private BoUserDao boUserDao;

	public User getUserByUsername(String usernameOrEmail) {
		Optional<BoUser> boUser = boUserDao.findUserByUsernameOrEmail(usernameOrEmail);

		if (boUser.isPresent()) {
			return boUser.get();
		}

		Optional<ChatUser> chatUser = chatUserDao.findUserByUsernameOrEmail(usernameOrEmail);

		if (chatUser.isPresent()) {
			return chatUser.get();
		}

		throw new ResourceNotFoundException("No user found with username/email: " + usernameOrEmail);
	}

	public void updateLastActive(String username) {
		User user = getUserByUsername(username);
		user.setLastActive(Instant.now());

		if (user instanceof BoUser) {
			boUserDao.save((BoUser) user);
		} else {
			chatUserDao.save((ChatUser) user);
		}
	}
}
