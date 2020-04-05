package com.gibsams.gibsamscoremodule.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.ChatLog;
import com.gibsams.gibsamscoremodule.repository.ChatLogRepository;
import com.gibsams.gibsamscoremodule.responses.ChatLogResponse;

public class ChatLogDao {

	@Autowired
	private ChatLogRepository chatLogRepository;

	public List<ChatLogResponse> findAll() {
		List<ChatLog> chatLogs = chatLogRepository.findAll();
		return chatLogs.stream().map(ChatLogResponse::new).collect(Collectors.toList());
	}

	public ChatLog findById(Long id) {
		return chatLogRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No chat log found"));
	}

	public ChatLog save(ChatLog chatLog) {
		return chatLogRepository.save(chatLog);
	}

	public boolean existsByUsername(String username) {
		return chatLogRepository.existsByUsername(username);
	}

}
