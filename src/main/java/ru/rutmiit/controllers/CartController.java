package ru.rutmiit.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.CartDto;
import ru.rutmiit.dto.CheckoutDto;
import ru.rutmiit.services.CartService;
import ru.rutmiit.services.OrderService;
import ru.rutmiit.services.ProductService;

@Slf4j
@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;

    @Autowired
    public CartController(CartService cartService, ProductService productService, OrderService orderService) {
        this.cartService = cartService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping
    public String viewCart(Model model) {
        log.debug("Отображение корзины");
        model.addAttribute("cart", cartService.getCart());
        return "cart-view";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            RedirectAttributes redirectAttributes) {
        log.debug("Добавление товара {} в корзину, количество: {}", productId, quantity);

        try {
            cartService.addToCart(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Товар добавлен в корзину");
        } catch (Exception e) {
            log.error("Ошибка при добавлении в корзину", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/products/product-details/" + productId;
    }

    @PostMapping("/add-from-list/{productId}")
    public String addToCartFromList(@PathVariable Long productId,
                                    @RequestParam(defaultValue = "1") Integer quantity,
                                    RedirectAttributes redirectAttributes) {
        log.debug("Добавление товара {} в корзину со списка товаров", productId);

        try {
            cartService.addToCart(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Товар добавлен в корзину");
        } catch (Exception e) {
            log.error("Ошибка при добавлении в корзину", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/products/all";
    }

    @PostMapping("/update/{productId}")
    public String updateQuantity(@PathVariable Long productId,
                                 @RequestParam Integer quantity,
                                 RedirectAttributes redirectAttributes) {
        log.debug("Обновление количества товара {} в корзине: {}", productId, quantity);

        try {
            cartService.updateQuantity(productId, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Количество обновлено");
        } catch (Exception e) {
            log.error("Ошибка при обновлении корзины", e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId,
                                 RedirectAttributes redirectAttributes) {
        log.debug("Удаление товара {} из корзины", productId);

        cartService.removeFromCart(productId);
        redirectAttributes.addFlashAttribute("successMessage", "Товар удален из корзины");

        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(RedirectAttributes redirectAttributes) {
        log.debug("Очистка корзины");

        cartService.clearCart();
        redirectAttributes.addFlashAttribute("successMessage", "Корзина очищена");

        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        log.debug("Отображение формы оформления заказа");

        CartDto cart = cartService.getCart();
        if (cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cart", cart);
        model.addAttribute("checkoutDto", new CheckoutDto());

        return "checkout";
    }

    @PostMapping("/checkout")
    public String processCheckout(@ModelAttribute CheckoutDto checkoutDto,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        log.debug("Обработка оформления заказа");

        try {
            // Получаем текущего пользователя из SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userEmail = authentication.getName();

            if (userEmail.equals("anonymousUser")) {
                return "redirect:/users/login";
            }

            orderService.createOrder(checkoutDto, userEmail);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Заказ успешно оформлен! Номер заказа будет отправлен на вашу почту.");

            return "redirect:/orders";
        } catch (Exception e) {
            log.error("Ошибка при оформлении заказа", e);
            model.addAttribute("errorMessage", "Ошибка при оформлении заказа: " + e.getMessage());
            model.addAttribute("cart", cartService.getCart());
            return "checkout";
        }
    }
}
