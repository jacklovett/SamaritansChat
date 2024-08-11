package com.samaritans.samaritanscoremodule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.samaritans.samaritanscoremodule.dao.BoUserDao;
import com.samaritans.samaritanscoremodule.dao.ChatConfigDao;
import com.samaritans.samaritanscoremodule.dao.ChatDao;
import com.samaritans.samaritanscoremodule.dao.ChatLogDao;
import com.samaritans.samaritanscoremodule.dao.ChatUserDao;
import com.samaritans.samaritanscoremodule.dao.NotificationDao;
import com.samaritans.samaritanscoremodule.dao.RoleDao;
import com.samaritans.samaritanscoremodule.dao.TranscriptDao;

/**
 * Samaritans Configuration Class
 * 
 * @author jackl
 *
 */
@Configuration
public class SamaritansConfig {

	@Bean
	RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	ChatUserDao chatUserDao() {
		return new ChatUserDao();
	}

	@Bean
	BoUserDao boUserDao() {
		return new BoUserDao();
	}

	@Bean
	ChatLogDao chatLogDao() {
		return new ChatLogDao();
	}

	@Bean
	RoleDao roleDao() {
		return new RoleDao();
	}

	@Bean
	ChatDao chatDao() {
		return new ChatDao();
	}

	@Bean
	ChatConfigDao chatConfigDao() {
		return new ChatConfigDao();
	}

	@Bean
	NotificationDao notificationDao() {
		return new NotificationDao();
	}

	@Bean
	TranscriptDao transcriptDao() {
		return new TranscriptDao();
	}
}
