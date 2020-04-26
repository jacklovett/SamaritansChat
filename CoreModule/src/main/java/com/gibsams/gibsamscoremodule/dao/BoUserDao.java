package com.gibsams.gibsamscoremodule.dao;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.BoUser;
import com.gibsams.gibsamscoremodule.repository.BoUserRepository;

/**
 * Dao class controlling access to bouser repository
 * 
 * @author jackl
 *
 */
public class BoUserDao {

	@Autowired
	private BoUserRepository boUserRepository;

	public List<BoUser> findAllUsers() {
		return boUserRepository.findAll().stream().collect(Collectors.toList());
	}

	/**
	 * Find boUser in database by their id
	 * 
	 * @return BoUser
	 */
	public BoUser findUserById(Long id) {
		return boUserRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No user found with id: " + id));
	}

	/**
	 * Find user by email. Used for login authentication.
	 * 
	 * @param usernameOrEmail
	 * @return user
	 */
	public Optional<BoUser> findUserByUsernameOrEmail(String usernameOrEmail) {
		return boUserRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
	}

	/**
	 * @param user to save
	 */
	public BoUser save(BoUser user) {
		return boUserRepository.save(user);
	}

	/**
	 * Delete user with the provided id
	 * 
	 * @param id
	 */
	public void deleteUserById(Long id) {
		try {
			boUserRepository.deleteById(id);
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
		return boUserRepository.existsByUsername(username);
	}

	/**
	 * Check if email address already exists
	 * 
	 * @return boolean
	 */
	public boolean existsByEmail(String email) {
		return boUserRepository.existsByEmail(email);
	}

}
