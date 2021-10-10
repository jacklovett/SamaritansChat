package com.samaritans.samaritanscoremodule.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.gson.Gson;
import com.samaritans.samaritanscoremodule.dao.ChatConfigDao;
import com.samaritans.samaritanscoremodule.exception.ResourceNotFoundException;
import com.samaritans.samaritanscoremodule.model.ChatConfig;
import com.samaritans.samaritanscoremodule.requests.ChatConfigRequest;
import com.samaritans.samaritanscoremodule.responses.ApiResponse;

@ExtendWith(MockitoExtension.class)
class ChatConfigControllerTest {

	private static final int ID = 1;
	private static final int AVAILABLE_FROM = 0;
	private static final int AVAILABLE_UNTIL = 12;

	private Gson gson;
	private MockMvc mockMvc;

	private ChatConfig config;
	private ChatConfigRequest chatConfigRequest;

	@Mock
	private ChatConfigDao chatConfigDao;

	@InjectMocks
	private ChatConfigController chatConfigController;

	@BeforeEach
	void setUp() {

		gson = new Gson();

		config = new ChatConfig();
		config.setId(ID);
		config.setTimeRestricted(true);
		config.setAvailableFrom(AVAILABLE_FROM);
		config.setAvailableUntil(AVAILABLE_UNTIL);

		chatConfigRequest = new ChatConfigRequest(config);

		this.mockMvc = MockMvcBuilders.standaloneSetup(chatConfigController).build();
	}

	@Test
	void testGetChatConfig() throws Exception {

		when(chatConfigDao.findConfig()).thenReturn(config);

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/config")
				.contentType(MediaType.APPLICATION_JSON);

		final MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		final MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(chatConfigRequest), response.getContentAsString());
	}

	@Test
	void testGetChatNotFound() throws Exception {

		doThrow(new ResourceNotFoundException("No chat config found")).when(chatConfigDao).findConfig();

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/config/")
				.contentType(MediaType.APPLICATION_JSON);

		final MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		final MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}

	@Test
	void testUpdateConfig() throws Exception {

		when(chatConfigDao.findConfig()).thenReturn(config);

		final String json = gson.toJson(chatConfigRequest);

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/config/edit")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		final MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		final MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(true, "Settings updated successfully")), response.getContentAsString());
	}

	@Test
	void testUpdateConfigWhenNotFound() throws Exception {

		doThrow(new ResourceNotFoundException("No chat config found")).when(chatConfigDao).findConfig();

		final String json = gson.toJson(chatConfigRequest);

		final RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/config/edit")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		final MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		final MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Unable to update chat settings")), response.getContentAsString());
	}

}
