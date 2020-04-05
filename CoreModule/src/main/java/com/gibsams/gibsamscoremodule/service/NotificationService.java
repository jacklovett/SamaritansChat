package com.gibsams.gibsamscoremodule.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.gibsams.gibsamscoremodule.dao.NotificationDao;
import com.gibsams.gibsamscoremodule.dao.UserDao;
import com.gibsams.gibsamscoremodule.model.Notification;
import com.gibsams.gibsamscoremodule.model.User;
import com.gibsams.gibsamscoremodule.requests.NotificationRequest;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.responses.NotificationResponse;
import com.gibsams.gibsamscoremodule.utils.NotificationTypeEnum;

@Service
public class NotificationService {

	private static final String CHAT = "/chat/";

	@Autowired
	private UserDao userDao;
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

		return notifications.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

	}

	public void addNotification(NotificationTypeEnum notificationTypeEnum, String recipient, String chatUser) {

		Notification notification = new Notification(notificationTypeEnum, chatUser);

		if (StringUtils.isNotBlank(recipient)) {
			User gibSamsUser = userDao.findUserByUsernameOrEmail(recipient);
			if (gibSamsUser != null) {
				notification.setUser(gibSamsUser);
				// get notification id
				notification = notificationDao.save(notification);
				simpMessagingTemplate.convertAndSend(CHAT + "notifications/" + recipient,
						new NotificationResponse(notification));
			} else {
				logger.error("No user found for recipient: {}. Unable to send notification.", recipient);
			}
		} else {
			logger.error("Notification recipient not specified. Unable to send notification");
		}
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
