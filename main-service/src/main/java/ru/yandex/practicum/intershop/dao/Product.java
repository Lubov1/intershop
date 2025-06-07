package ru.yandex.practicum.intershop.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private byte[] image;

    public Product(String name, String description, BigDecimal price, byte[] image, BasketItem basketItem) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
    }
}
