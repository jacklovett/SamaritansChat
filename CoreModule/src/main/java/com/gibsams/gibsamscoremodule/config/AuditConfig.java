package com.gibsams.gibsamscoremodule.config;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gibsams.gibsamscoremodule.security.UserPrincipal;

/**
 * 
 * @author jackl
 *
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

	@Bean
	public AuditorAware<String> auditorProvider() {
		return new SpringSecurityAuditAwareImpl();
	}
}

class SpringSecurityAuditAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {
		String auditorName = "System";
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			return Optional.of(auditorName);
		}

		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

		if (userPrincipal.getUserInfo() != null) {
			auditorName = userPrincipal.getUserInfo().getFirstName() + " " + userPrincipal.getUserInfo().getLastName();
		}
		return Optional.of(auditorName);
	}
}