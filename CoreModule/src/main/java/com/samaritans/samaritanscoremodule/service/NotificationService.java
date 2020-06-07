package com.samaritans.samaritanscoremodule.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.samaritans.samaritanscoremodule.dao.BoUserDao;
import com.samaritans.samaritanscoremodule.dao.NotificationDao;
import com.samaritans.samaritanscoremodule.model.BoUser;
import com.samaritans.samaritanscoremodule.model.Notification;
import com.samaritans.samaritanscoremodule.requests.NotificationRequest;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;
import com.samaritans.samaritanscoremodule.responses.NotificationResponse;
import com.samaritans.samaritanscoremodule.utils.NotificationTypeEnum;

@Service
public class NotificationService {

	@Autowired
	private BoUserDao boUserDao;
	@Autowired
	private NotificationDao notificationDao;
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public List<NotificationResponse> findNotifications(final Long userId) {

		final List<NotificationResponse> notifications = notificationDao.findNotificationByUserId(userId);

		if (notifications.isEmpty()) {
			logger.info("No notifications found in database");
		}

		return notifications;
	}

	public void addNotification(final NotificationTypeEnum notificationType, final String recipient,
			final String chatUser) {

		Notification notification = new Notification(notificationType, chatUser);

		if (StringUtils.isBlank(recipient)) {
			logger.error("Notification recipient not specified. Unable to send notification");
			return;
		}

		final Optional<BoUser> user = boUserDao.findUserByUsernameOrEmail(recipient);

		if (!user.isPresent()) {
			logger.error("No user found for recipient: {}. Unable to send notification.", recipient);
			return;
		}

		final BoUser samaritansUser = user.get();
		notification.setUser(samaritansUser);
		// get notification id
		notification = notificationDao.save(notification);
		simpMessagingTemplate.convertAndSend("/topic/" + "notifications." + recipient,
				new NotificationResponse(notification));
	}

	public ApiResponse deleteNotification(final Long id) {
		final boolean result = notificationDao.deleteNotificationById(id);
		return result ? new ApiResponse(true, "Notification deleted")
				: new ApiResponse(false, "Unable to delete notfication");
	}

	public void deleteNotifications() {
		notificationDao.deleteReadAndProcessedNotifications();
	}

	public void updateNotification(final NotificationRequest notificationRequest) {
		final Long notificationId = notificationRequest.getId();
		try {
			final Notification notification = notificationDao.findById(notificationId);
			notification.setRead(notificationRequest.isRead());
			notification.setProcessed(notificationRequest.isProcessed());
			notificationDao.save(notification);
		} catch (final Exception ex) {
			logger.error("Unable to update notification with id: {}", notificationId, ex);
		}
	}
}
