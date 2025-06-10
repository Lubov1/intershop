package ru.yandex.practicum.intershop.controllers;

import io.lettuce.core.RedisClient;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;
import redis.embedded.RedisServer;
import ru.yandex.practicum.intershop.client.ClientService;
import ru.yandex.practicum.intershop.client.domain.Balance;

import java.io.IOException;
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.when;

@Configuration
public class EmbeddedRedisConfiguration {

    @Bean(destroyMethod = "stop")
    public RedisServer redisServer() throws IOException {
        var redisServer = new RedisServer();
        redisServer.start();
        return redisServer;
    }

    @Bean
    public ClientService clientService() throws IOException {
        ClientService clientService = Mockito.mock(ClientService.class);
        when(clientService.getBalance()).thenReturn(Mono.just(BigDecimal.valueOf(30)));
        when(clientService.buyItems(any())).thenReturn(Mono.just(BigDecimal.valueOf(30)));
        return clientService;
    }

}