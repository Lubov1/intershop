package ru.yandex.practicum.intershop.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;

import java.util.List;
import java.util.Optional;

public class SequrityTest extends ControllerTest {
    @Test
    void getCart() {
        webTestClient
                .get().uri("/cart")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }

    @Test
    void addToCartPlus() {
        Long productId = 1L;
        String action = "plus";
        webTestClient
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/update")
                        .queryParam("productId", String.valueOf(productId))
                        .queryParam("action", action)
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }

    @Test
    void buy() {
        webTestClient
                .post().uri(uriBuilder -> uriBuilder
                        .path("/cart/buy")
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }
    @Test
    void getOrders() {
        webTestClient
                .get().uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }

    @Test
    void GetOrder() {
        Long orderId = 1L;

        webTestClient
                .get().uri("/orders/order/"+orderId)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }

    @Test
    void createProduct() {
        Long productId = 1L;
        String action = "plus";
        webTestClient
                .post().uri(uriBuilder -> uriBuilder
                        .path("/main/createItem")
                        .queryParam("name", "name")
                        .queryParam("price", 12.0f)
                        .queryParam("description", "description")
                        .queryParam("image", Optional.ofNullable(null))
                        .build())
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/login");
    }

    @Test
    void create() {
        webTestClient
                .mutateWith(
                        SecurityMockServerConfigurers.mockAuthentication(
                                new UsernamePasswordAuthenticationToken(
                                        "user1", null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
                                )
                        ))
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/main/createItem")
                        .queryParam("name", "name")
                        .queryParam("price", 12.0f)
                        .queryParam("description", "description")
                        .queryParam("image", Optional.ofNullable(null))
                        .build())
                .exchange()
                .expectStatus().isForbidden();
    }
}
