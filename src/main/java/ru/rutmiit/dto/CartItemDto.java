package ru.rutmiit.dto;

import java.math.BigDecimal;

public class CartItemDto {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
    private String image;

    public CartItemDto() {}

    public CartItemDto(Long productId, String productName, BigDecimal price, Integer quantity, String image) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    // Геттеры и сеттеры
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        this.subtotal = this.price.multiply(BigDecimal.valueOf(quantity));
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}
