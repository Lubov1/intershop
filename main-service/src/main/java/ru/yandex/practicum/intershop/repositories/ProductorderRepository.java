package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import ru.yandex.practicum.intershop.dao.Productorder;

import java.util.List;


public interface ProductorderRepository extends R2dbcRepository<Productorder, Long> {
    Flux<Productorder> saveAll(List<Productorder> rStream);
}
