package ru.yandex.practicum.intershop.dto;

import jakarta.persistence.Column;
import ru.yandex.practicum.intershop.dao.Product;

import java.math.BigDecimal;

public record ProductDto( Long id, String name, String description,
                          BigDecimal price, String image, int quantity) {
}
