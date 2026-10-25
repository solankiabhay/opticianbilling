package com.surya.optics.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.View;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class LoginControllerTest {
    @Test
    void rendersBrandedLoginPage() throws Exception {
        // Standalone MockMvc does not configure Thymeleaf's real template resolver.
        // A no-op View lets this focused controller test assert routing without
        // attempting to forward to /login as if it were a JSP.
        View testView = (model, request, response) -> { };
        MockMvcBuilders.standaloneSetup(new LoginController())
                .setViewResolvers((viewName, locale) -> {
                    assertEquals("login", viewName);
                    return testView;
                })
                .build()
                .perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
