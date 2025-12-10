package ru.rutmiit.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.AddProductDto;
import ru.rutmiit.dto.ShowDetailedProductInfoDto;
import ru.rutmiit.dto.ShowProductInfoDto;
import ru.rutmiit.models.exceptions.ProductNotFoundException;
import ru.rutmiit.services.CartService;
import ru.rutmiit.services.CategoryService;
import ru.rutmiit.services.ProductService;

import jakarta.validation.Valid;

@Slf4j
@Controller
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final CartService cartService;

    @Autowired
    public ProductController(ProductService productService,
                             CategoryService categoryService,
                             CartService cartService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.cartService = cartService;
    }

    @ModelAttribute("addProductDto")
    public AddProductDto initProduct() {
        return new AddProductDto();
    }

    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        log.debug("Отображение формы добавления товара");
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        return "product-add";
    }

    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute("addProductDto") AddProductDto addProductDto,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        log.debug("Обработка POST запроса на добавление товара: {}", addProductDto.getName());

        if (bindingResult.hasErrors()) {
            log.warn("Ошибки валидации при добавлении товара: {}", bindingResult.getAllErrors());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("cartItemCount", cartService.getCartItemCount());
            return "product-add";
        }

        try {
            ShowProductInfoDto savedProduct = productService.addProduct(addProductDto);
            log.info("Товар успешно добавлен: {}", savedProduct.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Товар '" + savedProduct.getName() + "' успешно добавлен!");
            return "redirect:/products/all";
        } catch (Exception e) {
            log.error("Ошибка при добавлении товара", e);
            model.addAttribute("errorMessage",
                    "Ошибка при добавлении товара: " + e.getMessage());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("cartItemCount", cartService.getCartItemCount());
            return "product-add";
        }
    }


    @GetMapping("/all")
    public String showAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search,
            Model model) {

        log.debug("Отображение списка товаров: страница={}, размер={}, поиск={}, сортировка={}",
                page, size, search, sortBy);

        Sort sort = direction.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ShowProductInfoDto> productPage;

        if (search != null && !search.trim().isEmpty()) {
            productPage = productService.searchProductsPaginated(search, pageable);
            model.addAttribute("searchQuery", search);
        } else {
            productPage = productService.getAllProductsPaginated(pageable);
        }

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("cartItemCount", cartService.getCartItemCount());

        return "product-all";
    }

    @GetMapping("/product-details/{id}")
    public String productDetails(@PathVariable Long id, Model model) {
        log.debug("Запрос детальной информации о товаре: {}", id);
        ShowDetailedProductInfoDto product = productService.getProductById(id);
        model.addAttribute("product", product);
        model.addAttribute("cartItemCount", cartService.getCartItemCount());
        return "product-details";
    }
    @GetMapping("/confirm-delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public String confirmDelete(@PathVariable Long id, Model model) {
        log.debug("Подтверждение удаления товара с ID: {}", id);

        try {
            ShowDetailedProductInfoDto product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("cartItemCount", cartService.getCartItemCount());
            return "product-confirm-delete";
        } catch (ProductNotFoundException e) {
            log.warn("Товар с ID {} не найден", id);
            return "redirect:/products/all";
        }
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")// Изменяем с GET на POST для безопасности
    public String deleteProduct(@PathVariable("id") Long id,
                                RedirectAttributes redirectAttributes) {
        log.debug("Запрос на удаление товара с ID: {}", id);

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Товар успешно удален!");
        } catch (ProductNotFoundException e) {
            log.error("Товар не найден: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Товар не найден!");
        } catch (RuntimeException e) {
            log.error("Ошибка при удалении товара: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/products/all";
    }
}