package ru.yandex.practicum.intershop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.dao.Productorder;

@Data
@NoArgsConstructor
public class OrderItem extends Item{
    private long orderId;

    public OrderItem(Productorder productorder) {
        super(productorder.getProduct(), productorder.getQuantity());
        this.orderId = productorder.getOrder().getId();
    }
}
