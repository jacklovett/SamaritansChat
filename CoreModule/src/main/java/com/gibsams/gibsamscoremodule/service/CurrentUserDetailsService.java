package com.gibsams.gibsamscoremodule.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.security.UserPrincipal;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

	@Autowired
	private UserDao userDao;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String usernameOrEmail) {
		User user = userDao.findUserByUsernameOrEmail(usernameOrEmail);
		if (!user.isEnabled()) {
			throw new GibSamsException("The user " + user.getUsername() + " is disabled");
		}
		// User found - create user to be stored in session
		return UserPrincipal.create(user);
	}

	// This method is used by JWTAuthenticationFilter
	@Transactional
	public UserDetails loadUserById(Long id) {
		User user = userDao.findUserById(id);

		if (!user.isEnabled()) {
			throw new GibSamsException("The user " + user.getUsername() + " is disabled");
		}
		return UserPrincipal.create(user);
	}

}
