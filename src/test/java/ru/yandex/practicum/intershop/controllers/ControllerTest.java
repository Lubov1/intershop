package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import ru.yandex.practicum.intershop.IntershopApplication;

@SpringBootTest(classes = IntershopApplication.class)
@AutoConfigureMockMvc
public class ControllerTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15") // Имя и версия образа
            .withDatabaseName("testdb")
                .withUsername("junit")
                .withPassword("junit").withReuse(true);
    @Autowired
    MockMvc mockMvc;
    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

}