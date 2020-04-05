package com.gibsams.gibsamscoremodule.service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.RoleDao;
import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.model.Role;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.utils.AppConstants;
import com.gibsams.gibsamscoremodule.utils.RoleEnum;
import com.github.javafaker.Faker;

/**
 * 
 * @author jackl
 *
 */
@Service
public class ChatUserService implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private Random random = new Random();

	private static final Logger logger = LoggerFactory.getLogger(ChatUserService.class);

	@Override
	public void registerUser(RegisterRequest registerRequest) {

		User user = new User();
		user.setChatUser(true);
		user.setUsername(registerRequest.getUsername());

		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		Set<Role> userRoles = new HashSet<>();

		try {
			Role userRole = roleDao.findRoleById(RoleEnum.CHAT.getId());
			userRoles.add(userRole);
			user.setRoles(userRoles);
			userDao.save(user);
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

		User user = userDao.findUserByUsernameOrEmail(username);
		user.setEnabled(false);
		userDao.save(user);
		logger.info("Chat user {} disabled", username);
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
		return (!userDao.existsByUsername(username) && username.length() <= 20);
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
