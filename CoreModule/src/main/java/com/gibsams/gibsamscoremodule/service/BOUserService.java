package com.gibsams.gibsamscoremodule.service;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.RoleDao;
import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.Role;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.model.UserInfo;
import com.gibsams.gibsamscoremodule.requests.RegisterRequest;
import com.gibsams.gibsamscoremodule.requests.UserDetailsRequest;
import com.gibsams.gibsamscoremodule.requests.UserRequest;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.utils.RoleEnum;

/**
 * 
 * @author jackl
 *
 */
@Service
public class BOUserService implements UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private static final Logger logger = LoggerFactory.getLogger(BOUserService.class);

	@Override
	public void registerUser(RegisterRequest registerRequest) {
		UserInfo userInfo = new UserInfo();
		userInfo.setFirstName(registerRequest.getFirstName());
		userInfo.setLastName(registerRequest.getLastName());
		userInfo.setContactNumber(registerRequest.getContactNumber());

		User user = new User();
		user.setUsername(registerRequest.getUsername());
		user.setEmail(registerRequest.getEmail());
		user.setChatUser(false);

		user.setUserInfo(userInfo);
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setRoles(determineUserRoles(registerRequest.isAdmin()));

		userDao.save(user);
	}

	public void updateUser(UserRequest userRequest) {
		Long userId = userRequest.getId();
		User user = userDao.findUserById(userId);
		if (user.getUserInfo() != null) {
			user.getUserInfo().setFirstName(userRequest.getFirstName());
			user.getUserInfo().setLastName(userRequest.getLastName());
			user.getUserInfo().setContactNumber(userRequest.getContactNumber());
		}
		user.setRoles(determineUserRoles(userRequest.isAdmin()));
		userDao.save(user);

	}

	public void updatePassword(UserDetailsRequest userDetailsRequest) {
		Long userId = userDetailsRequest.getUserId();

		User user = userDao.findUserById(userId);

		if (user != null) {
			user.setPassword(passwordEncoder.encode(userDetailsRequest.getValue()));
			userDao.save(user);
		} else {
			throw new ResourceNotFoundException("No user found with id " + userId + ". Password not updated");
		}

	}

	public ApiResponse checkCurrentPassword(UserDetailsRequest userDetailsRequest) {
		Long userId = userDetailsRequest.getUserId();
		User user = userDao.findUserById(userId);

		if (user != null) {
			if (passwordEncoder.matches(userDetailsRequest.getValue(), user.getPassword())) {
				return new ApiResponse(true, "Password Match");
			} else {
				return new ApiResponse(false, "Incorrect Password");
			}
		} else {
			String errorMessage = MessageFormat.format("No user found with id {0}. Unable to check password", userId);
			logger.error(errorMessage);
			return new ApiResponse(false, errorMessage);
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
