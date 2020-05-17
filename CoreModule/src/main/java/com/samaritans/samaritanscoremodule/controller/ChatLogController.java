package com.samaritans.samaritanscoremodule.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.samaritans.samaritanscoremodule.requests.ChatLogRequest;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.ChatLogResponse;
import com.samaritans.samaritanscoremodule.responses.TranscriptResponse;
import com.samaritans.samaritanscoremodule.service.ChatLogService;

/**
 * ChatLog REST controller
 * 
 * @author jackl
 */
@RestController
@RequestMapping("/api/chatlogs")
public class ChatLogController {

	@Autowired
	private ChatLogService chatLogService;

	private static final Logger logger = LoggerFactory.getLogger(ChatLogController.class);

	/**
	 * Endpoint to get chat logs for BackOffice application
	 * 
	 * @return List of Chat Logs
	 */
	@GetMapping()
	public ResponseEntity<List<ChatLogResponse>> getChatLogs() {
		logger.info("ChatLogController - getChatLogs - init");

		List<ChatLogResponse> chatLogs = chatLogService.findChatLogs();

		if (chatLogs.isEmpty()) {
			logger.info("No chat logs found in database");
		}

		return ResponseEntity.ok(chatLogs);
	}

	/**
	 * Get transcript by id
	 * 
	 * @param id
	 * @return TranscriptResponse
	 */
	@GetMapping("/transcript/{id}")
	public ResponseEntity<TranscriptResponse> getTranscriptById(@PathVariable Long id) {
		logger.info("ChatLogController - getTranscriptById - init");

		TranscriptResponse transcript = chatLogService.findTranscriptById(id);

		return ResponseEntity.of(Optional.ofNullable(transcript));
	}

	@PostMapping("/save")
	public ResponseEntity<ApiResponse> saveChatLog(@Valid @RequestBody ChatLogRequest chatLogRequest) {
		logger.info("ChatLogController - saveChatLog - init");
		if (chatLogService.chatLogExistsByUsername(chatLogRequest.getUsername())) {
			return new ResponseEntity<>(new ApiResponse(false, "Chat log has already been saved"),
					HttpStatus.BAD_REQUEST);
		}

		Long chatLogId = chatLogService.saveChatLog(chatLogRequest);

		if (chatLogId > 0) {
			URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/chatlogs/{id}")
					.buildAndExpand(chatLogId).toUri();

			return ResponseEntity.created(location).body(new ApiResponse(true, "Chat log saved successfully"));
		} else {
			return ResponseEntity.badRequest().body(new ApiResponse(false, "Unable to save chat log"));
		}

	}
}
