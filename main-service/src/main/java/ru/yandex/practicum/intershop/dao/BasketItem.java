package ru.yandex.practicum.intershop.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("BASKETITEM")
public class BasketItem {
    @Id
    private Long productId;
    private int quantity;
    private String userName;
}
