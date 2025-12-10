package ru.rutmiit.dto;

import java.util.List;

public class ShowDetailedCategoryInfoDto {
    private Long id;
    private String name;
    private ShowCategoryInfoDto parent;
    private List<ShowCategoryInfoDto> children;
    private List<ShowProductInfoDto> products;

    public ShowDetailedCategoryInfoDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ShowCategoryInfoDto getParent() { return parent; }
    public void setParent(ShowCategoryInfoDto parent) { this.parent = parent; }

    public List<ShowCategoryInfoDto> getChildren() { return children; }
    public void setChildren(List<ShowCategoryInfoDto> children) { this.children = children; }

    public List<ShowProductInfoDto> getProducts() { return products; }
    public void setProducts(List<ShowProductInfoDto> products) { this.products = products; }
}
