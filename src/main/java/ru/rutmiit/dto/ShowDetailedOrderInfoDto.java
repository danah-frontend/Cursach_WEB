package ru.rutmiit.dto;

import ru.rutmiit.models.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ShowDetailedOrderInfoDto {
    private Long id;
    private ShowUserInfoDto user;
    private BigDecimal total;
    private OrderStatus status;
    private String address;
    private LocalDateTime createdAt;
    private List<ShowOrderItemInfoDto> orderItems;

    public ShowDetailedOrderInfoDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ShowUserInfoDto getUser() { return user; }
    public void setUser(ShowUserInfoDto user) { this.user = user; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<ShowOrderItemInfoDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<ShowOrderItemInfoDto> orderItems) { this.orderItems = orderItems; }
}