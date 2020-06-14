package com.samaritans.samaritanscoremodule.service;

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

import com.google.gson.Gson;
import com.samaritans.samaritanscoremodule.dao.ChatConfigDao;
import com.samaritans.samaritanscoremodule.dao.ChatDao;
import com.samaritans.samaritanscoremodule.dao.ChatUserDao;
import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.model.ChatConfig;
import com.samaritans.samaritanscoremodule.model.ChatMessage;
import com.samaritans.samaritanscoremodule.model.ChatUser;
import com.samaritans.samaritanscoremodule.model.User;
import com.samaritans.samaritanscoremodule.requests.ConversationRequest;
import com.samaritans.samaritanscoremodule.requests.Message;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.ChatAvailabilityResponse;
import com.samaritans.samaritanscoremodule.utils.AppConstants;
import com.samaritans.samaritanscoremodule.utils.ChatAvailabilityEnum;
import com.samaritans.samaritanscoremodule.utils.MessageType;
import com.samaritans.samaritanscoremodule.utils.NotificationTypeEnum;

@Service
public class ChatService {

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

	private Set<String> activeSamaritansUsers = new HashSet<>();

	private Map<String, String> conversations = new HashMap<>();

	private final Gson gson = new Gson();

	private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

	public Message sendMessage(final Message message) {

		final String sender = message.getSender();
		final String recipient = message.getRecipient();

		if (!conversations.containsKey(sender) && !conversations.containsKey(recipient)) {
			throw new SamaritansException("No conversation found. Unable to send chat message");
		}

		if (AppConstants.SAMARITANS_USERNAME.equalsIgnoreCase(recipient)) {
			return sendToSamaritansUser(sender, message);
		} else {
			return sendToChatUser(sender, recipient, message);
		}

	}

	public void disconnect(final Message message) {

		if (!message.getType().equals(MessageType.LEAVE)) {
			throw new SamaritansException("Invalid message recieved. Unable to disconnect");
		}

		final String username = message.getSender();

		final User user = userService.getUserByUsername(username);

		if (user instanceof BoUser) {
			disconnectSamaritansUser(message, user);
			return;
		}

		if (user instanceof ChatUser) {
			disconnectChatUser(message, user);
			return;
		}

		logger.error("Unable to disconnect user: {} from chat", username);
	}

	/**
	 * Add active user to chat
	 * 
	 * @param Message
	 * @throws SamaritansException
	 */
	public void addActiveUser(final Message message) {

		final String username = message.getSender();

		if (StringUtils.isBlank(username)) {
			throw new SamaritansException("Unable to connect to chat: No user found");
		}

		if (activeUsers.contains(username) || activeSamaritansUsers.contains(username)) {
			logger.info("User {} already active", username);
			return;
		}

		final User user = userService.getUserByUsername(username);

		if (user instanceof BoUser) {
			addActiveSamaritansUser(username, message);
		} else {
			addActiveChatUser(username);
		}
	}

	/**
	 * Starts the conversation session between the two participants
	 * 
	 * @param conversationRequest
	 * @throws SamaritansException
	 * @return ApiResponse
	 */
	public ApiResponse startConversation(final ConversationRequest conversationRequest) {

		final String samaritansUser = conversationRequest.getSamaritansUser();
		final String chatUser = conversationRequest.getChatUser();

		if (!activeSamaritansUsers.contains(samaritansUser)) {
			throw new SamaritansException("You are not connected to chat");
		}

		if (!activeUsers.contains(chatUser)) {
			return new ApiResponse(false, chatUser + " is no longer active");
		}

		if (conversations.containsKey(chatUser)) {
			return new ApiResponse(false, chatUser + " is already assigned to a volunteer");
		}

		conversations.put(chatUser, samaritansUser);

		final Message joinMessage = new Message();
		joinMessage.setType(MessageType.JOIN);
		joinMessage.setDateSent(Instant.now());
		joinMessage.setRecipient(chatUser);
		// samaritans username is used to keep volunteer private
		joinMessage.setSender(AppConstants.SAMARITANS_USERNAME);
		// send join message to public user
		convertAndSend(chatUser, joinMessage);

		joinMessage.setSender(chatUser);
		joinMessage.setRecipient(samaritansUser);
		// send join message to samaritans user
		convertAndSend(samaritansUser, joinMessage);

		return new ApiResponse(true, chatUser + " added to contacts");
	}

	public String getChatUsers(final String username) {
		return gson.toJson(getActiveChatUsersForUser(username));
	}

	/**
	 * Get from database the list of chat messages by username
	 * 
	 * @param username
	 * @return List of Messages
	 */
	public List<Message> getMessagesByUsername(final String username) {
		return chatDao.findChatMessagesByUsername(username);
	}

	/**
	 * Check whether chat is available Checks chat configuration
	 * 
	 * @return boolean
	 */
	public ChatAvailabilityResponse isChatAvailable() {

		final Optional<ChatAvailabilityResponse> isChatBlocked = isChatBlockedByConfig();

		if (isChatBlocked.isPresent()) {
			return isChatBlocked.get();
		}

		if (this.activeSamaritansUsers.isEmpty()) {
			return new ChatAvailabilityResponse(ChatAvailabilityEnum.NO_VOLUNTEERS);
		}

		return new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE);
	}

	public void updateUnreadMessages(final String username) {
		try {
			final int numOfUpdatedMessages = chatDao.updateSeenMessagesByUsername(username);
			if (numOfUpdatedMessages > 0) {
				logger.info("{} unread messages successfully updated for user: {}", numOfUpdatedMessages, username);
			}
		} catch (final Exception e) {
			logger.error("Unable to update unread messages for user: {}", username, e);
		}
	}

	public ApiResponse isVolunteerActive(final String username) {

		final String activeSamaritansUser = conversations.get(username);

		if (StringUtils.isBlank(activeSamaritansUser)) {
			return new ApiResponse(false, "No active conversation found");
		}

		if (activeSamaritansUsers.contains(activeSamaritansUser)) {
			return new ApiResponse(true, "Volunteer is active");
		} else {
			return new ApiResponse(false, "Volunteer is no longer active");
		}
	}

	protected Set<String> getActiveUsers() {
		return activeUsers;
	}

	protected void setActiveUsers(final Set<String> activeUsers) {
		this.activeUsers = activeUsers;
	}

	protected Set<String> getActiveSamaritansUsers() {
		return activeSamaritansUsers;
	}

	protected void setActiveSamaritansUsers(final Set<String> activeSamaritansUsers) {
		this.activeSamaritansUsers = activeSamaritansUsers;
	}

	protected void setConversations(final Map<String, String> conversations) {
		this.conversations = conversations;
	}

	/**
	 * Send message to assigned samaritans volunteer
	 * 
	 * @param sender
	 * @param message
	 * @return message
	 */
	private Message sendToSamaritansUser(final String sender, final Message message) {
		// return message to chat user
		convertAndSend(sender, message);
		// find samaritans user to send to.
		final String recipient = conversations.get(sender);
		message.setRecipient(recipient);
		final ChatMessage savedMessage = chatDao.save(message);
		message.setDateSent(savedMessage.getDateCreated());
		convertAndSend(recipient, message);
		return message;
	}

	/**
	 * Send message to chat user
	 * 
	 * @param recipient
	 * @param message
	 * @return message
	 */
	private Message sendToChatUser(final String sender, final String recipient, final Message message) {

		final ChatMessage savedMessage = chatDao.save(message);
		message.setDateSent(savedMessage.getDateCreated());
		// return message to sender
		convertAndSend(sender, message);
		message.setSender(AppConstants.SAMARITANS_USERNAME);
		// send to chat user
		convertAndSend(recipient, message);

		return message;
	}

	private void addActiveChatUser(final String username) {
		activeUsers.add(username);
		for (final String samaritansUser : activeSamaritansUsers) {
			notificationService.addNotification(NotificationTypeEnum.NEW_USER_CONNECTED, samaritansUser, username);
		}
	}

	private void addActiveSamaritansUser(final String username, final Message message) {
		// if chat is available then add samaritans user
		final Optional<ChatAvailabilityResponse> isChatBlocked = isChatBlockedByConfig();
		if (isChatBlocked.isPresent()) {
			logger.info("Chat is blocked by configuration settings");
			return;
		}
		this.activeSamaritansUsers.add(username);
		// let new users know chat is available
		convertAndSend("availability", new ChatAvailabilityResponse(ChatAvailabilityEnum.AVAILABLE));

		// inform this users conversations that they are active again
		final Set<String> conversationKeys = getActiveChatUsersForUser(username);
		message.setSender(AppConstants.SAMARITANS_USERNAME);
		for (final String key : conversationKeys) {
			convertAndSend(key, message);
		}
	}

	private void disconnectChatUser(final Message message, final User user) {
		final String username = user.getUsername();
		logger.info("User {} disconnected", username);
		this.activeUsers.remove(username);
		String recipient = conversations.get(username);

		if (StringUtils.isNotBlank(recipient)) {
			this.conversations.remove(username, recipient);
			convertAndSend(recipient, message);
			// need to send this to correct user
			notificationService.addNotification(NotificationTypeEnum.USER_DISCONNECTED, recipient, username);
		} else {
			// check to see if any messages were sent
			// if none, then we will delete the user completely.
			final List<Message> messages = getMessagesByUsername(username);
			if (messages.isEmpty()) {
				// delete user completely
				// no notification needed ??
				chatUserDao.deleteUserById(user.getId());
				return;
			} else {

				final Message lastMessage = messages.get(messages.size() - 1);
				// determine who the samaritans volunteer was.
				recipient = lastMessage.getRecipient().equalsIgnoreCase(username) ? lastMessage.getSender()
						: lastMessage.getRecipient();

				notificationService.addNotification(NotificationTypeEnum.USER_DISCONNECTED, recipient, username);
			}
		}

		chatUserService.disableChatUser(username);
	}

	private void disconnectSamaritansUser(final Message message, final User user) {

		final String username = user.getUsername();
		logger.info("Samaritans user {} disconnected from chat", username);
		message.setSender(AppConstants.SAMARITANS_USERNAME);
		// get all chat users for samaritans user
		final Set<String> conversationKeys = getActiveChatUsersForUser(username);

		for (final String key : conversationKeys) {
			convertAndSend(key, message);
		}

		this.activeSamaritansUsers.remove(username);
		if (this.activeSamaritansUsers.isEmpty()) {
			convertAndSend("availability", new ChatAvailabilityResponse(ChatAvailabilityEnum.NO_VOLUNTEERS));
		}
	}

	private Optional<ChatAvailabilityResponse> isChatBlockedByConfig() {

		Optional<ChatAvailabilityResponse> chatAvailabilityResponse = Optional.empty();
		ChatConfig config = null;

		try {
			config = chatConfigDao.findConfig();

			final int availableFrom = config.getAvailableFrom();
			final int availableUntil = config.getAvailableUntil();

			if (config.isTimeRestricted()) {

				final LocalTime now = LocalTime.now();
				final int currentHour = now.getHour();

				if (currentHour < availableFrom || currentHour >= availableUntil) {

					chatAvailabilityResponse = Optional
							.of(new ChatAvailabilityResponse(ChatAvailabilityEnum.UNAVAILABLE_TIME));

					chatAvailabilityResponse.get()
							.setMessage(MessageFormat.format(ChatAvailabilityEnum.UNAVAILABLE_TIME.getMessage(),
									formatTimes(availableFrom), formatTimes(availableUntil)));
				}
			}
		} catch (final ResourceNotFoundException ex) {
			logger.error("Unable to check chat config's availability settings: ", ex);
		}

		return chatAvailabilityResponse;
	}

	private Set<String> getActiveChatUsersForUser(final String username) {
		return conversations.entrySet().stream().filter(entry -> username.equalsIgnoreCase(entry.getValue()))
				.map(Entry::getKey).collect(Collectors.toSet());
	}

	private void convertAndSend(final String destination, final Object payload) {
		simpMessagingTemplate.convertAndSend("/topic/" + destination, payload);
	}

	private String formatTimes(final int time) {

		if (time < 10) {
			return "0" + time + ":00";
		} else if (time == 24) {
			return "23:59";
		} else {
			return time + ":00";
		}
	}

}
