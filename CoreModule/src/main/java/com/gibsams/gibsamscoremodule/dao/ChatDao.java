package com.gibsams.gibsamscoremodule.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.model.ChatMessage;
import com.gibsams.gibsamscoremodule.repository.ChatRepository;
import com.gibsams.gibsamscoremodule.requests.Message;

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

	public int findNumberOfUnreadMessagesByUsername(String username) {
		return chatRepository.findNumberOfUnreadMessagesByUsername(username);
	}

	public int updateSeenMessagesByUsername(String username) {
		return chatRepository.updateSeenMessagesByUsername(username);
	}

}
