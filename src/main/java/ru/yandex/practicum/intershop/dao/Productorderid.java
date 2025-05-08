package ru.yandex.practicum.intershop.dao;

import jakarta.persistence.*;

import java.io.Serializable;

@Embeddable
public class Productorderid implements Serializable {
    @Column(name="product_id")
    private Long productId;

    @Column(name="order_id")
    private Long orderId;

}
