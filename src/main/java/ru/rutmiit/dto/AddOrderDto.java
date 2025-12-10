package ru.rutmiit.dto;

import ru.rutmiit.models.enums.OrderStatus;

import java.util.List;

public class AddOrderDto {
    private Long userId;
    private String address;
    private OrderStatus status;
    private List<OrderItemDto> orderItems;

    public AddOrderDto() {}

    public AddOrderDto(Long userId, String address, OrderStatus status, List<OrderItemDto> orderItems) {
        this.userId = userId;
        this.address = address;
        this.status = status;
        this.orderItems = orderItems;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderItemDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDto> orderItems) { this.orderItems = orderItems; }

    public static class OrderItemDto {
        private Long productId;
        private Integer quantity;

        public OrderItemDto() {}

        public OrderItemDto(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}