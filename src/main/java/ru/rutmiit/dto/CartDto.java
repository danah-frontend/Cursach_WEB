package ru.rutmiit.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartDto {
    private List<CartItemDto> items = new ArrayList<>();
    private BigDecimal total = BigDecimal.ZERO;
    private int totalItems = 0;

    public CartDto() {}

    public void addItem(CartItemDto item) {
        // Проверяем, есть ли уже такой товар в корзине
        for (CartItemDto existingItem : items) {
            if (existingItem.getProductId().equals(item.getProductId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                calculateTotals();
                return;
            }
        }
        items.add(item);
        calculateTotals();
    }

    public void removeItem(Long productId) {
        items.removeIf(item -> item.getProductId().equals(productId));
        calculateTotals();
    }

    public void updateQuantity(Long productId, Integer quantity) {
        for (CartItemDto item : items) {
            if (item.getProductId().equals(productId)) {
                if (quantity <= 0) {
                    removeItem(productId);
                } else {
                    item.setQuantity(quantity);
                }
                calculateTotals();
                return;
            }
        }
    }

    public void clear() {
        items.clear();
        total = BigDecimal.ZERO;
        totalItems = 0;
    }

    private void calculateTotals() {
        total = BigDecimal.ZERO;
        totalItems = 0;

        for (CartItemDto item : items) {
            total = total.add(item.getSubtotal());
            totalItems += item.getQuantity();
        }
    }

    // Геттеры
    public List<CartItemDto> getItems() { return items; }
    public BigDecimal getTotal() { return total; }
    public int getTotalItems() { return totalItems; }
}
