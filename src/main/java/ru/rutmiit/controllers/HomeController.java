package ru.rutmiit.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.rutmiit.services.CartService;
import ru.rutmiit.services.ProductService;

@Slf4j
@Controller
public class HomeController {

    private final ProductService productService;
    private final CartService cartService;

    @Autowired
    public HomeController(ProductService productService, CartService cartService) {
        this.productService = productService;
        this.cartService = cartService;
    }

    @GetMapping("/")
    public String homePage(Model model) {
        log.debug("Отображение главной страницы с инструментами");

        // Получаем топ-3 самых дешевых товаров
        var cheapestProducts = productService.getTop3CheapestProducts();
        model.addAttribute("cheapestProducts", cheapestProducts);

        // Передаем количество товаров в корзине
        model.addAttribute("cartItemCount", cartService.getCartItemCount());

        return "home_page";
    }

    @GetMapping("/about")
    public String aboutPage(Model model) {
        log.debug("Отображение страницы о магазине");
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        return "about";
    }

    @GetMapping("/contact")
    public String contactPage(Model model) {
        log.debug("Отображение страницы контактов");
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        return "contact";
    }
}