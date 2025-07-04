package ru.yandex.practicum.intershop.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.intershop.dao.BasketItem;
import ru.yandex.practicum.intershop.dao.Product;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Item {
    Long id;
    String name;
    String description;
    BigDecimal price;
    byte[] image;
    int quantity;

    public Item(Product product, BasketItem basketItem) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.image = product.getImage();
        this.quantity = basketItem.getQuantity();
    }

    public BigDecimal getPrice() {
        return price.multiply(new BigDecimal(quantity));
    }
}
