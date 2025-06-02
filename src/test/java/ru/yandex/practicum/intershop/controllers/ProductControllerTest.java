package ru.yandex.practicum.intershop.controllers;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProductControllerTest extends ControllerTest {
    @Test
    void getProduct() throws Exception {
        Long productId = 5L;
        webTestClient.get().uri("/main/product/" + productId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("Пятый продукт"));
                    assertTrue(body.contains("Описание"));
                    assertTrue(body.contains("10.02"));
                    assertTrue(body.contains("<span>0</span>"));

                });
    }
}
