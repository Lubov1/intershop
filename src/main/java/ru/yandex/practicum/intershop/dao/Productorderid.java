package ru.yandex.practicum.intershop.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Productorderid implements Serializable {
    @Column(name="product_id")
    private Long productId;

    @Column(name="order_id")
    private Long orderId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Productorderid that = (Productorderid) o;
        return Objects.equals(productId, that.productId) && Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, orderId);
    }
}
