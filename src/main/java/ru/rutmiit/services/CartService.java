package ru.rutmiit.services;

import ru.rutmiit.dto.CartDto;
import ru.rutmiit.dto.CartItemDto;
import ru.rutmiit.models.entities.Product;

public interface CartService {
    CartDto getCart();
    void addToCart(Long productId, Integer quantity);
    void removeFromCart(Long productId);
    void updateQuantity(Long productId, Integer quantity);
    void clearCart();
    int getCartItemCount();
}
