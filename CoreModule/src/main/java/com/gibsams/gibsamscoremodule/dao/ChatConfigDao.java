package com.gibsams.gibsamscoremodule.dao;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.ChatConfig;
import com.gibsams.gibsamscoremodule.repository.ChatConfigRepository;

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
