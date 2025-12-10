package ru.rutmiit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class AddProductDto {

    @NotEmpty(message = "Название товара не может быть пустым")
    @Size(min = 2, max = 100, message = "Название товара должно быть от 2 до 100 символов")
    private String name;

    @NotNull(message = "Цена не может быть пустой")
    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    @NotNull(message = "Категория должна быть указана")
    private Long categoryId; // ModelMapper проигнорирует это поле при маппинге в Product

    private String image;

    @NotNull(message = "Количество на складе должно быть указано")
    @Positive(message = "Количество на складе должно быть положительным")
    private Integer stock;

    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}