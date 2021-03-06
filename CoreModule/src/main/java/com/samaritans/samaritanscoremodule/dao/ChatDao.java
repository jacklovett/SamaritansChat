package com.samaritans.samaritanscoremodule.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.samaritans.samaritanscoremodule.model.ChatMessage;
import com.samaritans.samaritanscoremodule.repository.ChatRepository;
import com.samaritans.samaritanscoremodule.requests.Message;

public class ChatDao {

	@Autowired
	private ChatRepository chatRepository;

	public ChatMessage save(Message message) {
		ChatMessage chatMessage = new ChatMessage(message);
		return chatRepository.save(chatMessage);
	}

	public List<Message> findChatMessagesByUsername(String username) {
		return chatRepository.findAllBySenderOrRecipient(username, username).stream().map(Message::new)
				.collect(Collectors.toList());
	}

	public int updateSeenMessagesByUsername(String username) {
		return chatRepository.updateSeenMessagesByUsername(username);
	}

}
