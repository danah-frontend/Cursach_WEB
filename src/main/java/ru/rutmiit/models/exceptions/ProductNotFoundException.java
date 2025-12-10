package ru.rutmiit.models.exceptions;


public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(Long id) {
        super("Товар с ID " + id + " не найден");
    }
}
