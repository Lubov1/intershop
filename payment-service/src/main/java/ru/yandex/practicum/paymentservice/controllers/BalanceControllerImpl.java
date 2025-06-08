package ru.yandex.practicum.paymentservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.paymentservice.services.BalanceService;
import ru.yandex.practicum.paymentservice.api.BalanceApi;
import ru.yandex.practicum.paymentservice.controllers.domain.Balance;

@RestController
public class BalanceControllerImpl implements BalanceApi {
    @Autowired
    BalanceService balanceService;
    @Override
    public Mono<ResponseEntity<Balance>> balanceGet(ServerWebExchange exchange) {
        return Mono.just(ResponseEntity.ok((new Balance()).amount(balanceService.getBalance())));
    }
}
