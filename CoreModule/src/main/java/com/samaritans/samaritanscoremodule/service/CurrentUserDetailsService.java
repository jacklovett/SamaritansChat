package com.samaritans.samaritanscoremodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.model.User;
import com.samaritans.samaritanscoremodule.security.UserPrincipal;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) {

		User user = userService.getUserByUsername(usernameOrEmail);

		if (!user.isEnabled()) {
			throw new SamaritansException("The user " + user.getUsername() + " is disabled");
		}
		// User found - create user to be stored in session
		return UserPrincipal.create(user);
	}

}
