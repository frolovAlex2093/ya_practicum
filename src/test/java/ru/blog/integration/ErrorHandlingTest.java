package ru.blog.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;
import ru.blog.config.WebConfig;
import ru.blog.controller.GlobalExceptionHandler;
import ru.blog.service.CommentService;
import ru.blog.service.PostService;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringJUnitWebConfig(ErrorHandlingTest.TestConfig.class)
class ErrorHandlingTest {

    private MockMvc mockMvc;

    @Configuration
    @Import({WebConfig.class, GlobalExceptionHandler.class})
    static class TestConfig {

        @Bean
        public BrokenController brokenController() {
            return new BrokenController();
        }

        @Bean
        public PostService postService() {
            return Mockito.mock(PostService.class);
        }

        @Bean
        public CommentService commentService() {
            return Mockito.mock(CommentService.class);
        }
    }

    @RestController
    static class BrokenController {
        @GetMapping("/api/error-test")
        public void throwError() {
            throw new RuntimeException("Unexpected Database Crash!");
        }
    }

    @BeforeEach
    void setup(@Autowired WebApplicationContext wac) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void shouldReturn500OnGenericException() throws Exception {
        mockMvc.perform(get("/api/error-test"))
                .andExpect(status().isInternalServerError()) // Ожидаем 500
                .andExpect(jsonPath("$.error").value(containsString("Unexpected Database Crash")));
    }
}