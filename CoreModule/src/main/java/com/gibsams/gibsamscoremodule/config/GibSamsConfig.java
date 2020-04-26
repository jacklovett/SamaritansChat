package com.gibsams.gibsamscoremodule.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.gibsams.gibsamscoremodule.dao.BoUserDao;
import com.gibsams.gibsamscoremodule.dao.ChatConfigDao;
import com.gibsams.gibsamscoremodule.dao.ChatDao;
import com.gibsams.gibsamscoremodule.dao.ChatLogDao;
import com.gibsams.gibsamscoremodule.dao.ChatUserDao;
import com.gibsams.gibsamscoremodule.dao.NotificationDao;
import com.gibsams.gibsamscoremodule.dao.RoleDao;
import com.gibsams.gibsamscoremodule.dao.TranscriptDao;

/**
 * GibSams Configuration Class
 * 
 * @author jackl
 *
 */
@Configuration
public class GibSamsConfig {

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
