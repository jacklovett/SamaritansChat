package com.gibsams.gibsamscoremodule.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.UserInfo;
import com.gibsams.gibsamscoremodule.repository.UserInfoRepository;

/**
 * 
 * @author jackl
 *
 */
public class UserInfoDao {

	@Autowired
	private UserInfoRepository userDetailsRepository;

	/**
	 * Find user in database by their id
	 * 
	 * @return User
	 */
	public UserInfo findUserDetailsById(Long id) {
		return userDetailsRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No user details found with id: " + id));

	}

	/**
	 * @param user details to save
	 */
	public UserInfo save(UserInfo user) {
		return userDetailsRepository.save(user);
	}

	/**
	 * Delete user details with the provided id
	 * 
	 * @param id
	 */
	public void deleteUserDetailsById(Long id) {
		try {
			userDetailsRepository.deleteById(id);
		} catch (IllegalArgumentException e) {
			throw new GibSamsException("Unable to delete user details with id: " + id, e);
		}
	}

	public UserInfo findUserDetailsByUserId(Long id) {
		return userDetailsRepository.findByUser(id)
				.orElseThrow(() -> new ResourceNotFoundException("No user details found for user id: " + id));
	}

}
