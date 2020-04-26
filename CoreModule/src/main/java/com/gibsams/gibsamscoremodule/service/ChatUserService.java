package com.gibsams.gibsamscoremodule.service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.ChatUserDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.ChatUser;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.utils.AppConstants;
import com.github.javafaker.Faker;

/**
 * 
 * @author jackl
 *
 */
@Service
public class ChatUserService {

	@Autowired
	private ChatUserDao chatUserDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Random random = new Random();

	private static final Logger logger = LoggerFactory.getLogger(ChatUserService.class);

	public void registerUser(RegisterRequest registerRequest) {

		ChatUser user = new ChatUser();
		user.setUsername(registerRequest.getUsername());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		try {
			chatUserDao.save(user);
		} catch (Exception e) {
			logger.error("Unable to create chat user {} during login: ", registerRequest.getUsername(), e);
			throw new GibSamsException(AppConstants.CHAT_ACCESS_ERROR_MESSAGE);
		}
	}

	public RegisterRequest buildChatUserRequest() {

		RegisterRequest userRequest = new RegisterRequest();
		userRequest.setUsername(generateUsername());
		userRequest.setPassword(generatePassword());

		return userRequest;
	}

	/**
	 * Disables chat user once disconnected
	 * 
	 * @param username
	 */
	public void disableChatUser(String username) {
		Optional<ChatUser> user = chatUserDao.findUserByUsernameOrEmail(username);

		if (!user.isPresent()) {
			throw new ResourceNotFoundException("No user found with username: " + username);
		}

		ChatUser chatUser = user.get();
		chatUser.setEnabled(false);

		try {
			chatUserDao.save(chatUser);
		} catch (Exception e) {
			logger.error("Unable to disable chat user {}", username, e);
		}
	}

	/**
	 * Generates a unique, anonymous username to keep users identity secure
	 * 
	 * @return username
	 */
	private String generateUsername() {
		logger.info("ChatUserService - generateUsername - init");

		String username = null;
		boolean usernameValid = false;
		while (!usernameValid) {
			username = createRandomUsername();
			usernameValid = validateUsername(username);
		}

		logger.info("ChatUserService - generateUsername - end");
		return username;
	}

	private boolean validateUsername(String username) {
		return (!chatUserDao.existsByUsername(username) && username.length() <= 20);
	}

	private String createRandomUsername() {
		Faker faker = new Faker();
		String username = faker.name().username();
		username += random.nextInt(10000);
		return username;
	}

	/**
	 * Generates randomised password meant for one time use for chat user.
	 * 
	 * @return
	 */
	private String generatePassword() {
		return UUID.randomUUID().toString();
	}

}
