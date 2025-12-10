package ru.rutmiit.dto;

import java.math.BigDecimal;

public class ShowDetailedProductInfoDto {
    private Long id;
    private String name;
    private BigDecimal price;
    private ShowCategoryInfoDto category;
    private String image;
    private Integer stock;


    public ShowDetailedProductInfoDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public ShowCategoryInfoDto getCategory() { return category; }
    public void setCategory(ShowCategoryInfoDto category) { this.category = category; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}