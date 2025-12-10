package ru.rutmiit.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.rutmiit.dto.ShowOrderInfoDto;
import ru.rutmiit.dto.ShowDetailedOrderInfoDto;
import ru.rutmiit.models.enums.OrderStatus;
import ru.rutmiit.services.OrderService;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String viewOrders(Model model) {
        log.debug("Отображение списка заказов");

        // Получаем email из SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        if (userEmail.equals("anonymousUser")) {
            return "redirect:/users/login";
        }

        List<ShowOrderInfoDto> orders = orderService.getOrdersByUser(userEmail);
        model.addAttribute("orders", orders);

        return "orders-list";
    }

    @GetMapping("/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        log.debug("Просмотр деталей заказа: {}", id);

        try {
            ShowDetailedOrderInfoDto order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "order-details";
        } catch (Exception e) {
            log.error("Ошибка при получении заказа", e);
            return "redirect:/orders";
        }
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.debug("Отмена заказа: {}", id);

        try {
            orderService.cancelOrder(id);
            redirectAttributes.addFlashAttribute("successMessage", "Заказ успешно отменен");
        } catch (Exception e) {
            log.error("Ошибка при отмене заказа", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при отмене заказа");
        }

        return "redirect:/orders";
    }

    @GetMapping("/admin")
    public String viewAllOrders(Model model) {
        log.debug("Отображение всех заказов (админ)");

        List<ShowOrderInfoDto> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        return "admin-orders";
    }

    @PostMapping("/admin/update-status/{id}")
    public String updateOrderStatus(@PathVariable Long id,
                                    @RequestParam OrderStatus status,
                                    RedirectAttributes redirectAttributes) {
        log.debug("Обновление статуса заказа {} на {}", id, status);

        try {
            orderService.updateOrderStatus(id, status);
            redirectAttributes.addFlashAttribute("successMessage", "Статус заказа обновлен");
        } catch (Exception e) {
            log.error("Ошибка при обновлении статуса заказа", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении статуса");
        }

        return "redirect:/orders/admin";
    }
}
