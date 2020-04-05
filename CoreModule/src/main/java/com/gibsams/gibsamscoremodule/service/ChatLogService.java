package com.gibsams.gibsamscoremodule.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.ChatDao;
import com.gibsams.gibsamscoremodule.dao.ChatLogDao;
import com.gibsams.gibsamscoremodule.dao.TranscriptDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.model.ChatLog;
import com.gibsams.gibsamscoremodule.model.Transcript;
import com.gibsams.gibsamscoremodule.requests.ChatLogRequest;
import com.gibsams.gibsamscoremodule.requests.Message;
import com.gibsams.gibsamscoremodule.responses.ChatLogResponse;
import com.gibsams.gibsamscoremodule.responses.TranscriptMessage;
import com.gibsams.gibsamscoremodule.responses.TranscriptResponse;
import com.google.gson.Gson;

@Service
public class ChatLogService {

	private Gson gson = new Gson();

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private TranscriptDao transcriptDao;

	@Autowired
	private ChatLogDao chatLogDao;

	public Long saveChatLog(ChatLogRequest chatLogRequest) {

		Instant startTime = null;
		Instant endTime = null;

		String chatUser = chatLogRequest.getUsername();

		List<Message> conversation = chatDao.findChatMessagesByUsername(chatUser);

		Transcript transcript = new Transcript();
		transcript.setNotes(chatLogRequest.getNotes());

		if (!conversation.isEmpty()) {
			startTime = conversation.get(0).getDateSent();
			endTime = conversation.get((conversation.size() - 1)).getDateSent();
			transcript.setConversation(formattedConversation(conversation));
		}

		ChatLog chatLog = new ChatLog();
		chatLog.setVolunteer(chatLogRequest.getVolunteer());
		chatLog.setUsername(chatLogRequest.getUsername());
		chatLog.setStartTime(startTime);
		chatLog.setEndTime(endTime);
		chatLog.setRating(chatLogRequest.getRating());

		try {
			transcript = transcriptDao.save(transcript);
			chatLog.setTranscript(transcript);
			chatLog = chatLogDao.save(chatLog);
		} catch (Exception e) {
			throw new GibSamsException("Unable to save chat log: ", e);
		}

		return chatLog.getId();
	}

	public boolean chatLogExistsByUsername(String username) {
		return chatLogDao.existsByUsername(username);
	}

	public List<ChatLogResponse> findChatLogs() {
		return chatLogDao.findAll();
	}

	public TranscriptResponse findTranscriptById(Long id) {
		return transcriptDao.findById(id);
	}

	/**
	 * Format Messages to TranscriptMessages This converts Instant to correct string
	 * format and removes unnecessary MessageType property
	 * 
	 * @param messages
	 * @return json conversation
	 */
	private String formattedConversation(List<Message> messages) {

		List<TranscriptMessage> transcriptMessages = messages.stream().map(TranscriptMessage::new)
				.collect(Collectors.toList());

		return gson.toJson(transcriptMessages);
	}

}
