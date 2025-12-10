package ru.rutmiit.dto;

public class ShowCategoryInfoDto {
    private Long id;
    private String name;
    private Long parentId;
    private String parentName;
    private Integer productCount;

    public ShowCategoryInfoDto() {}

    public ShowCategoryInfoDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public String getParentName() { return parentName; }
    public void setParentName(String parentName) { this.parentName = parentName; }

    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }
}