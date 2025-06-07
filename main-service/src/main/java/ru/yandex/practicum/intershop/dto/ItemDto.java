package ru.yandex.practicum.intershop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.codec.multipart.FilePart;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ItemDto {
    private String name;
    private BigDecimal price;
    private String description;
    private FilePart image;
}
