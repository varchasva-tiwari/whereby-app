package com.demo.whereby.controllerTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @BeforeClass
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void userRegistrationHandler() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/registerUser")
                        .accept(MediaType.ALL)
        ).andExpect(status().isOk());
    }

    @Test
    void loginHandler() {
    }

    @Test
    void registrationPage() {
    }

    @Test
    void homePageHandler() {
    }
}