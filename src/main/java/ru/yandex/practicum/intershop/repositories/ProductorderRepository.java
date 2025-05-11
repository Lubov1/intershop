package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.intershop.dao.Productorder;

import java.util.List;

public interface ProductorderRepository extends JpaRepository<Productorder, Long> {
    List<Productorder> findAllByOrderId(Long order_id);
}
