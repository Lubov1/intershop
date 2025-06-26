package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CartControllerTest extends ControllerTest {

    @AfterEach
    void resetDb(TestInfo testInfo, @Autowired DatabaseClient databaseClient) throws IOException {
        if (testInfo.getTags().contains("init-db")) {
            String sql = Files.readString(Path.of("src/test/resources/schema.sql"));
            for (var command: sql.split(";")) {
                if (!command.trim().isEmpty()) {
                    databaseClient.sql(command).then().block();
                }
            }
        }

    }
    @Test
    void getCart() {
        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get().uri("/cart")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<form"));
                    assertTrue(body.contains("Первый продукт"));
                    assertTrue(body.contains("Второй продукт"));
                    assertTrue(body.contains("Третий продукт"));
                });

    }

    @Test
    void addToCartPlus() {
        Long productId = 1L;
        String action = "plus";
        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/update")
                        .queryParam("productId", String.valueOf(productId))
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().isSeeOther();

        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get()
                .uri("/main/product/" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Первый продукт"));
                    assertTrue(body.contains("<span>3</span>"));
                    assertFalse(body.contains("<span>2</span>"));

                });
    }

    @Test
    void addToCartMinus() {
        Long productId = 2L;
        String action = "minus";
        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/update")
                        .queryParam("productId", String.valueOf(productId))
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().isSeeOther();

        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get().uri("/main/product/" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Второй продукт"));
                    assertTrue(body.contains("<span>0</span>"));
                    assertFalse(body.contains("<span>1</span>"));

                });
    }

    @Test
    void notExistAction() {
        Long productId = 1L;
        String action = "not exist";
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/update")
                        .queryParam("productId", String.valueOf(productId))
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Tag("init-db")
    void buy() {
        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/buy")
                        .build())
                .exchange()
                .expectStatus().isSeeOther();

        webTestClient.mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get().uri("/cart")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertFalse(body.contains("продукт"));
                });

        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<a href=\"/orders/order/3\">3</a>"));
                });

    }
}