package com.github.yogurt.sample.test;

import com.github.yogurt.core.Configuration;
import com.github.yogurt.sample.test.controller.TestController;
import com.github.yogurt.sample.test.service.TestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author: jtwu
 * @Date: 2018/11/3 17:26
 */

@RunWith(SpringRunner.class)
@WebMvcTest(TestController.class)
public class TestControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private TestService testService;

    @MockBean
    private Configuration configuration;

    @Test
    public void testGet() throws Exception {
        mvc.perform(get("/tests")).andExpect(status().isOk());
    }
}
