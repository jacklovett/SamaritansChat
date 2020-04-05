package com.gibsams.gibsamscoremodule.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gibsams.gibsamscoremodule.exception.GibSamsException;
import com.gibsams.gibsamscoremodule.exception.ResourceNotFoundException;
import com.gibsams.gibsamscoremodule.model.Notification;
import com.gibsams.gibsamscoremodule.repository.NotificationRepository;
import com.gibsams.gibsamscoremodule.responses.NotificationResponse;

public class NotificationDao {

	@Autowired
	private NotificationRepository notificationRepository;

	private static final Logger logger = LoggerFactory.getLogger(NotificationDao.class);

	/**
	 * Find notifications for user
	 * 
	 * @param username
	 * @return List of notifications
	 */
	public List<NotificationResponse> findNotificationByUserId(Long userId) {
		return notificationRepository.findAllByUserByOrderByIdDesc(userId).stream().map(NotificationResponse::new)
				.collect(Collectors.toList());
	}

	public Notification save(Notification notification) {
		return notificationRepository.save(notification);
	}

	/**
	 * Delete notification with the provided id
	 * 
	 * @param id
	 */
	public boolean deleteNotificationById(Long id) {
		boolean result = false;
		try {
			notificationRepository.deleteById(id);
			result = true;
		} catch (IllegalArgumentException e) {
			throw new GibSamsException("Unable to delete notification with id: " + id, e);
		}
		return result;
	}

	public void deleteReadAndProcessedNotifications() {
		try {
			notificationRepository.deleteReadAndProcessedNotifications();
		} catch (Exception e) {
			logger.error("Unable to delete notifications", e);
		}
	}

	public Notification findById(Long id) {
		return notificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No notification found with id: " + id));
	}
}
