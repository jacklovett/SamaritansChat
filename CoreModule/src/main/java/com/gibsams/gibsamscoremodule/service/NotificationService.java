package com.gibsams.gibsamscoremodule.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.BoUserDao;
import com.gibsams.gibsamscoremodule.dao.NotificationDao;
import com.gibsams.gibsamscoremodule.model.BoUser;
import com.gibsams.gibsamscoremodule.model.Notification;
import com.gibsams.gibsamscoremodule.requests.NotificationRequest;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.responses.NotificationResponse;
import com.gibsams.gibsamscoremodule.utils.NotificationTypeEnum;

@Service
public class NotificationService {

	private static final String CHAT = "/chat/";

	@Autowired
	private BoUserDao boUserDao;
	@Autowired
	private NotificationDao notificationDao;
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;

	private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public List<NotificationResponse> findNotifications(Long userId) {

		List<NotificationResponse> notifications = notificationDao.findNotificationByUserId(userId);

		if (notifications.isEmpty()) {
			logger.info("No notifications found in database");
		}

		return notifications;
	}

	public void addNotification(NotificationTypeEnum notificationType, String recipient, String chatUser) {

		Notification notification = new Notification(notificationType, chatUser);

		if (StringUtils.isBlank(recipient)) {
			logger.error("Notification recipient not specified. Unable to send notification");
			return;
		}

		Optional<BoUser> user = boUserDao.findUserByUsernameOrEmail(recipient);

		if (!user.isPresent()) {
			logger.error("No user found for recipient: {}. Unable to send notification.", recipient);
			return;
		}

		BoUser gibSamsUser = user.get();
		notification.setUser(gibSamsUser);
		// get notification id
		notification = notificationDao.save(notification);
		simpMessagingTemplate.convertAndSend(CHAT + "notifications/" + recipient,
				new NotificationResponse(notification));
	}

	public ApiResponse deleteNotification(Long id) {
		boolean result = notificationDao.deleteNotificationById(id);
		if (result)
			return new ApiResponse(true, "Notification deleted");
		else {
			return new ApiResponse(false, "Unable to delete notfication");
		}

	}

	public void deleteNotifications() {
		notificationDao.deleteReadAndProcessedNotifications();
	}

	public void updateNotification(NotificationRequest notificationRequest) {
		Long notificationId = notificationRequest.getId();
		try {
			Notification notification = notificationDao.findById(notificationId);
			notification.setRead(notificationRequest.isRead());
			notification.setProcessed(notificationRequest.isProcessed());
			notificationDao.save(notification);
		} catch (Exception ex) {
			logger.error("Unable to update notification with id: " + notificationId, ex);
		}
	}
}
