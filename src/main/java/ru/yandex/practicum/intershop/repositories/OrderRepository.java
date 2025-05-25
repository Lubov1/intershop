package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import ru.yandex.practicum.intershop.dao.Orders;


public interface OrderRepository extends R2dbcRepository<Orders, Long> {
}
