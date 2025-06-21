package ru.yandex.practicum.intershop.configs;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dto.CacheItemProduct;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Product> redisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<Product> serializer =
                new Jackson2JsonRedisSerializer<>(Product.class);

        RedisSerializationContext<String, Product> context = RedisSerializationContext
                .<String, Product>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, CacheItemProduct[]> redisTemplateArray(ReactiveRedisConnectionFactory factory, ObjectMapper objectMapper) {
        Jackson2JsonRedisSerializer<CacheItemProduct[]> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, CacheItemProduct[].class);

        RedisSerializationContext<String, CacheItemProduct[]> context = RedisSerializationContext
                .<String, CacheItemProduct[]>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, String> userSessionMap(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<String> serializer =
                new Jackson2JsonRedisSerializer<>(String.class);

        RedisSerializationContext<String, String> context = RedisSerializationContext
                .<String, String>newSerializationContext(new StringRedisSerializer())
                .value(serializer)
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}