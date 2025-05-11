package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.BasketItem;

import java.util.List;

public interface CartRepository extends JpaRepository<BasketItem, Long> {
    @Override
    List<BasketItem> findAll();
}
