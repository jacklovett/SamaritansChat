package com.gibsams.gibsamscoremodule.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

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

import com.gibsams.gibsamscoremodule.dao.ChatConfigDao;
import com.gibsams.gibsamscoremodule.model.ChatConfig;
import com.gibsams.gibsamscoremodule.requests.ChatConfigRequest;
import com.gibsams.gibsamscoremodule.responses.ApiResponse;
import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class ChatConfigControllerTest {

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

	@Before
	public void setUp() {

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
	public void testGetChatConfig() throws Exception {

		when(chatConfigDao.findConfig()).thenReturn(config);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/config")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(chatConfigRequest), response.getContentAsString());
	}

	@Test
	public void testGetChatNotFound() throws Exception {

		when(chatConfigDao.findConfig()).thenReturn(null);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/config/")
				.contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
	}

	@Test
	public void testUpdateConfig() throws Exception {

		when(chatConfigDao.findConfig()).thenReturn(config);

		String json = gson.toJson(chatConfigRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/config/edit")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.OK.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(true, "Settings updated successfully")),
				response.getContentAsString());
	}

	@Test
	public void testUpdateConfigWhenNotFound() throws Exception {

		when(chatConfigDao.findConfig()).thenReturn(null);

		String json = gson.toJson(chatConfigRequest);

		RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/api/config/edit")
				.accept(MediaType.APPLICATION_JSON).content(json).contentType(MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		MockHttpServletResponse response = result.getResponse();

		assertNotNull(response);
		assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
		assertEquals(gson.toJson(new ApiResponse(false, "Unable to update chat settings")),
				response.getContentAsString());
	}

}
