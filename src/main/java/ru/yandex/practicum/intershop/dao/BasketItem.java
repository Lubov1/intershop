package ru.yandex.practicum.intershop.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "BASKETITEM")
public class BasketItem {
    @Id
    private Long productId;

    private int quantity;

    public BasketItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    @OneToOne
    @JoinColumn(name = "product_id", referencedColumnName="id", nullable = false)
    private Product product;

    public BigDecimal getPrice() {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }
}
