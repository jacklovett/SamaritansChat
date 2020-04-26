package com.gibsams.gibsamscoremodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.security.UserPrincipal;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) {

		User user = userService.getUserByUsername(usernameOrEmail);

		if (!user.isEnabled()) {
			throw new GibSamsException("The user " + user.getUsername() + " is disabled");
		}
		// User found - create user to be stored in session
		return UserPrincipal.create(user);
	}

}
