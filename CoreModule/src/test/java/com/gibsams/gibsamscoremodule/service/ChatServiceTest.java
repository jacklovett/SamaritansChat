package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.gibsams.gibsamscoremodule.dao.ChatConfigDao;
import com.gibsams.gibsamscoremodule.dao.ChatDao;
import com.gibsams.gibsamscoremodule.dao.ChatUserDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.model.BoUser;
import com.gibsams.gibsamscoremodule.model.ChatConfig;
import com.gibsams.gibsamscoremodule.model.ChatMessage;
import com.gibsams.gibsamscoremodule.model.ChatUser;
import com.gibsams.gibsamscoremodule.requests.ConversationRequest;
import com.gibsams.gibsamscoremodule.requests.Message;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.responses.ChatAvailabilityResponse;
import com.gibsams.gibsamscoremodule.responses.ChatUserResponse;
import com.gibsams.gibsamscoremodule.utils.AppConstants;
import com.gibsams.gibsamscoremodule.utils.ChatAvailabilityEnum;
import com.gibsams.gibsamscoremodule.utils.MessageType;
import com.gibsams.gibsamscoremodule.utils.NotificationTypeEnum;
import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class ChatServiceTest {

	private static final Long ID = 1L;
	private static final String CHAT = "/chat/";
	private static final int AVAILABLE_FROM = 0;
	private static final int AVAILABLE_UNTIL = 24;
	private static final String USERNAME = "jlove09876";
	private static final String USERNAME_2 = "lovej1234";
	private static final String GIB_SAMS_USER = "gibSamsUser";
	private static final String MESSAGE_CONTENT = "I'm a little teapot!";

	private Gson gson;
	private ChatUser chatUser;
	private BoUser boUser;
	private ChatConfig config;
	private Message message;
	private ChatMessage chatMessage;
	private Set<String> activeUsers;
	private Set<String> activeGibSamsUsers;
	private List<Message> chatMessages;
	private Map<String, String> conversations;
	private ConversationRequest conversationRequest;

	@Mock
	private SimpMessagingTemplate simpMessagingTemplate;
	@Mock
	private ChatUserDao chatUserDao;
	@Mock
	private ChatDao chatDao;
	@Mock
	private ChatConfigDao chatConfigDao;
	@Mock
	private UserService userService;
	@Mock
	private ChatUserService chatUserService;
	@Mock
	private NotificationService notificationService;
	@InjectMocks
	private ChatService chatService;

	@Before
	public void setUp() {

		gson = new Gson();

		chatUser = new ChatUser();
		chatUser.setId(ID);
		chatUser.setUsername(GIB_SAMS_USER);

		boUser = new BoUser();
		boUser.setId(ID);
		boUser.setUsername(GIB_SAMS_USER);

		config = new ChatConfig();
		config.setTimeRestricted(true);
		config.setAvailableFrom(AVAILABLE_FROM);
		config.setAvailableUntil(AVAILABLE_UNTIL);

		conversations = new HashMap<>();
		conversations.put(USERNAME, GIB_SAMS_USER);

		message = new Message();
		message.setSender(USERNAME);
		message.setContent(MESSAGE_CONTENT);
		message.setType(MessageType.CHAT);

		chatMessage = new ChatMessage(message);
		chatMessage.setDateCreated(Instant.now());

		chatMessages = new ArrayList<>();
		chatMessages.add(message);

		activeUsers = new HashSet<>();
		activeGibSamsUsers = new HashSet<>();

		conversationRequest = new ConversationRequest();
		conversationRequest.setGibSamsUser(GIB_SAMS_USER);
		conversationRequest.setChatUser(USERNAME);

	}

	@Test
	public void testSendMessageAsChatUser() {

		message.setRecipient(AppConstants.GIB_SAMS_USERNAME);

		when(chatDao.save(message)).thenReturn(chatMessage);

		chatService.setConversations(conversations);

		Message responseMessage = chatService.sendMessage(message);

		assertNotNull(responseMessage);
		assertEquals(USERNAME, responseMessage.getSender());
		assertEquals(GIB_SAMS_USER, responseMessage.getRecipient());
		assertEquals(chatMessage.getDateCreated(), responseMessage.getDateSent());
		verify(chatDao, times(1)).save(responseMessage);
		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + GIB_SAMS_USER, responseMessage);
	}

	@Test
	public void testSendMessageAsGibSamsUser() {

		chatService.setConversations(conversations);

		message.setSender(GIB_SAMS_USER);
		message.setRecipient(USERNAME);

		when(chatDao.save(message)).thenReturn(chatMessage);

		Message responseMessage = chatService.sendMessage(message);

		assertNotNull(responseMessage);
		assertEquals(USERNAME, responseMessage.getRecipient());
		assertEquals(AppConstants.GIB_SAMS_USERNAME, responseMessage.getSender());
		assertEquals(chatMessage.getDateCreated(), responseMessage.getDateSent());
		verify(chatDao, times(1)).save(message);
		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + USERNAME, responseMessage);
	}

	@Test(expected = GibSamsException.class)
	public void testSendMessageWhenNoConversationFound() {

		chatService.setConversations(new HashMap<>());
		chatService.sendMessage(message);

		verify(chatDao, never()).save(Mockito.any(Message.class));
		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));
	}

	@Test
	public void testDisconnectGibSamsUser() {

		activeGibSamsUsers.add(GIB_SAMS_USER);
		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		assertEquals(1, chatService.getActiveGibSamsUsers().size());

		conversations.put(USERNAME_2, GIB_SAMS_USER);
		chatService.setConversations(conversations);

		message.setSender(GIB_SAMS_USER);
		message.setType(MessageType.LEAVE);

		when(userService.getUserByUsername(GIB_SAMS_USER)).thenReturn(boUser);

		chatService.disconnect(message);

		message.setRecipient(USERNAME);
		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + USERNAME, message);

		message.setRecipient(USERNAME_2);
		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + USERNAME, message);

		assertTrue(chatService.getActiveGibSamsUsers().isEmpty());
		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.NO_VOLUNTEERS));

	}

	@Test
	public void testDisconnectChatUser() {

		chatUser.setUsername(USERNAME);

		when(userService.getUserByUsername(USERNAME)).thenReturn(chatUser);

		message.setSender(USERNAME);
		message.setRecipient(AppConstants.GIB_SAMS_USERNAME);
		message.setType(MessageType.LEAVE);

		activeUsers.add(USERNAME);
		chatService.setActiveUsers(activeUsers);

		assertEquals(1, chatService.getActiveUsers().size());

		chatService.setConversations(conversations);

		chatService.disconnect(message);

		assertTrue(chatService.getActiveUsers().isEmpty());
		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.USER_DISCONNECTED, GIB_SAMS_USER,
				USERNAME);
		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + GIB_SAMS_USER, message);
		verify(chatUserService, times(1)).disableChatUser(USERNAME);

	}

	@Test
	public void testDisconnectWhenConversationNotFoundAndDeleteUser() {

		chatUser.setUsername(USERNAME);

		when(userService.getUserByUsername(USERNAME)).thenReturn(chatUser);

		message.setSender(USERNAME);
		message.setType(MessageType.LEAVE);

		activeUsers.add(USERNAME);
		chatService.setActiveUsers(activeUsers);

		assertEquals(1, chatService.getActiveUsers().size());

		chatService.setConversations(new HashMap<>());

		chatService.disconnect(message);

		assertTrue(chatService.getActiveUsers().isEmpty());
		verify(chatUserDao, times(1)).deleteUserById(ID);
		verify(notificationService, never()).addNotification(NotificationTypeEnum.USER_DISCONNECTED, GIB_SAMS_USER,
				USERNAME);
		verify(simpMessagingTemplate, never()).convertAndSend(CHAT + GIB_SAMS_USER, message);

	}

	@Test
	public void testDisconnectWhenConversationNotFoundWhenMessagesExist() {

		chatUser.setUsername(USERNAME);

		when(userService.getUserByUsername(USERNAME)).thenReturn(chatUser);

		message.setRecipient(GIB_SAMS_USER);
		chatMessages.add(message);

		when(chatDao.findChatMessagesByUsername(USERNAME)).thenReturn(chatMessages);

		message.setSender(USERNAME);
		message.setType(MessageType.LEAVE);

		activeUsers.add(USERNAME);
		chatService.setActiveUsers(activeUsers);

		assertEquals(1, chatService.getActiveUsers().size());

		chatService.setConversations(new HashMap<>());

		chatService.disconnect(message);

		assertTrue(chatService.getActiveUsers().isEmpty());
		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.USER_DISCONNECTED, GIB_SAMS_USER,
				USERNAME);
		verify(simpMessagingTemplate, never()).convertAndSend(CHAT + GIB_SAMS_USER, message);
		verify(chatUserService, times(1)).disableChatUser(USERNAME);

	}

	@Test(expected = GibSamsException.class)
	public void testDisconnectIncorrectMessageType() {

		message.setSender(USERNAME);
		message.setRecipient(AppConstants.GIB_SAMS_USERNAME);
		message.setType(MessageType.CHAT);

		chatService.disconnect(message);

	}

	@Test
	public void testAddActiveUserWhenGibSamsUserAndChatIsAvailable() {

		when(userService.getUserByUsername(GIB_SAMS_USER)).thenReturn(boUser);
		when(chatConfigDao.findConfig()).thenReturn(config);

		message.setSender(GIB_SAMS_USER);
		message.setType(MessageType.JOIN);

		chatService.setConversations(conversations);

		chatService.addActiveUser(message);

		assertEquals(1, chatService.getActiveGibSamsUsers().size());
		assertTrue(chatService.getActiveGibSamsUsers().contains(GIB_SAMS_USER));

		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));

		message.setSender(AppConstants.GIB_SAMS_USERNAME);
		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT + USERNAME, message);
	}

	@Test
	public void testAddActiveUserWhenGibSamsUserAndChatIsUnAvailable() {

		when(userService.getUserByUsername(GIB_SAMS_USER)).thenReturn(boUser);

		config.setAvailableFrom(24);
		config.setAvailableUntil(24);

		when(chatConfigDao.findConfig()).thenReturn(config);

		message.setSender(GIB_SAMS_USER);
		message.setType(MessageType.JOIN);

		chatService.addActiveUser(message);

		assertEquals(0, chatService.getActiveGibSamsUsers().size());

		verify(simpMessagingTemplate, never()).convertAndSend(CHAT + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));
	}

	@Test
	public void testAddActiveUserWhenGibSamsUserAlreadyConnected() {

		activeGibSamsUsers.add(GIB_SAMS_USER);

		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		message.setSender(GIB_SAMS_USER);
		message.setType(MessageType.JOIN);

		chatService.addActiveUser(message);

		assertEquals(1, chatService.getActiveGibSamsUsers().size());
		assertTrue(chatService.getActiveGibSamsUsers().contains(GIB_SAMS_USER));

		verify(userService, never()).getUserByUsername(Mockito.anyString());
		verify(simpMessagingTemplate, never()).convertAndSend(CHAT + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));
		verify(simpMessagingTemplate, never()).convertAndSend(CHAT, message);
	}

	@Test
	public void testAddActiveUserWhenChatUser() {

		chatUser.setUsername(USERNAME);

		when(userService.getUserByUsername(USERNAME)).thenReturn(chatUser);

		message.setSender(USERNAME);
		message.setType(MessageType.JOIN);

		activeGibSamsUsers.add(GIB_SAMS_USER);
		activeGibSamsUsers.add(AppConstants.GIB_SAMS_USERNAME);

		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		chatService.addActiveUser(message);

		assertEquals(1, chatService.getActiveUsers().size());
		assertTrue(chatService.getActiveUsers().contains(USERNAME));

		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.NEW_USER_CONNECTED, GIB_SAMS_USER,
				USERNAME);
		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.NEW_USER_CONNECTED,
				AppConstants.GIB_SAMS_USERNAME, USERNAME);
	}

	@Test
	public void testAddActiveUserWhenChatUserAlreadyConnected() {

		activeUsers.add(USERNAME);

		chatService.setActiveUsers(activeUsers);

		message.setSender(USERNAME);
		message.setType(MessageType.JOIN);

		chatService.addActiveUser(message);

		assertEquals(1, chatService.getActiveUsers().size());
		assertTrue(chatService.getActiveUsers().contains(USERNAME));

		verify(chatUserDao, never()).findUserByUsernameOrEmail(USERNAME);
		verify(notificationService, never()).addNotification(Mockito.any(), Mockito.any(), Mockito.any());

	}

	@Test
	public void testGetChatUsers() {

		conversations.put(USERNAME_2, GIB_SAMS_USER);
		chatService.setConversations(conversations);

		ChatUserResponse expectedChatUser1 = new ChatUserResponse(USERNAME, 1);
		ChatUserResponse expectedChatUser2 = new ChatUserResponse(USERNAME_2, 1);

		Set<ChatUserResponse> expectedUsers = new HashSet<>();
		expectedUsers.add(expectedChatUser1);
		expectedUsers.add(expectedChatUser2);

		when(chatDao.findNumberOfUnreadMessagesByUsername(Mockito.anyString())).thenReturn(1);

		String response = chatService.getChatUsers(GIB_SAMS_USER);

		assertNotNull(response);
		assertEquals(gson.toJson(expectedUsers), response);
		verify(chatDao, times(2)).findNumberOfUnreadMessagesByUsername(Mockito.anyString());

	}

	@Test
	public void testGetChatUsersWhenEmpty() {

		conversations = new HashMap<>();
		chatService.setConversations(conversations);

		String response = chatService.getChatUsers(AppConstants.GIB_SAMS_USERNAME);

		assertNotNull(response);
		assertEquals("[]", response);
		verify(chatDao, never()).findNumberOfUnreadMessagesByUsername(Mockito.anyString());
	}

	@Test
	public void testIsChatAvailableFailsWhenNoVolunteers() {

		when(chatConfigDao.findConfig()).thenReturn(config);

		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		ChatAvailabilityResponse response = chatService.isChatAvailable();

		assertNotNull(response);
		assertEquals(ChatAvailabilityEnum.NO_VOLUNTEERS.getType(), response.getType());
		assertEquals(ChatAvailabilityEnum.NO_VOLUNTEERS.getMessage(), response.getMessage());

	}

	@Test
	public void testIsChatAvailableSuccess() {

		when(chatConfigDao.findConfig()).thenReturn(config);

		activeGibSamsUsers.add(GIB_SAMS_USER);
		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		ChatAvailabilityResponse response = chatService.isChatAvailable();

		assertNotNull(response);
		assertEquals(ChatAvailabilityEnum.AVAILABLE.getType(), response.getType());
		assertEquals(ChatAvailabilityEnum.AVAILABLE.getMessage(), response.getMessage());
	}

	@Test
	public void testIsChatAvailableWhenTimeUnavailable() {

		config.setAvailableFrom(0);
		config.setAvailableUntil(0);

		when(chatConfigDao.findConfig()).thenReturn(config);

		ChatAvailabilityResponse response = chatService.isChatAvailable();

		String expectedMessage = MessageFormat.format(ChatAvailabilityEnum.UNAVAILABLE_TIME.getMessage(), "00:00",
				"00:00");

		assertNotNull(response);
		assertEquals(ChatAvailabilityEnum.UNAVAILABLE_TIME.getType(), response.getType());
		assertEquals(expectedMessage, response.getMessage());
	}

	@Test
	public void testGetMessagesByUsername() {

		when(chatDao.findChatMessagesByUsername(USERNAME)).thenReturn(chatMessages);

		List<Message> response = chatService.getMessagesByUsername(USERNAME);

		assertNotNull(response);
		assertFalse(response.isEmpty());
		assertEquals(MessageType.CHAT, response.get(0).getType());
		assertEquals(message.getSender(), response.get(0).getSender());
		assertEquals(message.getContent(), response.get(0).getContent());
	}

	@Test
	public void testGetMessagesByUsernameWhenNoMessages() {

		List<Message> response = chatService.getMessagesByUsername(USERNAME);

		assertNotNull(response);
		assertTrue(response.isEmpty());

	}

	@Test
	public void testUpdateUnreadMessages() {

		when(chatDao.updateSeenMessagesByUsername(USERNAME)).thenReturn(1);

		chatService.updateUnreadMessages(USERNAME);
		verify(chatDao, times(1)).updateSeenMessagesByUsername(USERNAME);

	}

	@Test
	public void testStartConversation() {

		message.setType(MessageType.JOIN);

		activeUsers.add(USERNAME);
		chatService.setActiveUsers(activeUsers);

		activeGibSamsUsers.add(GIB_SAMS_USER);
		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		assertEquals(1, chatService.getActiveUsers().size());
		assertEquals(1, chatService.getActiveGibSamsUsers().size());

		conversations = new HashMap<>();
		chatService.setConversations(conversations);

		ApiResponse response = chatService.startConversation(conversationRequest);

		assertNotNull(response);
		assertEquals(new ApiResponse(true, USERNAME + " added to contacts"), response);
		verify(simpMessagingTemplate, times(2)).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));

	}

	@Test(expected = GibSamsException.class)
	public void testStartConversationWhenGibSamsUserNotConnected() {
		chatService.startConversation(conversationRequest);
	}

	@Test
	public void testStartConversationWhenChatUserNotActive() {

		activeGibSamsUsers.add(GIB_SAMS_USER);
		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		ApiResponse response = chatService.startConversation(conversationRequest);
		assertEquals(new ApiResponse(false, USERNAME + " is no longer active"), response);
		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));
	}

	@Test
	public void testStartConversationWhenConversationInProgress() {

		activeUsers.add(USERNAME);
		chatService.setActiveUsers(activeUsers);

		activeGibSamsUsers.add(GIB_SAMS_USER);
		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		chatService.setConversations(conversations);

		ApiResponse response = chatService.startConversation(conversationRequest);
		assertEquals(new ApiResponse(false, USERNAME + " is already assigned to a GibSams volunteer"), response);
		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));

	}

	@Test
	public void testIsVolunteerAvailable() {

		chatService.setConversations(conversations);
		activeGibSamsUsers.add(GIB_SAMS_USER);
		chatService.setActiveGibSamsUsers(activeGibSamsUsers);

		ApiResponse response = chatService.isVolunteerActive(USERNAME);

		assertEquals(new ApiResponse(true, "Volunteer is active"), response);
	}

	@Test
	public void testIsVolunteerAvailableWhenNoConversation() {

		ApiResponse response = chatService.isVolunteerActive(USERNAME);

		assertEquals(new ApiResponse(false, "No active conversation found"), response);
	}

	@Test
	public void testIsVolunteerAvailableWhenNotActive() {

		chatService.setConversations(conversations);

		ApiResponse response = chatService.isVolunteerActive(USERNAME);

		assertEquals(new ApiResponse(false, "Volunteer is no longer active"), response);
	}
}