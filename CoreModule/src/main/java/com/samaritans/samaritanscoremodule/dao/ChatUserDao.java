package com.samaritans.samaritanscoremodule.dao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.model.ChatUser;
import com.samaritans.samaritanscoremodule.repository.ChatUserRepository;

/**
 * Dao class controlling access to user repository
 * 
 * @author jackl
 *
 */
public class ChatUserDao {

	@Autowired
	private ChatUserRepository chatUserRepository;

	/**
	 * Find user by username or email. Used for login authentication.
	 * 
	 * @param usernameOrEmail
	 * @return user
	 */
	public Optional<ChatUser> findUserByUsernameOrEmail(String usernameOrEmail) {
		return chatUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
	}

	/**
	 * @param user to save
	 */
	public ChatUser save(ChatUser user) {
		return chatUserRepository.save(user);
	}

	/**
	 * Delete user with the provided id
	 * 
	 * @param id
	 */
	public void deleteUserById(Long id) {
		try {
			chatUserRepository.deleteById(id);
		} catch (IllegalArgumentException e) {
			throw new SamaritansException("Unable to delete user with id: " + id, e);
		}
	}

	/**
	 * Check if username already exists
	 * 
	 * @return boolean
	 */
	public boolean existsByUsername(String username) {
		return chatUserRepository.existsByUsername(username);
	}

}
