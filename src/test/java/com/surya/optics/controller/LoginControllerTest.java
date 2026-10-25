package com.surya.optics.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class LoginControllerTest {
    @Test
    void rendersBrandedLoginPage() throws Exception {
        MockMvcBuilders.standaloneSetup(new LoginController()).build()
                .perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
