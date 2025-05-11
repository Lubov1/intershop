package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.intershop.dao.Orders;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findById(Long id);

}
