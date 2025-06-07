package ru.yandex.practicum.intershop.client;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.api.DefaultApi;
import ru.yandex.practicum.intershop.client.domain.Balance;

import java.math.BigDecimal;

@Service
public class ClientService {
    final DefaultApi defaultApi = new DefaultApi();

    public Mono<BigDecimal> getBalance() {
        return defaultApi.balanceGet().map(Balance::getAmount);
    }

    public Mono<BigDecimal> buyItems(BigDecimal price) {
        return defaultApi.buyPostWithResponseSpec(price)
                .bodyToMono(Balance.class)
                .map(Balance::getAmount);
    }
}
