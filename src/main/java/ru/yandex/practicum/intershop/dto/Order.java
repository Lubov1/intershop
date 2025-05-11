package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.intershop.dao.Orders;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class Order {
    Long id;
    BigDecimal price;
    List<OrderItem> items;
    public Order(Orders orders, List<OrderItem> items) {
        this.id = orders.getId();
        this.price = orders.getPrice();
        this.items = items;
    }
}
