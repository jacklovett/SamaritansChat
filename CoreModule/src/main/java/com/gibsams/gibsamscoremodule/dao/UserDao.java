package com.gibsams.gibsamscoremodule.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.repository.UserRepository;
import com.gibsams.gibsamscoremodule.responses.UserResponse;

/**
 * Dao class controlling access to user repository
 * 
 * @author jackl
 *
 */
public class UserDao {

	@Autowired
	private UserRepository userRepository;

	/**
	 * Find all users and return as a list of UserResponses
	 * 
	 * @return List of UserResponses
	 */
	public List<User> findAllUsers() {
		return userRepository.findAll().stream().collect(Collectors.toList());
	}

	public List<UserResponse> findAllUserResponses() {
		return userRepository.findByUserInfoNotNull().stream().map(u -> new UserResponse(u, u.getUserInfo()))
				.collect(Collectors.toList());
	}

	/**
	 * Find user in database by their id
	 * 
	 * @return User
	 */
	public User findUserById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No user found with id: " + id));
	}

	/**
	 * Find user by username or email. Used for login authentication.
	 * 
	 * @param usernameOrEmail
	 * @return user
	 */
	public User findUserByUsernameOrEmail(String usernameOrEmail) {
		return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
				() -> new ResourceNotFoundException("No user found with username/email: " + usernameOrEmail));
	}

	/**
	 * @param user to save
	 */
	public User save(User user) {
		return userRepository.save(user);
	}

	/**
	 * Delete user with the provided id
	 * 
	 * @param id
	 */
	public void deleteUserById(Long id) {
		try {
			userRepository.deleteById(id);
		} catch (IllegalArgumentException e) {
			throw new GibSamsException("Unable to delete user with id: " + id, e);
		}
	}

	/**
	 * Check if username already exists
	 * 
	 * @return boolean
	 */
	public boolean existsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}

	/**
	 * Check if email address already exists
	 * 
	 * @return boolean
	 */
	public boolean existsByEmail(String email) {
		return userRepository.existsByEmail(email);
	}

}
