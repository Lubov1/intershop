package ru.yandex.practicum.intershop.repositories;


import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.Product;

import java.util.List;


public interface ProductRepository extends R2dbcRepository<Product, Long> {
    Flux<Product> findAllBy(Pageable pageable);
    Flux<Product> searchAllByNameContaining(String name, Pageable pageable);
    Mono<Long> countByNameContaining(String name);
    Flux<Product> findAllById(List<Long> ids);
}
