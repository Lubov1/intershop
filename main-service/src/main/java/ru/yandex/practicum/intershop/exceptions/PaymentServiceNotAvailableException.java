package ru.yandex.practicum.intershop.exceptions;

public class PaymentServiceNotAvailableException extends RuntimeException {
    private static final String message = "Payment service is not available";
    public PaymentServiceNotAvailableException() {
        super(message);
    }
}
