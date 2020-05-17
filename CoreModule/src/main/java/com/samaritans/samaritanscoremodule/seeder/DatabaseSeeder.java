package com.samaritans.samaritanscoremodule.seeder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.model.ChatConfig;
import com.samaritans.samaritanscoremodule.model.Role;
import com.samaritans.samaritanscoremodule.repository.BoUserRepository;
import com.samaritans.samaritanscoremodule.repository.ChatConfigRepository;
import com.samaritans.samaritanscoremodule.repository.RoleRepository;
import com.samaritans.samaritanscoremodule.requests.RegisterRequest;
import com.samaritans.samaritanscoremodule.service.BoUserService;
import com.samaritans.samaritanscoremodule.utils.RoleEnum;

@Component
public class DatabaseSeeder {

	@Autowired
	private BoUserService boUserService;
	@Autowired
	private ChatConfigRepository chatConfigRepository;
	@Autowired
	private BoUserRepository boUserRepository;
	@Autowired
	private RoleRepository roleRepository;

	private static final Logger logger = LoggerFactory.getLogger(DatabaseSeeder.class);

	@EventListener
	public void seed(ContextRefreshedEvent event) {
		seedRoles();
		seedChatConfig();
		seedBoUsers();
	}

	private void seedBoUsers() {

		RegisterRequest registerRequest = new RegisterRequest();
		registerRequest.setFirstName("Jack");
		registerRequest.setLastName("Lovett");
		registerRequest.setUsername("lovettj");
		registerRequest.setEmail("jack.l.lovett@gmail.com");
		registerRequest.setPassword("ChangeMe1112!");
		registerRequest.setContactNumber("+447904804644");
		registerRequest.setAdmin(true);

		Optional<BoUser> user = boUserRepository.findByUsernameOrEmail(registerRequest.getUsername(),
				registerRequest.getEmail());

		if (user.isPresent()) {
			logger.info("User already found with username {} or email {}. Seeding not required",
					registerRequest.getUsername(), registerRequest.getEmail());
			return;
		}

		try {
			boUserService.registerUser(registerRequest);
		} catch (Exception e) {
			logger.error("Unable to seed user: {}", registerRequest.getEmail(), e);
		}
	}

	private void seedChatConfig() {

		Optional<ChatConfig> chatConfig = this.chatConfigRepository.findFirstByOrderByIdAsc();

		if (chatConfig.isPresent()) {
			logger.info("Chat config found. Seeding not required");
			return;
		}

		ChatConfig config = new ChatConfig();
		config.setAvailableFrom(0);
		config.setAvailableUntil(24);
		config.setTimeRestricted(true);

		try {
			this.chatConfigRepository.save(config);
		} catch (Exception e) {
			logger.error("Unable to insert chat log to db", e);
		}
	}

	private void seedRoles() {

		List<RoleEnum> roles = new ArrayList<>();
		roles.add(RoleEnum.USER);
		roles.add(RoleEnum.ADMIN);

		roles.stream().forEach(role -> {
			Optional<Role> savedRole = roleRepository.findById(role.getId());

			if (savedRole.isPresent()) {
				logger.info("Role {} already saved in database", role.getName());
				return;
			}

			logger.info("Inserting {} Role to db", role.getName());

			try {
				roleRepository.save(new Role(role.getId(), role.getName()));
			} catch (Exception e) {
				logger.error("Unable to insert {} role to db", role.getName(), e);
			}
		});
	}

}
