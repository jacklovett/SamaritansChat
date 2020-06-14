package com.samaritans.samaritanscoremodule.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.ChatConfig;
import com.samaritans.samaritanscoremodule.repository.ChatConfigRepository;

/**
 * 
 * @author jackl
 *
 */
public class ChatConfigDao {

	@Autowired
	private ChatConfigRepository chatConfigRepository;

	/**
	 * Find the first chat config
	 * 
	 * @throws ResourceNotFoundException
	 * @return ChatConfig
	 */
	public ChatConfig findConfig() {
		return chatConfigRepository.findFirstByOrderByIdAsc()
				.orElseThrow(() -> new ResourceNotFoundException("No chat config found"));
	}

	/**
	 * @param config to save
	 */
	public ChatConfig save(final ChatConfig config) {
		return chatConfigRepository.save(config);
	}
}
