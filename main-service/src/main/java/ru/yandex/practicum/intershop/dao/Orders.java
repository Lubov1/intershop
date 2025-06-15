package ru.yandex.practicum.intershop.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orders {
    @Id
    Long id;

    private BigDecimal price;

    private String userName;

    public Orders(BigDecimal price, String userName) {
        this.price = price;
        this.userName = userName;
    }
}
