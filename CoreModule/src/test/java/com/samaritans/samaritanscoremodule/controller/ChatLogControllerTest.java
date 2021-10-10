package com.samaritans.samaritanscoremodule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.samaritans.samaritanscoremodule.model.ChatLog;
import com.samaritans.samaritanscoremodule.model.Transcript;
import com.samaritans.samaritanscoremodule.requests.ChatLogRequest;
import com.samaritans.samaritanscoremodule.requests.Message;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.ChatLogResponse;
import com.samaritans.samaritanscoremodule.responses.TranscriptResponse;
import com.samaritans.samaritanscoremodule.service.ChatLogService;
import com.samaritans.samaritanscoremodule.utils.AppConstants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@ExtendWith(MockitoExtension.class)
class ChatLogControllerTest {

	private static final Long ID = 1l;
	private static final String TRANSCRIPT_NOTES = "notes";
	private static final String USERNAME = "jlove0987";
	private static final String MESSAGE_CONTENT = "Hello World!";
	private static final String CHAT_LOG_URI = "http://localhost/api/chatlogs/1";

	private Gson gson;
	private MockMvc mockMvc;
	private ChatLog chatLog;
	private Transcript transcript;
	private TranscriptResponse transcriptResponse;
	private ChatLogRequest chatLogRequest;
	private ChatLogResponse chatLogResponse;
	private List<ChatLogResponse> chatLogResponses;

	private Message message1;
	private Message message2;
	private List<Message> conversation;

	@Mock
	private ChatLogService chatLogService;
	@InjectMocks
	private ChatLogController chatLogController;

	@BeforeEach
	void setUp() {

		gson = new GsonBuilder().serializeNulls().create();

		chatLog = new ChatLog();
		chatLog.setId(ID);
		chatLog.setUsername(USERNAME);
		chatLog.setVolunteer(AppConstants.SAMARITANS_USERNAME);

		chatLogRequest = new ChatLogRequest();
		chatLogRequest.setUsername(USERNAME);
		chatLogRequest.setVolunteer(AppConstants.SAMARITANS_USERNAME);

		message1 = new Message();
		message1.setDateSent(Instant.now());
		message1.setRecipient(USERNAME);
		message1.setSender(AppConstants.SAMARITANS_USERNAME);
		message1.setContent(MESSAGE_CONTENT);

		message2 = new Message();
		message2.setDateSent(Instant.now());
		message2.setRecipient(AppConstants.SAMARITANS_USERNAME);
		message2.setSender(USERNAME);
		message2.setContent(MESSAGE_CONTENT);

		conversation = new ArrayList<>();
		conversation.add(message1);
		conversation.add(message2);

		transcript = new Transcript();
		transcript.setId(ID);
		transcript.setNotes(TRANSCRIPT_NOTES);
		transcript.setConversation(gson.toJson(conversation));
		transcript.setChatLog(chatLog);

		chatLog.setTranscript(transcript);

		chatLogResponse = new ChatLogResponse(chatLog);

		chatLogResponses = new ArrayList<>();
		chatLogResponses.add(chatLogResponse);

		transcriptResponse = new TranscriptResponse(transcript);

		this.mockMvc = MockMvcBuilders.standaloneSetup(chatLogController).build();
	}

	@Test
	void testGetChatLogs() throws Exception {

		when(chatLogService.findChatLogs()).thenReturn(chatLogResponses);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chatlogs/")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(chatLogResponses), response.getContentAsString());
	}

	@Test
	void testGetGetChatLogsWhenEmpty() throws Exception {

		chatLogResponses.clear();

		when(chatLogService.findChatLogs()).thenReturn(chatLogResponses);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chatlogs/")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[]", response.getContentAsString());

	}

	@Test
	void testGetTranscriptById() throws Exception {

		when(chatLogService.findTranscriptById(ID)).thenReturn(transcriptResponse);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chatlogs/transcript/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(transcriptResponse), response.getContentAsString());
	}

	@Test
	void testGetTranscriptByIdWhenTranscriptNotFound() throws Exception {

		when(chatLogService.findTranscriptById(ID)).thenReturn(null);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chatlogs/transcript/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
		assertEquals("", response.getContentAsString());
	}

	@Test
	void testSave() throws Exception {

		String json = gson.toJson(chatLogRequest);

		when(chatLogService.saveChatLog(chatLogRequest)).thenReturn(ID);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/chatlogs/save").accept(MediaType.APPLICATION_JSON)
				.content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.CREATED.value(), response.getStatus());
		assertEquals(CHAT_LOG_URI, response.getHeader("Location"));
		assertEquals(gson.toJson(new ApiResponse(true, "Chat log saved successfully")), response.getContentAsString());
	}

	@Test
	void testSaveBadRequest() throws Exception {

		String json = gson.toJson(chatLogRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/chatlogs/save").accept(MediaType.APPLICATION_JSON)
				.content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Unable to save chat log")), response.getContentAsString());
	}

	@Test
	void testSaveWhenChatLogAlreadyExists() throws Exception {

		String json = gson.toJson(chatLogRequest);

		when(chatLogService.chatLogExistsByUsername(USERNAME)).thenReturn(true);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/chatlogs/save").accept(MediaType.APPLICATION_JSON)
				.content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Chat log has already been saved")), response.getContentAsString());

	}
}
