package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest extends ControllerTest {

    @Test
    void getOrders() {
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
                    assertFalse(body.contains("<a href=\"/orders/order/3\">3</a>"));
                    assertTrue(body.contains("<a href=\"/orders/order/2\">2</a>"));
                    assertTrue(body.contains("<a href=\"/orders/order/1\">1</a>"));
                });
    }

    @Test
    void GetOrder() {
        Long orderId = 1L;

        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .get().uri("/orders/order/"+orderId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertFalse(body.contains("<form"));
                    assertTrue(body.contains("Первый продукт"));
                    assertTrue(body.contains("Второй продукт"));
                    assertFalse(body.contains("Третий продукт"));
                });
    }
}