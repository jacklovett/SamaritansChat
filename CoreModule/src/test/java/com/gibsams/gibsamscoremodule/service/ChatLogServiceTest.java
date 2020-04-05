package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.gibsams.gibsamscoremodule.dao.ChatDao;
import com.gibsams.gibsamscoremodule.dao.ChatLogDao;
import com.gibsams.gibsamscoremodule.dao.TranscriptDao;
import com.gibsams.gibsamscoremodule.model.ChatLog;
import com.gibsams.gibsamscoremodule.model.Transcript;
import com.gibsams.gibsamscoremodule.requests.ChatLogRequest;
import com.gibsams.gibsamscoremodule.requests.Message;
import com.gibsams.gibsamscoremodule.responses.ChatLogResponse;
import com.gibsams.gibsamscoremodule.responses.TranscriptResponse;
import com.gibsams.gibsamscoremodule.utils.AppConstants;
import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class ChatLogServiceTest {

	private static final Long ID = 1l;
	private static final String USERNAME = "jlove0987";
	private static final String TRANSCRIPT_NOTES = "notes";
	private static final String MESSAGE_CONTENT = "Hello World!";

	private Gson gson;

	private Message message1;
	private Message message2;
	private List<Message> conversation;

	private ChatLog chatLog;
	private Transcript transcript;
	private TranscriptResponse transcriptResponse;

	private ChatLogRequest chatLogRequest;
	private ChatLogResponse chatLogResponse;
	private List<ChatLogResponse> chatLogResponses;

	@Mock
	private ChatDao chatDao;
	@Mock
	private TranscriptDao transcriptDao;
	@Mock
	private ChatLogDao chatLogDao;
	@InjectMocks
	private ChatLogService chatLogService;

	@Before
	public void setUp() {

		gson = new Gson();

		chatLogRequest = new ChatLogRequest();
		chatLogRequest.setUsername(USERNAME);
		chatLogRequest.setNotes(TRANSCRIPT_NOTES);
		chatLogRequest.setVolunteer(AppConstants.GIB_SAMS_USERNAME);

		chatLogResponses = new ArrayList<>();
		chatLogResponses.add(chatLogResponse);

		message1 = new Message();
		message1.setDateSent(Instant.now());
		message1.setRecipient(USERNAME);
		message1.setSender(AppConstants.GIB_SAMS_USERNAME);
		message1.setContent(MESSAGE_CONTENT);

		message2 = new Message();
		message2.setDateSent(Instant.now());
		message2.setRecipient(AppConstants.GIB_SAMS_USERNAME);
		message2.setSender(USERNAME);
		message2.setContent(MESSAGE_CONTENT);

		conversation = new ArrayList<>();
		conversation.add(message1);
		conversation.add(message2);

		chatLog = new ChatLog();
		chatLog.setId(ID);
		chatLog.setVolunteer(AppConstants.GIB_SAMS_USERNAME);
		chatLog.setUsername(USERNAME);

		transcript = new Transcript();
		transcript.setId(ID);
		transcript.setNotes(TRANSCRIPT_NOTES);
		transcript.setConversation(gson.toJson(conversation));
		transcript.setChatLog(chatLog);

		chatLog.setTranscript(transcript);

		transcriptResponse = new TranscriptResponse(transcript);

		chatLogResponse = new ChatLogResponse(chatLog);
	}

	@Test
	public void testSaveChatLog() {

		when(chatDao.findChatMessagesByUsername(USERNAME)).thenReturn(conversation);

		doAnswer((Answer<ChatLog>) c -> {
			chatLog.setId(ID);
			return chatLog;
		}).when(chatLogDao).save(Mockito.any(ChatLog.class));

		Long chatLogId = chatLogService.saveChatLog(chatLogRequest);

		assertEquals(ID, chatLogId);
	}

	@Test
	public void testSaveChatLogWhenNoMessages() {

		conversation.clear();

		when(chatDao.findChatMessagesByUsername(USERNAME)).thenReturn(conversation);

		doAnswer((Answer<ChatLog>) c -> {
			chatLog.setId(ID);
			return chatLog;
		}).when(chatLogDao).save(Mockito.any(ChatLog.class));

		Long chatLogId = chatLogService.saveChatLog(chatLogRequest);

		assertEquals(ID, chatLogId);
	}

	@Test
	public void testFindChatLogs() {

		when(chatLogDao.findAll()).thenReturn(chatLogResponses);

		List<ChatLogResponse> response = chatLogService.findChatLogs();

		assertNotNull(response);
		assertEquals(chatLogResponses, response);
	}

	@Test
	public void testFindTranscriptById() {

		when(transcriptDao.findById(ID)).thenReturn(transcriptResponse);

		TranscriptResponse response = chatLogService.findTranscriptById(ID);

		assertNotNull(response);
		assertEquals(transcriptResponse, response);
	}
}
