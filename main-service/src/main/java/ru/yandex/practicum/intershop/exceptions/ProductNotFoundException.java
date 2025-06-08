package ru.yandex.practicum.intershop.exceptions;

public class ProductNotFoundException extends RuntimeException {
    private static final String message = "Product not found";
    public ProductNotFoundException(String message) {
        super(message);
    }
}
