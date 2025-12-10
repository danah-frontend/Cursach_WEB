package ru.rutmiit.models.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Long id) {
        super("Order not found with id: " + id);
    }
}
