package ru.yandex.practicum.paymentservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.paymentservice.api.BuyApi;
import ru.yandex.practicum.paymentservice.controllers.domain.Balance;

import java.math.BigDecimal;

@RestController
public class BuyControllersImpl implements BuyApi {
    @Autowired
    public BalanceService balanceService;
    @Override
    public Mono<ResponseEntity<Balance>> buyPost(BigDecimal price, ServerWebExchange exchange) {
        BigDecimal balance = balanceService.getBalance().subtract(price);
        if (balance.compareTo(BigDecimal.ZERO) >= 0) {
            balanceService.setBalance(balance);
        }
        return Mono.just(ResponseEntity.ok().body((new Balance().amount(balance))));
    }
}
