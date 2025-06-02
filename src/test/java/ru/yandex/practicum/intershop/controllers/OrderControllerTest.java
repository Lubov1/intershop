package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderControllerTest extends ControllerTest {

    @Test
    void getOrders() throws Exception {
        webTestClient.get().uri("/orders")
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
    void GetOrder() throws Exception {
        Long orderId = 1L;

        webTestClient.get().uri("/orders/order/"+orderId)
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