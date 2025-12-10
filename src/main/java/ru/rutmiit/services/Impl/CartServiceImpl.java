package ru.rutmiit.services.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import ru.rutmiit.dto.CartDto;
import ru.rutmiit.dto.CartItemDto;
import ru.rutmiit.models.entities.Product;
import ru.rutmiit.repositories.ProductRepository;
import ru.rutmiit.services.CartService;

@Service("cartService")
@SessionScope
public class CartServiceImpl implements CartService {

    private CartDto cart = new CartDto();

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CartDto getCart() {
        return cart;
    }

    @Override
    public void addToCart(Long productId, Integer quantity) {
        if (quantity <= 0) {
            return;
        }

        // Получаем актуальный продукт из базы
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        // Проверяем наличие на складе
        if (product.getStock() < quantity) {
            throw new RuntimeException("Недостаточно товара '" + product.getName() + "' на складе. Доступно: " + product.getStock());
        }

        // Проверяем, есть ли уже такой товар в корзине
        int alreadyInCart = 0;
        for (CartItemDto item : cart.getItems()) {
            if (item.getProductId().equals(productId)) {
                alreadyInCart = item.getQuantity();
                break;
            }
        }

        // Проверяем общее количество (в корзине + добавляемое)
        if (product.getStock() < (alreadyInCart + quantity)) {
            throw new RuntimeException("Недостаточно товара '" + product.getName() + "' на складе. Доступно: " + product.getStock() +
                    ", уже в корзине: " + alreadyInCart);
        }

        CartItemDto cartItem = new CartItemDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                quantity,
                product.getImage()
        );

        cart.addItem(cartItem);
    }

    @Override
    public void removeFromCart(Long productId) {
        cart.removeItem(productId);
    }

    @Override
    public void updateQuantity(Long productId, Integer quantity) {
        if (quantity <= 0) {
            removeFromCart(productId);
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Товар не найден"));

        if (product.getStock() < quantity) {
            throw new RuntimeException("Недостаточно товара '" + product.getName() + "' на складе. Доступно: " + product.getStock());
        }

        cart.updateQuantity(productId, quantity);
    }

    @Override
    public void clearCart() {
        cart.clear();
    }

    @Override
    public int getCartItemCount() {
        return cart.getTotalItems();
    }
}