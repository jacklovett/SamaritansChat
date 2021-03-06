package com.samaritans.samaritanscoremodule.security;

import java.util.Collection;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import com.samaritans.samaritanscoremodule.exception.SamaritansException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Component to provide JwtToken
 * 
 * @author jackl
 *
 */
@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationInMs}")
	private int jwtExpirationInMs;

	public String generateToken(Authentication authentication) {

		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

		JSONObject userDetails = getUserDetailsForResponse(userPrincipal);

		return Jwts.builder().setSubject(userDetails.toString()).setIssuedAt(new Date()).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String getUsernameFromJWT(String token) {
		Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();

		String username = "";
		JSONObject subject;

		try {
			subject = new JSONObject(claims.getSubject());
			username = subject.getString("username");
		} catch (JSONException e) {
			throw new SamaritansException("getUsernameFromJWT - Unable to get username from token", e);
		}

		return username;
	}

	public boolean validateToken(String authToken) {

		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
			return true;
		} catch (SignatureException ex) {
			logger.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		return false;
	}

	/**
	 * Creates JSONObject with the logged in user's details ready for JWT token
	 * 
	 * @param userPrincipal
	 * @return JSONObject
	 */
	private JSONObject getUserDetailsForResponse(UserPrincipal userPrincipal) {
		JSONObject userDetails = new JSONObject();
		try {
			userDetails.put("userId", Long.toString(userPrincipal.getId()));
			userDetails.put("admin", isUserAdmin(userPrincipal));
			userDetails.put("username", userPrincipal.getUsername());
		} catch (JSONException e) {
			throw new SamaritansException("Unable to set user details when generating JWT token", e);
		}
		return userDetails;
	}

	/**
	 * Check the granted user roles for the logged in user
	 * 
	 * @param authentication
	 * @return isAdmin
	 */
	private boolean isUserAdmin(UserPrincipal currentUser) {
		Collection<? extends GrantedAuthority> userRoles = currentUser.getAuthorities();

		for (GrantedAuthority role : userRoles) {
			if (role.getAuthority().contains("ADMIN")) {
				return true;
			}
		}

		return false;
	}
}