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
	 * Find config in database by id
	 * 
	 * @return ChatConfig
	 */
	public ChatConfig findConfigById(int id) {
		return chatConfigRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No chat config found with id: " + id));
	}

	/**
	 * @param config to save
	 */
	public ChatConfig save(ChatConfig config) {
		return chatConfigRepository.save(config);
	}
}
