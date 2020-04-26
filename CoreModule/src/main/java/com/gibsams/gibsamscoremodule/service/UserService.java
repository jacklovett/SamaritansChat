package com.gibsams.gibsamscoremodule.service;

import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.BoUserDao;
import com.gibsams.gibsamscoremodule.dao.ChatUserDao;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.BoUser;
import com.gibsams.gibsamscoremodule.model.ChatUser;
import com.gibsams.gibsamscoremodule.model.User;

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
