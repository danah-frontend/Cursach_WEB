package ru.rutmiit.services;

import ru.rutmiit.dto.CheckoutDto;
import ru.rutmiit.dto.ShowOrderInfoDto;
import ru.rutmiit.dto.ShowDetailedOrderInfoDto;
import ru.rutmiit.models.enums.OrderStatus;

import java.util.List;

public interface OrderService {
    List<ShowOrderInfoDto> getAllOrders();
    ShowDetailedOrderInfoDto getOrderById(Long id);
    ShowDetailedOrderInfoDto createOrder(CheckoutDto checkoutDto, String userEmail);
    ShowOrderInfoDto updateOrderStatus(Long id, OrderStatus status);
    void cancelOrder(Long id);
    List<ShowOrderInfoDto> getOrdersByUser(String userEmail);
    List<ShowOrderInfoDto> getOrdersByStatus(OrderStatus status);
}