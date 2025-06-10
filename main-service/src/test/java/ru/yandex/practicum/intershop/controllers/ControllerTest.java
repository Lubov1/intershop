package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.intershop.IntershopApplication;

@Testcontainers
@SpringBootTest(classes = {IntershopApplication.class, EmbeddedRedisConfiguration.class})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ControllerTest {
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("junit")
            .withPassword("junit")
            .withInitScript("schema.sql")
            .withReuse(true);
    @Autowired
    WebTestClient webTestClient;
    @BeforeAll
    public static void beforeAll() {
        postgres.start();
    }
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () ->
                "r2dbc:postgresql://%s:%d/%s".formatted(
                        postgres.getHost(),
                        postgres.getFirstMappedPort(),
                        postgres.getDatabaseName()
                ));
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

}
