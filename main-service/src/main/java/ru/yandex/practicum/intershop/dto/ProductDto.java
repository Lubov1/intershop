package ru.yandex.practicum.intershop.dto;


import java.math.BigDecimal;

public record ProductDto( Long id, String name, String description,
                          BigDecimal price, String image, int quantity) {
}
