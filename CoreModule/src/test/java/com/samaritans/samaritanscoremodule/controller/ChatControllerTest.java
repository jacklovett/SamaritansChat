package com.samaritans.samaritanscoremodule.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.requests.ConversationRequest;
import com.samaritans.samaritanscoremodule.requests.Message;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.ChatAvailabilityResponse;
import com.samaritans.samaritanscoremodule.service.ChatService;
import com.samaritans.samaritanscoremodule.utils.AppConstants;
import com.samaritans.samaritanscoremodule.utils.ChatAvailabilityEnum;
import com.samaritans.samaritanscoremodule.utils.MessageType;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

	private static final String USERNAME = "jlove09876";
	private static final String MESSAGE_CONTENT = "I'm a little teapot!";

	private Message message;
	private Gson gson;
	private ConversationRequest conversationRequest;
	private List<Message> conversation;
	private Set<String> chatUsers;
	private MockMvc mockMvc;

	@Mock
	private ChatService chatService;

	@InjectMocks
	private ChatController chatController;

	@BeforeEach
	void setUp() {

		gson = new GsonBuilder().serializeNulls().create();

		message = new Message();
		message.setSender(USERNAME);

		conversation = new ArrayList<>();
		conversation.add(message);

		chatUsers = new HashSet<>();
		chatUsers.add(USERNAME);

		conversationRequest = new ConversationRequest();
		conversationRequest.setSamaritansUser(AppConstants.SAMARITANS_USERNAME);
		conversationRequest.setChatUser(USERNAME);

		this.mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();

	}

	@Test
	void testSendMessage() {

		message.setSender(AppConstants.SAMARITANS_USERNAME);
		message.setRecipient(USERNAME);
		message.setContent(MESSAGE_CONTENT);
		message.setType(MessageType.CHAT);

		when(chatService.sendMessage(message)).thenReturn(message);

		Message responseMessage = chatController.sendMessage(message);

		assertNotNull(responseMessage);
		assertEquals(responseMessage, message);
		verify(chatService, times(1)).sendMessage(message);
	}

	@Test
	void testSendMessageHandlesException() {

		doThrow(new MessagingException("Error")).when(chatService).sendMessage(message);

		chatController.sendMessage(message);

		assertThrows(SamaritansException.class, () -> chatController.sendMessage(message));
	}

	@Test
	void testDisconnectHandlesException() {

		doThrow(new MessagingException("Error")).when(chatService).disconnect(message);

		chatController.disconnect(message);

		assertThrows(SamaritansException.class, () -> chatController.disconnect(message));
	}

	@Test
	void testGetChatUsers() throws Exception {

		String chatUsersJson = gson.toJson(chatUsers);

		when(chatService.getChatUsers(AppConstants.SAMARITANS_USERNAME)).thenReturn(chatUsersJson);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chat/users/" + AppConstants.SAMARITANS_USERNAME)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(chatUsersJson, response.getContentAsString());

	}

	@Test
	void testGetChatUsersWhenEmpty() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chat/users/" + USERNAME)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("", response.getContentAsString());
	}

	@Test
	void testAddActiveUser() {

		message.setSender(USERNAME);
		message.setType(MessageType.JOIN);

		ResponseEntity<ApiResponse> response = chatController.addActiveUser(message);

		assertNotNull(response);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getBody().getSuccess());
	}

	@Test
	void testAddActiveUserWhenMessageIsNotAJoinMessage() {

		message.setSender(USERNAME);
		message.setType(MessageType.CHAT);

		ResponseEntity<ApiResponse> response = chatController.addActiveUser(message);

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
		assertFalse(response.getBody().getSuccess());
	}

	@Test
	void testUpdateUnreadMessages() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/chat/updateUnreadMessages")
				.accept(MediaType.APPLICATION_JSON).content(USERNAME).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}

	@Test
	void testIsChatAvailable() throws Exception {

		ChatAvailabilityResponse chatAvailabilityResponse = new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE);

		when(chatService.isChatAvailable()).thenReturn(chatAvailabilityResponse);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chat/availability")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(chatAvailabilityResponse), response.getContentAsString());
	}

	@Test
	void testGetMessages() throws Exception {

		when(chatService.getMessagesByUsername(USERNAME)).thenReturn(conversation);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chat/messages/" + USERNAME)
				.accept(MediaType.APPLICATION_JSON).content(USERNAME).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(conversation), response.getContentAsString());
	}

	@Test
	void testGetMessagesWhenNoMessages() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chat/messages/" + USERNAME)
				.accept(MediaType.APPLICATION_JSON).content(USERNAME).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[]", response.getContentAsString());
	}

	@Test
	void testStartConversation() throws Exception {

		ApiResponse expectedResponse = new ApiResponse(true, USERNAME + " added to contacts");

		when(chatService.startConversation(conversationRequest)).thenReturn(expectedResponse);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/chat/startConversation/")
				.accept(MediaType.APPLICATION_JSON).content(gson.toJson(conversationRequest))
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(gson.toJson(expectedResponse), response.getContentAsString());
	}

	@Test
	void testIsVolunteerActive() throws Exception {

		ApiResponse expectedResponse = new ApiResponse(true, "Volunteer is active");

		when(chatService.isVolunteerActive(USERNAME)).thenReturn(expectedResponse);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chat/isVolunteerActive/" + USERNAME)
				.accept(MediaType.APPLICATION_JSON).content(USERNAME).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(gson.toJson(expectedResponse), response.getContentAsString());
	}

	@Test
	void testIsVolunteerActiveBadRequest() throws Exception {

		ApiResponse expectedResponse = new ApiResponse(false,
				"No username provided. Unable to check if volunteer is active");

		String emptyUsername = " ";

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/chat/isVolunteerActive/" + emptyUsername)
				.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
		assertEquals(gson.toJson(expectedResponse), response.getContentAsString());
	}
}
