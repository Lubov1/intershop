package ru.yandex.practicum.intershop.repositories;


import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.Product;



public interface ProductRepository extends R2dbcRepository<Product, Long> {
    Mono<byte[]> findImageById(Long id);
}
