package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.*;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.practicum.intershop.IntershopApplication;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;

@Testcontainers
@SpringBootTest(classes = {IntershopApplication.class, EmbeddedRedisConfiguration.class})
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ControllerTest {
    @MockitoBean
    ReactiveClientRegistrationRepository clientRepo;
    @MockitoBean
    ServerOAuth2AuthorizedClientRepository authClientRepo;
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("junit")
            .withPassword("junit")
            .withInitScript("schema.sql")
            .withReuse(true);
    @Autowired
    ApplicationContext applicationContext;

    WebTestClient webTestClient;
    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
                .apply(springSecurity())
                .build();
    }
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
