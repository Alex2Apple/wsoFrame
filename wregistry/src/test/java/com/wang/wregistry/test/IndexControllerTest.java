package com.wang.wregistry.test;

import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.wang.wregistry.web.controller.IndexController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class IndexControllerTest {

	@Test
	public void testIndexPage() throws Exception {
		IndexController controller = new IndexController();
		MockMvc mockMvc = standaloneSetup(controller).build();
		mockMvc.perform(get("/")).andExpect(view().name("index"));
	}
}
