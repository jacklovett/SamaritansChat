package com.gibsams.gibsamscoremodule.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gibsams.gibsamscoremodule.requests.NotificationRequest;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.gibsams.gibsamscoremodule.responses.NotificationResponse;
import com.gibsams.gibsamscoremodule.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationService;

	private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

	/**
	 * Return the notifications for the provided user id
	 * 
	 * @param user id
	 * @return List of Notifications
	 */
	@GetMapping("/{id}")
	public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable Long id) {
		logger.info("NotificationController - getNotifications - init");

		List<NotificationResponse> notifications = notificationService.findNotifications(id);

		if (notifications.isEmpty()) {
			logger.info("No notifications found in database");
		}

		return ResponseEntity.ok(notifications);
	}

	@PutMapping("/edit")
	public void updateNotification(@Valid @RequestBody NotificationRequest notificationRequest) {
		logger.info("NotificationController - updateNotification - init");
		notificationService.updateNotification(notificationRequest);
	}

	/**
	 * Delete specified notification
	 * 
	 * @param id
	 */
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<ApiResponse> deleteNotification(@PathVariable Long id) {
		logger.info("NotificationController - deleteNotification - init");
		return ResponseEntity.ok(notificationService.deleteNotification(id));
	}

	/**
	 * Delete read and processed notifications
	 */
	@DeleteMapping("/delete")
	public void deleteNotifications() {
		logger.info("NotificationController - deleteNotifications - init");
		notificationService.deleteNotifications();
	}
}
