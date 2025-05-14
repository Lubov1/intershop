package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.intershop.dao.Orders;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findById(Long id);

    @Query(value = "SELECT o from Orders o LEFT JOIN FETCH o.productorders")
    List<Orders> findAll();
}
