package ru.yandex.practicum.intershop.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Productorder {
    @EmbeddedId
    private Productorderid productorderid;

    private int quantity;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name="order_id")
    private Orders order;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    public Productorder(Productorderid productorderid, int quantity) {
        this.productorderid = productorderid;
        this.quantity = 1;
    }
}
