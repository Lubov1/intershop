package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.yandex.practicum.intershop.dao.Product;

import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    Page<Product> findAll(Pageable pageable);
    Optional<Product> findById(long id);

}
