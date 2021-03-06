package com.samaritans.samaritanscoremodule.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samaritans.samaritanscoremodule.dao.ChatConfigDao;
import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.ChatConfig;
import com.samaritans.samaritanscoremodule.requests.ChatConfigRequest;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;

/**
 * ChatConfig REST controller
 * 
 * @author jackl
 *
 */
@RestController
@RequestMapping("/api/config")
public class ChatConfigController {

	@Autowired
	private ChatConfigDao chatConfigDao;

	private static final Logger logger = LoggerFactory.getLogger(ChatConfigController.class);

	/**
	 * Get chat config by id endpoint
	 * 
	 * @param id
	 * @return chat config
	 */
	@GetMapping()
	public ResponseEntity<ChatConfigRequest> getChatConfig() {
		logger.info("ChatConfigController - getChatConfig - init");
		final ChatConfig config = chatConfigDao.findConfig();
		final ChatConfigRequest chatConfigRequest = new ChatConfigRequest(config);
		return ResponseEntity.ok(chatConfigRequest);
	}

	/**
	 * ChatConfig update endpoint
	 * 
	 * @param ChatConfig
	 * @return ApiResponse
	 */
	@PutMapping("/edit")
	public ResponseEntity<ApiResponse> updateConfig(@Valid @RequestBody final ChatConfigRequest configRequest) {
		logger.info("ChatConfigController - updateConfig - init");
		try {
			final ChatConfig config = chatConfigDao.findConfig();
			config.setTimeRestricted(configRequest.isTimeRestricted());
			config.setAvailableFrom(configRequest.getAvailableFrom());
			config.setAvailableUntil(configRequest.getAvailableUntil());
			chatConfigDao.save(config);
		} catch (ResourceNotFoundException e) {
			return new ResponseEntity<>(new ApiResponse(false, "Unable to update chat settings"), HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(new ApiResponse(true, "Settings updated successfully"));
	}

}
