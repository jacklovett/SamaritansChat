package com.gibsams.gibsamscoremodule.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.gibsams.gibsamscoremodule.dao.BoUserDao;
import com.gibsams.gibsamscoremodule.dao.NotificationDao;
import com.gibsams.gibsamscoremodule.model.BoUser;
import com.gibsams.gibsamscoremodule.model.Notification;
import com.gibsams.gibsamscoremodule.requests.NotificationRequest;
import com.gibsams.gibsamscoremodule.responses.NotificationResponse;
import com.gibsams.gibsamscoremodule.utils.AppConstants;
import com.gibsams.gibsamscoremodule.utils.NotificationTypeEnum;

@RunWith(MockitoJUnitRunner.class)
public class NotificationServiceTest {

	private static final Long ID = 1l;
	private static final String CHAT_ENDPOINT = "/chat/notifications";
	private static final String USERNAME = "jlove0987";

	private BoUser boUser;
	private Notification notification;
	private NotificationRequest notificationRequest;
	private NotificationResponse notificationResponse;
	private List<NotificationResponse> notificationResponses;

	@Mock
	private BoUserDao boUserDao;
	@Mock
	private NotificationDao notificationDao;
	@Mock
	private SimpMessagingTemplate simpMessagingTemplate;
	@InjectMocks
	private NotificationService notificationService;

	@Before
	public void setUp() {

		boUser = new BoUser();
		boUser.setId(ID);
		boUser.setUsername(AppConstants.GIB_SAMS_USERNAME);

		notification = new Notification(NotificationTypeEnum.NEW_USER_CONNECTED, USERNAME);
		notification.setId(ID);

		notificationResponse = new NotificationResponse(notification);

		notificationResponses = new ArrayList<>();
		notificationResponses.add(notificationResponse);

		notificationRequest = new NotificationRequest();
		notificationRequest.setId(ID);
		notificationRequest.setProcessed(true);
		notificationRequest.setRead(true);

	}

	@Test
	public void testFindNotifications() {

		when(notificationDao.findNotificationByUserId(ID)).thenReturn(notificationResponses);

		List<NotificationResponse> response = notificationService.findNotifications(ID);

		assertNotNull(response);
		assertEquals(1, response.size());
		assertEquals(notificationResponse, response.get(0));
	}

	@Test
	public void testFindNotificationsWhenEmpty() {

		List<NotificationResponse> response = notificationService.findNotifications(ID);

		assertNotNull(response);
		assertTrue(response.isEmpty());
	}

	@Test
	public void testAddNotification() {

		when(boUserDao.findUserByUsernameOrEmail(AppConstants.GIB_SAMS_USERNAME)).thenReturn(Optional.of(boUser));

		notification = new Notification(NotificationTypeEnum.USER_DISCONNECTED, USERNAME);
		notification.setContent(MessageFormat.format(NotificationTypeEnum.USER_DISCONNECTED.getContent(), USERNAME));
		notification.setUser(boUser);

		doAnswer((Answer<Notification>) c -> {
			notification.setId(ID);
			return notification;
		}).when(notificationDao).save(Mockito.any(Notification.class));

		notificationService.addNotification(NotificationTypeEnum.USER_DISCONNECTED, AppConstants.GIB_SAMS_USERNAME,
				USERNAME);

		verify(simpMessagingTemplate, times(1)).convertAndSend(CHAT_ENDPOINT + "/" + AppConstants.GIB_SAMS_USERNAME,
				new NotificationResponse(notification));
		verify(notificationDao, times(1)).save(Mockito.any(Notification.class));

	}

	@Test
	public void testAddNotificationWhenRecipientNotSpecified() {

		notification = new Notification(NotificationTypeEnum.NEW_USER_CONNECTED, USERNAME);

		notificationService.addNotification(NotificationTypeEnum.NEW_USER_CONNECTED, null, USERNAME);

		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(),
				Mockito.any(NotificationResponse.class));
		verify(notificationDao, never()).save(Mockito.any(Notification.class));

	}

	@Test
	public void testAddNotificationWhenUserNotFound() {

		when(boUserDao.findUserByUsernameOrEmail(AppConstants.GIB_SAMS_USERNAME)).thenReturn(Optional.empty());

		notification.setContent(MessageFormat.format(NotificationTypeEnum.USER_DISCONNECTED.getContent(), USERNAME));

		notificationService.addNotification(NotificationTypeEnum.USER_DISCONNECTED, AppConstants.GIB_SAMS_USERNAME,
				USERNAME);

		verify(simpMessagingTemplate, never()).convertAndSend(Mockito.anyString(),
				Mockito.any(NotificationResponse.class));
		verify(notificationDao, never()).save(Mockito.any(Notification.class));
	}

	@Test
	public void testUpdateNotification() {

		when(notificationDao.findById(ID)).thenReturn(notification);

		notificationService.updateNotification(notificationRequest);

		verify(notificationDao, times(1)).save(notification);
	}

	@Test
	public void testUpdateNotificationWhenNotificationNotFound() {

		notificationService.updateNotification(notificationRequest);
		verify(notificationDao, never()).save(Mockito.any(Notification.class));
	}
}
