package ru.yandex.practicum.intershop.repositories;


import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.practicum.intershop.dao.BasketItem;

@Repository
public interface CartRepository extends R2dbcRepository<BasketItem, Long> {
    Flux<BasketItem> findAll();
    Mono<BasketItem> findByProductId(Long id);
    @Query("INSERT INTO BASKETITEM (product_id, quantity) VALUES (:id, :qty) RETURNING *")
    Mono<BasketItem> insert(@Param("id") Long id, @Param("qty") int qty);
}
