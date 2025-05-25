package ru.yandex.practicum.intershop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.dao.Product;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class OrderItem {
    Long id;
    String name;
    String description;
    BigDecimal price;
    byte[] image;
    private long orderId;
    private int quantity;

    public OrderItem(Long orderId, Product product, int quantity) {
        this.orderId = orderId;
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.image = product.getImage();
        this.quantity = quantity;
    }
}
