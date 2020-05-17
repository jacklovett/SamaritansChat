package com.samaritans.samaritanscoremodule.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.samaritans.samaritanscoremodule.model.Notification;
import com.samaritans.samaritanscoremodule.responses.NotificationResponse;
import com.samaritans.samaritanscoremodule.service.NotificationService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(MockitoJUnitRunner.class)
public class NotificationControllerTest {

	private static final Long ID = 1l;

	private Gson gson;
	private MockMvc mockMvc;
	private Notification notification;
	private NotificationResponse notificationResponse;
	private List<NotificationResponse> notificationResponses;

	@Mock
	private NotificationService notificationService;
	@InjectMocks
	private NotificationController notificationController;

	@Before
	public void setUp() {

		gson = new GsonBuilder().serializeNulls().create();

		notification = new Notification();

		notificationResponse = new NotificationResponse(notification);

		notificationResponses = new ArrayList<>();
		notificationResponses.add(notificationResponse);

		this.mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
	}

	@Test
	public void testGetNotifications() throws Exception {

		when(notificationService.findNotifications(ID)).thenReturn(notificationResponses);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/notifications/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(notificationResponses), response.getContentAsString());
	}

	@Test
	public void testGetNotificationsWhenEmpty() throws Exception {

		notificationResponses.clear();

		when(notificationService.findNotifications(ID)).thenReturn(notificationResponses);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/notifications/" + ID)
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals("[]", response.getContentAsString());

	}

	@Test
	public void testDeleteNotification() throws Exception {

		RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/api/notifications/delete/" + ID);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
	}
}
