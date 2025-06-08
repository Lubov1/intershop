package ru.yandex.practicum.paymentservice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.practicum.paymentservice.PaymentServiceApplication;
import ru.yandex.practicum.paymentservice.controllers.domain.Balance;

import java.math.BigDecimal;


@SpringBootTest(classes = PaymentServiceApplication.class)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class BuyControllersImplTest {
    @Autowired
    private WebTestClient webClient;

    @Value("${balance}")
    private BigDecimal balance;

    @Test
    public void testBuyFailed() {
        webClient.post().uri(uriBuilder -> uriBuilder.path("/buy")
                        .queryParam("price", balance.add(BigDecimal.ONE))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Balance.class)
                .isEqualTo(new Balance().amount(BigDecimal.ONE.negate()));
    }

    @Test
    public void testBuySuccess() {
        webClient.post().uri(uriBuilder -> uriBuilder.path("/buy")
                        .queryParam("price", balance)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Balance.class)
                .isEqualTo(new Balance().amount(BigDecimal.ZERO));
    }
}