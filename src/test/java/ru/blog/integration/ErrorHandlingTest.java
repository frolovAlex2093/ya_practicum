package ru.blog.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ErrorHandlingTest.BrokenConfig.class)
class ErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @TestConfiguration
    static class BrokenConfig {
        @Bean
        public BrokenController brokenController() {
            return new BrokenController();
        }
    }

    // Тестовый контроллер, который всегда падает
    @RestController
    static class BrokenController {
        @GetMapping("/api/error-test")
        public void throwError() {
            throw new RuntimeException("Unexpected Database Crash!");
        }
    }

    @Test
    void shouldReturn500OnGenericException() throws Exception {
        // Проверяем, что GlobalExceptionHandler перехватывает ошибку
        mockMvc.perform(get("/api/error-test"))
                .andExpect(status().isInternalServerError()) // Ожидаем 500
                .andExpect(jsonPath("$.error").value(containsString("Unexpected Database Crash")));
    }
}