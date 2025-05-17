package ru.yandex.practicum.intershop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ru.yandex.practicum.intershop.dao.Product;

import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, Long> {

    @Query(
            value = "SELECT p FROM Product p LEFT JOIN FETCH p.basketItem",
            countQuery = "SELECT COUNT(p) FROM Product p"
    )
    Page<Product> findAll(Pageable pageable);
    @Query(
            value = "SELECT p FROM Product p LEFT JOIN FETCH p.basketItem b WHERE LOWER(p.name ) like CONCAT('%',:str,'%')",
            countQuery = "SELECT COUNT(p) FROM Product p"
    )
    Page<Product> findAllByNameContaining(Pageable pageable, @Param("str") String name);
    Optional<Product> findById(long id);

}
