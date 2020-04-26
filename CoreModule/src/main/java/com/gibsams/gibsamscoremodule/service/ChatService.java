package com.gibsams.gibsamscoremodule.service;

import java.text.MessageFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.ChatConfigDao;
import com.gibsams.gibsamscoremodule.dao.ChatDao;
import com.gibsams.gibsamscoremodule.dao.ChatUserDao;
import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.BoUser;
import com.gibsams.gibsamscoremodule.model.ChatConfig;
import com.gibsams.gibsamscoremodule.model.ChatMessage;
import com.gibsams.gibsamscoremodule.model.ChatUser;
import com.gibsams.gibsamscoremodule.model.User;
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

@Service
public class ChatService {

	private static final String CHAT = "/chat/";

	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	@Autowired
	private ChatUserDao chatUserDao;

	@Autowired
	private ChatDao chatDao;

	@Autowired
	private ChatConfigDao chatConfigDao;

	@Autowired
	private UserService userService;

	@Autowired
	private ChatUserService chatUserService;

	@Autowired
	private NotificationService notificationService;

	private Set<String> activeUsers = new HashSet<>();

	private Set<String> activeGibSamsUsers = new HashSet<>();

	private Map<String, String> conversations = new HashMap<>();

	private Gson gson = new Gson();

	private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

	public Message sendMessage(Message message) {

		String sender = message.getSender();
		String recipient = message.getRecipient();

		if (!conversations.containsKey(sender) && !conversations.containsKey(recipient)) {
			throw new GibSamsException("No conversation found. Unable to send chat message");
		}

		if (AppConstants.GIB_SAMS_USERNAME.equalsIgnoreCase(recipient)) {
			return sendToGibSamsUser(sender, message);
		} else {
			return sendToChatUser(sender, recipient, message);
		}

	}

	public void disconnect(Message message) {

		if (!message.getType().equals(MessageType.LEAVE)) {
			throw new GibSamsException("Invalid message recieved. Unable to disconnect");
		}

		String username = message.getSender();

		User user = userService.getUserByUsername(username);

		if (user instanceof BoUser) {
			disconnectGibSamsUser(message, user);
			return;
		}

		if (user instanceof ChatUser) {
			disconnectChatUser(message, user);
			return;
		}

		logger.error("Unable to disconnect user: {} from chat", username);
	}

	public void addActiveUser(Message message) {

		String username = message.getSender();

		if (activeUsers.contains(username) || activeGibSamsUsers.contains(username)) {
			logger.info("User already active");
			return;
		}

		User user = userService.getUserByUsername(username);

		if (user instanceof BoUser) {
			addActiveGibSamsUser(username, message);
			return;
		}

		addActiveChatUser(username);

	}

	public ApiResponse startConversation(ConversationRequest conversationRequest) {

		String gibSamsUser = conversationRequest.getGibSamsUser();
		String chatUser = conversationRequest.getChatUser();

		if (!activeGibSamsUsers.contains(gibSamsUser)) {
			throw new GibSamsException("You are not connected to chat");
		}

		if (!activeUsers.contains(chatUser)) {
			return new ApiResponse(false, chatUser + " is no longer active");
		}

		if (conversations.containsKey(chatUser)) {
			return new ApiResponse(false, chatUser + " is already assigned to a GibSams volunteer");
		} else {
			conversations.put(chatUser, gibSamsUser);
		}

		Message joinMessage = new Message();
		joinMessage.setType(MessageType.JOIN);
		joinMessage.setDateSent(Instant.now());
		joinMessage.setRecipient(chatUser);
		// gib sams username is used to keep volunteer private
		joinMessage.setSender(AppConstants.GIB_SAMS_USERNAME);
		// send join message to public user
		simpMessagingTemplate.convertAndSend(CHAT + chatUser, joinMessage);

		joinMessage.setSender(chatUser);
		joinMessage.setRecipient(gibSamsUser);
		// send join message to gibsams user
		simpMessagingTemplate.convertAndSend(CHAT + gibSamsUser, joinMessage);

		return new ApiResponse(true, chatUser + " added to contacts");
	}

	public String getChatUsers(String username) {

		Set<String> usersActiveChatUsers = getActiveChatUsersForGibsSamsUser(username);

		Set<ChatUserResponse> chatUsers = usersActiveChatUsers.stream()
				.map(user -> new ChatUserResponse(user, chatDao.findNumberOfUnreadMessagesByUsername(user)))
				.collect(Collectors.toSet());

		return gson.toJson(chatUsers);
	}

	/**
	 * Get from database the list of chat messages by username
	 * 
	 * @param username
	 * @return List of Messages
	 */
	public List<Message> getMessagesByUsername(String username) {
		return chatDao.findChatMessagesByUsername(username);
	}

	/**
	 * Check whether chat is available Checks chat configuration
	 * 
	 * @return boolean
	 */
	public ChatAvailabilityResponse isChatAvailable() {

		Optional<ChatAvailabilityResponse> isChatBlocked = isChatBlockedByConfig();

		if (isChatBlocked.isPresent()) {
			return isChatBlocked.get();
		}

		if (this.activeGibSamsUsers.isEmpty()) {
			return new ChatAvailabilityResponse(ChatAvailabilityEnum.NO_VOLUNTEERS);
		}

		return new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE);
	}

	public void updateUnreadMessages(String username) {
		try {
			int numOfUpdatedMessages = chatDao.updateSeenMessagesByUsername(username);
			if (numOfUpdatedMessages > 0) {
				logger.info("{} unread messages successfully updated for user: {}", numOfUpdatedMessages, username);
			}
		} catch (Exception e) {
			logger.error("Unable to update unread messages for user: {}", username, e);
		}
	}

	public ApiResponse isVolunteerActive(String username) {

		String activeGibSamsUser = conversations.get(username);

		if (StringUtils.isBlank(activeGibSamsUser)) {
			return new ApiResponse(false, "No active conversation found");
		}

		if (activeGibSamsUsers.contains(activeGibSamsUser)) {
			return new ApiResponse(true, "Volunteer is active");
		} else {
			return new ApiResponse(false, "Volunteer is no longer active");
		}
	}

	protected Set<String> getActiveUsers() {
		return activeUsers;
	}

	protected void setActiveUsers(Set<String> activeUsers) {
		this.activeUsers = activeUsers;
	}

	protected Set<String> getActiveGibSamsUsers() {
		return activeGibSamsUsers;
	}

	protected void setActiveGibSamsUsers(Set<String> activeGibSamsUsers) {
		this.activeGibSamsUsers = activeGibSamsUsers;
	}

	protected void setConversations(Map<String, String> conversations) {
		this.conversations = conversations;
	}

	/**
	 * Send message to assigned gib sams volunteer
	 * 
	 * @param sender
	 * @param message
	 * @return message
	 */
	private Message sendToGibSamsUser(String sender, Message message) {
		// return message to chat user
		simpMessagingTemplate.convertAndSend(CHAT + sender, message);
		// find gibsams user to send to.
		String recipient = conversations.get(sender);
		message.setRecipient(recipient);
		ChatMessage savedMessage = chatDao.save(message);
		message.setDateSent(savedMessage.getDateCreated());
		simpMessagingTemplate.convertAndSend(CHAT + recipient, message);
		return message;
	}

	/**
	 * Send message to chat user
	 * 
	 * @param recipient
	 * @param message
	 * @return message
	 */
	private Message sendToChatUser(String sender, String recipient, Message message) {

		ChatMessage savedMessage = chatDao.save(message);
		message.setDateSent(savedMessage.getDateCreated());
		// return message to sender
		simpMessagingTemplate.convertAndSend(CHAT + sender, message);
		message.setSender(AppConstants.GIB_SAMS_USERNAME);
		// send to chat user
		simpMessagingTemplate.convertAndSend(CHAT + recipient, message);

		return message;
	}

	private void addActiveChatUser(String username) {
		activeUsers.add(username);
		for (String gibSamsUser : activeGibSamsUsers) {
			notificationService.addNotification(NotificationTypeEnum.NEW_USER_CONNECTED, gibSamsUser, username);
		}
	}

	private void addActiveGibSamsUser(String username, Message message) {
		// if chat is available then add gibsams user
		Optional<ChatAvailabilityResponse> isChatBlocked = isChatBlockedByConfig();
		if (isChatBlocked.isPresent()) {
			logger.info("Chat is blocked by configuration settings");
			return;
		}
		this.activeGibSamsUsers.add(username);
		// let new users know chat is available
		simpMessagingTemplate.convertAndSend(CHAT + "availability",
				new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));

		// inform this users conversations that they are active again
		Set<String> conversationKeys = getActiveChatUsersForGibsSamsUser(username);
		message.setSender(AppConstants.GIB_SAMS_USERNAME);
		for (String key : conversationKeys) {
			simpMessagingTemplate.convertAndSend(CHAT + key, message);
		}
	}

	private void disconnectChatUser(Message message, User user) {
		String username = user.getUsername();
		logger.info("User {} disconnected", username);
		this.activeUsers.remove(username);
		String recipient = conversations.get(username);

		if (StringUtils.isNotBlank(recipient)) {
			this.conversations.remove(username, recipient);
			simpMessagingTemplate.convertAndSend(CHAT + recipient, message);
			// need to send this to correct user
			notificationService.addNotification(NotificationTypeEnum.USER_DISCONNECTED, recipient, username);
		} else {
			// check to see if any messages were sent
			// if none, then we will delete the user completely.
			List<Message> messages = getMessagesByUsername(username);
			if (messages.isEmpty()) {
				// delete user completely
				// no notification needed ??
				chatUserDao.deleteUserById(user.getId());
				return;
			} else {

				Message lastMessage = messages.get(messages.size() - 1);
				// determine who the gibSams volunteer was.
				recipient = lastMessage.getRecipient().equalsIgnoreCase(username) ? lastMessage.getSender()
						: lastMessage.getRecipient();

				notificationService.addNotification(NotificationTypeEnum.USER_DISCONNECTED, recipient, username);
			}
		}

		chatUserService.disableChatUser(username);
	}

	private void disconnectGibSamsUser(Message message, User user) {

		String username = user.getUsername();
		logger.info("GibSams user {} disconnected from chat", username);
		message.setSender(AppConstants.GIB_SAMS_USERNAME);
		// get all chat users for gibsams user
		Set<String> conversationKeys = getActiveChatUsersForGibsSamsUser(username);

		for (String key : conversationKeys) {
			simpMessagingTemplate.convertAndSend(CHAT + key, message);
		}

		this.activeGibSamsUsers.remove(username);
		if (this.activeGibSamsUsers.isEmpty()) {
			simpMessagingTemplate.convertAndSend(CHAT + "availability",
					new ChatAvailabilityResponse(ChatAvailabilityEnum.NO_VOLUNTEERS));
		}
	}

	private Optional<ChatAvailabilityResponse> isChatBlockedByConfig() {

		Optional<ChatAvailabilityResponse> chatAvailabilityResponse = Optional.empty();
		ChatConfig config = null;

		try {
			config = chatConfigDao.findConfig();

			int availableFrom = config.getAvailableFrom();
			int availableUntil = config.getAvailableUntil();

			if (config.isTimeRestricted()) {

				LocalTime now = LocalTime.now();
				int currentHour = now.getHour();

				if (currentHour < availableFrom || currentHour >= availableUntil) {

					chatAvailabilityResponse = Optional
							.of(new ChatAvailabilityResponse(ChatAvailabilityEnum.UNAVAILABLE_TIME));

					chatAvailabilityResponse.get()
							.setMessage(MessageFormat.format(ChatAvailabilityEnum.UNAVAILABLE_TIME.getMessage(),
									formatTimes(availableFrom), formatTimes(availableUntil)));
				}
			}
		} catch (ResourceNotFoundException ex) {
			logger.error("Unable to check chat config's availability settings: ", ex);
		}

		return chatAvailabilityResponse;
	}

	private Set<String> getActiveChatUsersForGibsSamsUser(String username) {
		return conversations.entrySet().stream().filter(entry -> username.equalsIgnoreCase(entry.getValue()))
				.map(Entry::getKey).collect(Collectors.toSet());
	}

	private String formatTimes(int time) {

		if (time < 10) {
			return "0" + time + ":00";
		} else if (time == 24) {
			return "23:59";
		} else {
			return time + ":00";
		}
	}

}
