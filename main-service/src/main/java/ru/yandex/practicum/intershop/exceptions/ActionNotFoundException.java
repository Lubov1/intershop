package ru.yandex.practicum.intershop.exceptions;

public class ActionNotFoundException  extends RuntimeException {
    private static final String message = "Action not found";
    public ActionNotFoundException() {
        super(message);
    }
}
