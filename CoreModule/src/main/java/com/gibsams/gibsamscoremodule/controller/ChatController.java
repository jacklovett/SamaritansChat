package com.gibsams.gibsamscoremodule.controller;

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

import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.requests.ConversationRequest;
import com.gibsams.gibsamscoremodule.requests.Message;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.responses.ChatAvailabilityResponse;
import com.gibsams.gibsamscoremodule.service.ChatService;
import com.gibsams.gibsamscoremodule.utils.MessageType;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

	@Autowired
	private ChatService chatService;

	private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

	/**
	 * Endpoint to handle chat message
	 * 
	 * @param message
	 * @return message back to sender
	 */
	@MessageMapping("/send/message")
	public Message sendMessage(@Payload Message message) {
		try {
			return chatService.sendMessage(message);
		} catch (MessagingException e) {
			throw new GibSamsException("Unable to send message: " + e);
		}
	}

	/**
	 * Endpoint to handle disconnect event
	 * 
	 * @param message
	 */
	@MessageMapping("/send/disconnect")
	public void disconnect(@Payload Message message) {
		logger.info("ChatController - disconnect - init");
		try {
			chatService.disconnect(message);
		} catch (MessagingException e) {
			throw new GibSamsException("An error occurred when handling disconnection for user: " + message.getSender(),
					e);
		}
	}

	/**
	 * Endpoint to add active user
	 * 
	 * @param message
	 * @return ResponseEntity
	 */
	@MessageMapping("/send/addActiveUser")
	public ResponseEntity<ApiResponse> addActiveUser(@Payload Message message) {
		logger.info("ChatController - addActiveUser - init");
		if (message.getType().equals(MessageType.JOIN)) {
			chatService.addActiveUser(message);
		} else {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "Unexpected message type recieved from message: " + message));
		}
		return ResponseEntity.ok(new ApiResponse(true, "Successfully Connected!"));
	}

	@PutMapping("/updateUnreadMessages")
	public void updateUnreadMessages(@Valid @RequestBody String username) {
		logger.info("ChatController - updateUnreadMessages - init");
		chatService.updateUnreadMessages(username);
	}

	@GetMapping("/users/{username}")
	public ResponseEntity<String> getChatUsers(@PathVariable String username) {
		logger.info("ChatController - getChatUsers - init");
		return ResponseEntity.ok(chatService.getChatUsers(username));
	}

	@GetMapping("/availability")
	public ResponseEntity<ChatAvailabilityResponse> isChatAvailable() {
		logger.info("ChatController - isChatAvailable - init");
		return ResponseEntity.ok(chatService.isChatAvailable());
	}

	@GetMapping("/isVolunteerActive/{username}")
	public ResponseEntity<ApiResponse> isVolunteerActive(@PathVariable String username) {
		logger.info("ChatController - isVolunteerActive - init");

		if (StringUtils.isBlank(username)) {
			return ResponseEntity.badRequest()
					.body(new ApiResponse(false, "No username provided. Unable to check if volunteer is active"));
		}

		return ResponseEntity.ok(chatService.isVolunteerActive(username));
	}

	@PostMapping("/startConversation")
	public ResponseEntity<ApiResponse> startConversation(@Valid @RequestBody ConversationRequest conversationRequest) {
		logger.info("ChatController - startConversation - init");
		return ResponseEntity.ok(chatService.startConversation(conversationRequest));
	}

	/**
	 * Endpoint to load conversation for user
	 * 
	 * @return List of Messages
	 */
	@GetMapping("/conversation/{username}")
	public ResponseEntity<List<Message>> getConversationByUsername(@PathVariable String username) {

		logger.info("ChatController - getConversationByUsername - init");

		List<Message> messages = chatService.getMessagesByUsername(username);

		if (messages.isEmpty()) {
			logger.warn("No messages found in database");
		}

		return ResponseEntity.ok(messages);
	}

}
