package ru.yandex.practicum.intershop.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Productorder {
    private Long productId;
    private Long orderId;
    private int quantity;
}
