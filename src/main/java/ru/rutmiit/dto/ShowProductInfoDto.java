package ru.rutmiit.dto;

import java.math.BigDecimal;

public class ShowProductInfoDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private String categoryName;
    private String image;
    private Integer stock;

    public ShowProductInfoDto() {}

    public ShowProductInfoDto(Long id, String name, BigDecimal price, String categoryName, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.categoryName = categoryName;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}