package ru.yandex.practicum.paymentservice.controllers;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.paymentservice.PaymentServiceApplication;
import ru.yandex.practicum.paymentservice.controllers.domain.Balance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.*;


@SpringBootTest(classes = PaymentServiceApplication.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BuyControllersImplTest {
    @Autowired
    WebTestClient webTestClient;

    @Value("${balance}")
    private BigDecimal balance;


    @Test
    @Order(1)
    void balanceGet() {
        webTestClient
                .mutateWith(mockOAuth2Login()
                        .oauth2User(new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                                Map.of("sub", "user1","email", "user1@example.com" ),"email")
                        ))
                .get().uri("/balance")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Balance.class)
                .isEqualTo((new Balance()).amount(balance));
    }


    @Test
    @Order(2)
    public void testBuyFailed() {
        webTestClient
                .mutateWith(
                        mockJwt()
                                .jwt(jwt -> jwt
                                        .subject("user1")
                                        .claim("email", "user1@example.com"))
                                .authorities(List.of(
                                        new SimpleGrantedAuthority("ROLE_ADMIN")))
                )
                .post().uri(uriBuilder -> uriBuilder.path("/buy")
                        .queryParam("price", balance.add(BigDecimal.ONE))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Balance.class)
                .isEqualTo(new Balance().amount(BigDecimal.ONE.negate()));
    }

    @Test
    @Order(3)
    public void testBuySuccess() {
        webTestClient
                .mutateWith(mockOAuth2Login()
                        .oauth2User(new DefaultOAuth2User(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")),
                                Map.of("sub", "user1","email", "user1@example.com" ),"email")
                        ))
                .post().uri(uriBuilder -> uriBuilder.path("/buy")
                        .queryParam("price", balance)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Balance.class)
                .isEqualTo(new Balance().amount(BigDecimal.ZERO));
    }
}