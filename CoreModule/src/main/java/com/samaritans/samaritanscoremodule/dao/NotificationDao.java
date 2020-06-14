package com.samaritans.samaritanscoremodule.dao;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.exception.SamaritansException;
import com.samaritans.samaritanscoremodule.model.Notification;
import com.samaritans.samaritanscoremodule.repository.NotificationRepository;
import com.samaritans.samaritanscoremodule.responses.NotificationResponse;

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
	public List<NotificationResponse> findNotificationByUserId(final Long userId) {
		return notificationRepository.findAllByUserByOrderByIdDesc(userId).stream().map(NotificationResponse::new)
				.collect(Collectors.toList());
	}

	public Notification save(final Notification notification) {
		return notificationRepository.save(notification);
	}

	/**
	 * Delete notification with the provided id
	 * 
	 * @param id
	 */
	public void deleteNotificationById(final Long id) {
		try {
			notificationRepository.deleteById(id);
		} catch (final IllegalArgumentException e) {
			throw new SamaritansException("Unable to delete notification with id: " + id, e);
		}
	}

	public void deleteReadAndProcessedNotifications() {
		try {
			notificationRepository.deleteReadAndProcessedNotifications();
		} catch (final Exception e) {
			logger.error("Unable to delete notifications", e);
		}
	}

	public Notification findById(final Long id) {
		return notificationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No notification found with id: " + id));
	}
}
