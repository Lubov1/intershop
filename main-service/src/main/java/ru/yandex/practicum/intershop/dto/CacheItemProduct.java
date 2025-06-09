package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.dao.Product;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheItemProduct {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;

    public CacheItemProduct(Product product) {
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.id = product.getId();
    }

}
