package com.samaritans.samaritanscoremodule.service;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.samaritans.samaritanscoremodule.dao.BoUserDao;
import com.samaritans.samaritanscoremodule.dao.RoleDao;
import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.model.Role;
import com.samaritans.samaritanscoremodule.requests.RegisterRequest;
import com.samaritans.samaritanscoremodule.requests.UserDetailsRequest;
import com.samaritans.samaritanscoremodule.requests.UserRequest;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.utils.RoleEnum;

/**
 * 
 * @author jackl
 *
 */
@Service
public class BoUserService {

	@Autowired
	private BoUserDao boUserDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(BoUserService.class);

	public void registerUser(RegisterRequest registerRequest) {

		BoUser user = new BoUser();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());
		user.setContactNumber(registerRequest.getContactNumber());

		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setRoles(determineUserRoles(registerRequest.isAdmin()));

		boUserDao.save(user);
	}

	public void updateUser(UserRequest userRequest) {
		Long userId = userRequest.getId();
		BoUser user = boUserDao.findUserById(userId);
		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setContactNumber(userRequest.getContactNumber());
		user.setRoles(determineUserRoles(userRequest.isAdmin()));
		boUserDao.save(user);
	}

	public void updatePassword(UserDetailsRequest userDetailsRequest) {
		Long userId = userDetailsRequest.getUserId();

		BoUser user = boUserDao.findUserById(userId);

		if (user == null) {
			throw new ResourceNotFoundException("No user found with id " + userId + ". Password not updated");
		}

		user.setPassword(passwordEncoder.encode(userDetailsRequest.getValue()));
		boUserDao.save(user);
	}

	public ApiResponse checkCurrentPassword(UserDetailsRequest userDetailsRequest) {
		Long userId = userDetailsRequest.getUserId();
		BoUser user = boUserDao.findUserById(userId);

		if (user == null) {
			String errorMessage = MessageFormat.format("No user found with id {0}. Unable to check password", userId);
			logger.error(errorMessage);
			return new ApiResponse(false, errorMessage);
		}

		if (passwordEncoder.matches(userDetailsRequest.getValue(), user.getPassword())) {
			return new ApiResponse(true, "Password Match");
		} else {
			return new ApiResponse(false, "Incorrect Password");
		}
	}

	/**
	 * Determine what roles to set for the new user.
	 * 
	 * @param isAdmin
	 * @return The collection of roles applicable to the user
	 */
	private Set<Role> determineUserRoles(boolean isAdmin) {
		Set<Role> userRoles = new HashSet<>();

		Role userRole = roleDao.findRoleById(RoleEnum.USER.getId());

		userRoles.add(userRole);

		if (isAdmin) {
			Role adminRole = roleDao.findRoleById(RoleEnum.ADMIN.getId());
			userRoles.add(adminRole);
		}

		return userRoles;
	}

}
