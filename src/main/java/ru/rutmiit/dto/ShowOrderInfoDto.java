package ru.rutmiit.dto;

import ru.rutmiit.models.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ShowOrderInfoDto {
    private Long id;
    private String userName;
    private BigDecimal total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Integer itemCount;
    private String address;  // ДОБАВЬТЕ ЭТО ПОЛЕ!

    public ShowOrderInfoDto() {}

    public ShowOrderInfoDto(Long id, BigDecimal total, OrderStatus status, LocalDateTime createdAt, String address) {
        this.id = id;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
        this.address = address;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public String getAddress() { return address; }  // ДОБАВЬТЕ
    public void setAddress(String address) { this.address = address; }  // ДОБАВЬТЕ
}