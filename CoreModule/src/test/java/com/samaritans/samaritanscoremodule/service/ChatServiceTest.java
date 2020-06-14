package com.samaritans.samaritanscoremodule.service;

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

import com.google.gson.Gson;
import com.samaritans.samaritanscoremodule.dao.ChatConfigDao;
import com.samaritans.samaritanscoremodule.dao.ChatDao;
import com.samaritans.samaritanscoremodule.dao.ChatUserDao;
import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.model.ChatConfig;
import com.samaritans.samaritanscoremodule.model.ChatMessage;
import com.samaritans.samaritanscoremodule.model.ChatUser;
import com.samaritans.samaritanscoremodule.requests.ConversationRequest;
import com.samaritans.samaritanscoremodule.requests.Message;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.ChatAvailabilityResponse;
import com.samaritans.samaritanscoremodule.responses.ChatUserResponse;
import com.samaritans.samaritanscoremodule.utils.AppConstants;
import com.samaritans.samaritanscoremodule.utils.ChatAvailabilityEnum;
import com.samaritans.samaritanscoremodule.utils.MessageType;
import com.samaritans.samaritanscoremodule.utils.NotificationTypeEnum;

@RunWith(MockitoJUnitRunner.class)
public class ChatServiceTest {

	private static final Long ID = 1L;
	private static final String TOPIC = "/topic/";
	private static final int AVAILABLE_FROM = 0;
	private static final int AVAILABLE_UNTIL = 24;
	private static final String USERNAME = "jlove09876";
	private static final String USERNAME_2 = "lovej1234";
	private static final String SAMARITANS_USER = "samaritansUser";
	private static final String MESSAGE_CONTENT = "I'm a little teapot!";

	private Gson gson;
	private ChatUser chatUser;
	private BoUser boUser;
	private ChatConfig config;
	private Message message;
	private ChatMessage chatMessage;
	private Set<String> activeUsers;
	private Set<String> activeSamaritansUsers;
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
		chatUser.setUsername(SAMARITANS_USER);

		boUser = new BoUser();
		boUser.setId(ID);
		boUser.setUsername(SAMARITANS_USER);

		config = new ChatConfig();
		config.setTimeRestricted(true);
		config.setAvailableFrom(AVAILABLE_FROM);
		config.setAvailableUntil(AVAILABLE_UNTIL);

		conversations = new HashMap<>();
		conversations.put(USERNAME, SAMARITANS_USER);

		message = new Message();
		message.setSender(USERNAME);
		message.setContent(MESSAGE_CONTENT);
		message.setType(MessageType.CHAT);

		chatMessage = new ChatMessage(message);
		chatMessage.setDateCreated(Instant.now());

		chatMessages = new ArrayList<>();
		chatMessages.add(message);

		activeUsers = new HashSet<>();
		activeSamaritansUsers = new HashSet<>();

		conversationRequest = new ConversationRequest();
		conversationRequest.setSamaritansUser(SAMARITANS_USER);
		conversationRequest.setChatUser(USERNAME);

	}

	@Test
	public void testSendMessageAsChatUser() {

		message.setRecipient(AppConstants.SAMARITANS_USERNAME);

		when(chatDao.save(message)).thenReturn(chatMessage);

		chatService.setConversations(conversations);

		final Message responseMessage = chatService.sendMessage(message);

		assertNotNull(responseMessage);
		assertEquals(USERNAME, responseMessage.getSender());
		assertEquals(SAMARITANS_USER, responseMessage.getRecipient());
		assertEquals(chatMessage.getDateCreated(), responseMessage.getDateSent());
		verify(chatDao, times(1)).save(responseMessage);
		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + SAMARITANS_USER, responseMessage);
	}

	@Test
	public void testSendMessageAsSamaritansUser() {

		chatService.setConversations(conversations);

		message.setSender(SAMARITANS_USER);
		message.setRecipient(USERNAME);

		when(chatDao.save(message)).thenReturn(chatMessage);

		final Message responseMessage = chatService.sendMessage(message);

		assertNotNull(responseMessage);
		assertEquals(USERNAME, responseMessage.getRecipient());
		assertEquals(AppConstants.SAMARITANS_USERNAME, responseMessage.getSender());
		assertEquals(chatMessage.getDateCreated(), responseMessage.getDateSent());
		verify(chatDao, times(1)).save(message);
		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + USERNAME, responseMessage);
	}

	@Test(expected = SamaritansException.class)
	public void testSendMessageWhenNoConversationFound() {

		chatService.setConversations(new HashMap<>());
		chatService.sendMessage(message);

		verify(chatDao, never()).save(Mockito.any(Message.class));
		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));
	}

	@Test
	public void testDisconnectSamaritansUser() {

		activeSamaritansUsers.add(SAMARITANS_USER);
		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		assertEquals(1, chatService.getActiveSamaritansUsers().size());

		conversations.put(USERNAME_2, SAMARITANS_USER);
		chatService.setConversations(conversations);

		message.setSender(SAMARITANS_USER);
		message.setType(MessageType.LEAVE);

		when(userService.getUserByUsername(SAMARITANS_USER)).thenReturn(boUser);

		chatService.disconnect(message);

		message.setRecipient(USERNAME);
		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + USERNAME, message);

		message.setRecipient(USERNAME_2);
		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + USERNAME, message);

		assertTrue(chatService.getActiveSamaritansUsers().isEmpty());
		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.NO_VOLUNTEERS));

	}

	@Test
	public void testDisconnectChatUser() {

		chatUser.setUsername(USERNAME);

		when(userService.getUserByUsername(USERNAME)).thenReturn(chatUser);

		message.setSender(USERNAME);
		message.setRecipient(AppConstants.SAMARITANS_USERNAME);
		message.setType(MessageType.LEAVE);

		activeUsers.add(USERNAME);
		chatService.setActiveUsers(activeUsers);

		assertEquals(1, chatService.getActiveUsers().size());

		chatService.setConversations(conversations);

		chatService.disconnect(message);

		assertTrue(chatService.getActiveUsers().isEmpty());
		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.USER_DISCONNECTED, SAMARITANS_USER,
				USERNAME);
		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + SAMARITANS_USER, message);
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
		verify(notificationService, never()).addNotification(NotificationTypeEnum.USER_DISCONNECTED, SAMARITANS_USER,
				USERNAME);
		verify(simpMessagingTemplate, never()).convertAndSend(TOPIC + SAMARITANS_USER, message);

	}

	@Test
	public void testDisconnectWhenConversationNotFoundWhenMessagesExist() {

		chatUser.setUsername(USERNAME);

		when(userService.getUserByUsername(USERNAME)).thenReturn(chatUser);

		message.setRecipient(SAMARITANS_USER);
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
		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.USER_DISCONNECTED, SAMARITANS_USER,
				USERNAME);
		verify(simpMessagingTemplate, never()).convertAndSend(TOPIC + SAMARITANS_USER, message);
		verify(chatUserService, times(1)).disableChatUser(USERNAME);

	}

	@Test(expected = SamaritansException.class)
	public void testDisconnectIncorrectMessageType() {

		message.setSender(USERNAME);
		message.setRecipient(AppConstants.SAMARITANS_USERNAME);
		message.setType(MessageType.CHAT);

		chatService.disconnect(message);

	}

	@Test
	public void testAddActiveUserWhenSamaritansUserAndChatIsAvailable() {

		when(userService.getUserByUsername(SAMARITANS_USER)).thenReturn(boUser);
		when(chatConfigDao.findConfig()).thenReturn(config);

		message.setSender(SAMARITANS_USER);
		message.setType(MessageType.JOIN);

		chatService.setConversations(conversations);

		chatService.addActiveUser(message);

		assertEquals(1, chatService.getActiveSamaritansUsers().size());
		assertTrue(chatService.getActiveSamaritansUsers().contains(SAMARITANS_USER));

		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));

		message.setSender(AppConstants.SAMARITANS_USERNAME);
		verify(simpMessagingTemplate, times(1)).convertAndSend(TOPIC + USERNAME, message);
	}

	@Test
	public void testAddActiveUserWhenSamaritansUserAndChatIsUnAvailable() {

		when(userService.getUserByUsername(SAMARITANS_USER)).thenReturn(boUser);

		config.setAvailableFrom(24);
		config.setAvailableUntil(24);

		when(chatConfigDao.findConfig()).thenReturn(config);

		message.setSender(SAMARITANS_USER);
		message.setType(MessageType.JOIN);

		chatService.addActiveUser(message);

		assertEquals(0, chatService.getActiveSamaritansUsers().size());

		verify(simpMessagingTemplate, never()).convertAndSend(TOPIC + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));
	}

	@Test
	public void testAddActiveUserWhenSamaritansUserAlreadyConnected() {

		activeSamaritansUsers.add(SAMARITANS_USER);

		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		message.setSender(SAMARITANS_USER);
		message.setType(MessageType.JOIN);

		chatService.addActiveUser(message);

		assertEquals(1, chatService.getActiveSamaritansUsers().size());
		assertTrue(chatService.getActiveSamaritansUsers().contains(SAMARITANS_USER));

		verify(userService, never()).getUserByUsername(Mockito.anyString());
		verify(simpMessagingTemplate, never()).convertAndSend(TOPIC + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));
		verify(simpMessagingTemplate, never()).convertAndSend(TOPIC, message);
	}

	@Test
	public void testAddActiveUserWhenChatUser() {

		chatUser.setUsername(USERNAME);

		when(userService.getUserByUsername(USERNAME)).thenReturn(chatUser);

		message.setSender(USERNAME);
		message.setType(MessageType.JOIN);

		activeSamaritansUsers.add(SAMARITANS_USER);
		activeSamaritansUsers.add(AppConstants.SAMARITANS_USERNAME);

		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		chatService.addActiveUser(message);

		assertEquals(1, chatService.getActiveUsers().size());
		assertTrue(chatService.getActiveUsers().contains(USERNAME));

		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.NEW_USER_CONNECTED, SAMARITANS_USER,
				USERNAME);
		verify(notificationService, times(1)).addNotification(NotificationTypeEnum.NEW_USER_CONNECTED,
				AppConstants.SAMARITANS_USERNAME, USERNAME);
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

		conversations.put(USERNAME_2, SAMARITANS_USER);
		chatService.setConversations(conversations);

		final ChatUserResponse expectedChatUser1 = new ChatUserResponse(USERNAME, 1);
		final ChatUserResponse expectedChatUser2 = new ChatUserResponse(USERNAME_2, 1);

		final Set<ChatUserResponse> expectedUsers = new HashSet<>();
		expectedUsers.add(expectedChatUser1);
		expectedUsers.add(expectedChatUser2);

		when(chatDao.findNumberOfUnreadMessagesByUsername(Mockito.anyString())).thenReturn(1);

		final String response = chatService.getChatUsers(SAMARITANS_USER);

		assertNotNull(response);
		assertEquals(gson.toJson(expectedUsers), response);
		verify(chatDao, times(2)).findNumberOfUnreadMessagesByUsername(Mockito.anyString());

	}

	@Test
	public void testGetChatUsersWhenEmpty() {

		conversations = new HashMap<>();
		chatService.setConversations(conversations);

		final String response = chatService.getChatUsers(AppConstants.SAMARITANS_USERNAME);

		assertNotNull(response);
		assertEquals("[]", response);
		verify(chatDao, never()).findNumberOfUnreadMessagesByUsername(Mockito.anyString());
	}

	@Test
	public void testIsChatAvailableFailsWhenNoVolunteers() {

		when(chatConfigDao.findConfig()).thenReturn(config);

		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		final ChatAvailabilityResponse response = chatService.isChatAvailable();

		assertNotNull(response);
		assertEquals(ChatAvailabilityEnum.NO_VOLUNTEERS.getType(), response.getType());
		assertEquals(ChatAvailabilityEnum.NO_VOLUNTEERS.getMessage(), response.getMessage());

	}

	@Test
	public void testIsChatAvailableSuccess() {

		when(chatConfigDao.findConfig()).thenReturn(config);

		activeSamaritansUsers.add(SAMARITANS_USER);
		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		final ChatAvailabilityResponse response = chatService.isChatAvailable();

		assertNotNull(response);
		assertEquals(ChatAvailabilityEnum.AVAILABLE.getType(), response.getType());
		assertEquals(ChatAvailabilityEnum.AVAILABLE.getMessage(), response.getMessage());
	}

	@Test
	public void testIsChatAvailableWhenTimeUnavailable() {

		config.setAvailableFrom(0);
		config.setAvailableUntil(0);

		when(chatConfigDao.findConfig()).thenReturn(config);

		final ChatAvailabilityResponse response = chatService.isChatAvailable();

		final String expectedMessage = MessageFormat.format(ChatAvailabilityEnum.UNAVAILABLE_TIME.getMessage(), "00:00",
				"00:00");

		assertNotNull(response);
		assertEquals(ChatAvailabilityEnum.UNAVAILABLE_TIME.getType(), response.getType());
		assertEquals(expectedMessage, response.getMessage());
	}

	@Test
	public void testGetMessagesByUsername() {

		when(chatDao.findChatMessagesByUsername(USERNAME)).thenReturn(chatMessages);

		final List<Message> response = chatService.getMessagesByUsername(USERNAME);

		assertNotNull(response);
		assertFalse(response.isEmpty());
		assertEquals(MessageType.CHAT, response.get(0).getType());
		assertEquals(message.getSender(), response.get(0).getSender());
		assertEquals(message.getContent(), response.get(0).getContent());
	}

	@Test
	public void testGetMessagesByUsernameWhenNoMessages() {

		final List<Message> response = chatService.getMessagesByUsername(USERNAME);

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

		activeSamaritansUsers.add(SAMARITANS_USER);
		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		assertEquals(1, chatService.getActiveUsers().size());
		assertEquals(1, chatService.getActiveSamaritansUsers().size());

		conversations = new HashMap<>();
		chatService.setConversations(conversations);

		final ApiResponse response = chatService.startConversation(conversationRequest);

		assertNotNull(response);
		assertEquals(new ApiResponse(true, USERNAME + " added to contacts"), response);
		verify(simpMessagingTemplate, times(2)).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));

	}

	@Test(expected = SamaritansException.class)
	public void testStartConversationWhenSamaritansUserNotConnected() {
		chatService.startConversation(conversationRequest);
	}

	@Test
	public void testStartConversationWhenChatUserNotActive() {

		activeSamaritansUsers.add(SAMARITANS_USER);
		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		final ApiResponse response = chatService.startConversation(conversationRequest);
		assertEquals(new ApiResponse(false, USERNAME + " is no longer active"), response);
		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));
	}

	@Test
	public void testStartConversationWhenConversationInProgress() {

		activeUsers.add(USERNAME);
		chatService.setActiveUsers(activeUsers);

		activeSamaritansUsers.add(SAMARITANS_USER);
		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		chatService.setConversations(conversations);

		final ApiResponse response = chatService.startConversation(conversationRequest);
		assertEquals(new ApiResponse(false, USERNAME + " is already assigned to a volunteer"), response);
		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(), Mockito.any(Message.class));

	}

	@Test
	public void testIsVolunteerAvailable() {

		chatService.setConversations(conversations);
		activeSamaritansUsers.add(SAMARITANS_USER);
		chatService.setActiveSamaritansUsers(activeSamaritansUsers);

		final ApiResponse response = chatService.isVolunteerActive(USERNAME);

		assertEquals(new ApiResponse(true, "Volunteer is active"), response);
	}

	@Test
	public void testIsVolunteerAvailableWhenNoConversation() {

		final ApiResponse response = chatService.isVolunteerActive(USERNAME);

		assertEquals(new ApiResponse(false, "No active conversation found"), response);
	}

	@Test
	public void testIsVolunteerAvailableWhenNotActive() {

		chatService.setConversations(conversations);

		final ApiResponse response = chatService.isVolunteerActive(USERNAME);

		assertEquals(new ApiResponse(false, "Volunteer is no longer active"), response);
	}
}