package com.samaritans.samaritanscoremodule.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.requests.ConversationRequest;
import com.samaritans.samaritanscoremodule.requests.Message;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.ChatAvailabilityResponse;
import com.samaritans.samaritanscoremodule.service.ChatService;
import com.samaritans.samaritanscoremodule.utils.MessageType;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	@Autowired
	private ChatService chatService;

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	/**
	 * Endpoint to handle chat message
	 * 
	 * @param Message
	 * @throws SamaritansException
	 * @return Message
	 */
	@MessageMapping("/send.message")
	public Message sendMessage(@Payload final Message message) {
		try {
			return chatService.sendMessage(message);
		} catch (final MessagingException e) {
			throw new SamaritansException("Unable to send message: " + e);
		}
	}

	/**
	 * Endpoint to handle disconnect event
	 * 
	 * @param Message
	 * @throws SamaritansException
	 */
	@MessageMapping("/send.disconnect")
	public void disconnect(@Payload final Message message) {
		logger.info("ChatController - disconnect - init");
		try {
			chatService.disconnect(message);
		} catch (final MessagingException e) {
			throw new SamaritansException(
					"An error occurred when handling disconnection for user: " + message.getSender(), e);
		}
	}

	/**
	 * Endpoint to add active user
	 * 
	 * @param message
	 * @return ResponseEntity
	 */
	@MessageMapping("/send.addActiveUser")
	public ResponseEntity<ApiResponse> addActiveUser(@Payload final Message message) {
		logger.info("ChatController - addActiveUser - init");

		if (!message.getType().equals(MessageType.JOIN)) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Unexpected message type: " + message.getType()));
		}

		chatService.addActiveUser(message);
		return ResponseEntity.ok(new ApiResponse(true, "Successfully Connected!"));
	}

	@PutMapping("/updateUnreadMessages")
	public void updateUnreadMessages(@Valid @RequestBody final String username) {
		logger.info("ChatController - updateUnreadMessages - init");
		chatService.updateUnreadMessages(username);
	}

	@GetMapping("/users/{username}")
	public ResponseEntity<String> getChatUsers(@PathVariable final String username) {
		logger.info("ChatController - getChatUsers - init");
		return ResponseEntity.ok(chatService.getChatUsers(username));
	}

	@GetMapping("/availability")
	public ResponseEntity<ChatAvailabilityResponse> isChatAvailable() {
		logger.info("ChatController - isChatAvailable - init");
		return ResponseEntity.ok(chatService.isChatAvailable());
	}

	@GetMapping("/isVolunteerActive/{username}")
	public ResponseEntity<ApiResponse> isVolunteerActive(@PathVariable final String username) {
		logger.info("ChatController - isVolunteerActive - init");

		if (StringUtils.isBlank(username)) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "No username provided. Unable to check if volunteer is active"));
		}

		return ResponseEntity.ok(chatService.isVolunteerActive(username));
	}

	@PostMapping("/startConversation")
	public ResponseEntity<ApiResponse> startConversation(
			@Valid @RequestBody final ConversationRequest conversationRequest) {
		logger.info("ChatController - startConversation - init");
		return ResponseEntity.ok(chatService.startConversation(conversationRequest));
	}

	/**
	 * Endpoint to load chat messages for user
	 * 
	 * @return List of Messages
	 */
	@GetMapping("/messages/{username}")
	public ResponseEntity<List<Message>> getMessages(@PathVariable final String username) {
		logger.info("ChatController - getMessages - init");
		final List<Message> messages = chatService.getMessagesByUsername(username);
		return ResponseEntity.ok(messages);
	}

}
