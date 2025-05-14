package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.intershop.dao.BasketItem;

import java.util.List;

public interface CartRepository extends JpaRepository<BasketItem, Long> {
    @Query(value = "SELECT b FROM BasketItem b INNER JOIN FETCH b.product")
    List<BasketItem> findAll();
}
