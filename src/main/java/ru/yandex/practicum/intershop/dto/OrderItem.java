package ru.yandex.practicum.intershop.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.intershop.dao.Orders;
import ru.yandex.practicum.intershop.dao.Product;
import ru.yandex.practicum.intershop.dao.Productorder;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class OrderItem {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private int quantity;
    private long orderId;

    public OrderItem(Productorder productorder) {
        Product product = productorder.getProduct();
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.image = product.getImage();
        this.quantity = productorder.getQuantity();
        this.orderId = productorder.getOrder().getId();
    }
}
