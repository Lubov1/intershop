package ru.yandex.practicum.intershop.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "NAME")
    private String name;
    private String description;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    private byte[] image;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private BasketItem basketItem;

    public Product(String name, String description, BigDecimal price, byte[] image, BasketItem basketItem) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.basketItem = basketItem;
    }
}
