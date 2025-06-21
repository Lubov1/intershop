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
    Mono<BasketItem> findByProductIdAndUserName(Long id, String name);
    @Query("INSERT INTO BASKETITEM (product_id, quantity, user_name) VALUES (:id, :qty, :name) RETURNING *")
    Mono<BasketItem> insert(@Param("id") Long id, @Param("qty") int qty, @Param("name") String name);

    Flux<BasketItem> findAllByUserName(String username);

    Mono<Object> deleteAllByUserName(String userName);
}
