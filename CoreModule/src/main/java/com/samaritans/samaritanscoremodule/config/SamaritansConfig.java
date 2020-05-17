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
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ChatUserDao chatUserDao() {
		return new ChatUserDao();
	}

	@Bean
	public BoUserDao boUserDao() {
		return new BoUserDao();
	}

	@Bean
	public ChatLogDao chatLogDao() {
		return new ChatLogDao();
	}

	@Bean
	public RoleDao roleDao() {
		return new RoleDao();
	}

	@Bean
	public ChatDao chatDao() {
		return new ChatDao();
	}

	@Bean
	public ChatConfigDao chatConfigDao() {
		return new ChatConfigDao();
	}

	@Bean
	public NotificationDao notificationDao() {
		return new NotificationDao();
	}

	@Bean
	public TranscriptDao transcriptDao() {
		return new TranscriptDao();
	}
}
