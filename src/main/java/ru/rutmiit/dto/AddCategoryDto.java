package ru.rutmiit.dto;

public class AddCategoryDto {
    private String name;
    private Long parentId;

    public AddCategoryDto() {}

    public AddCategoryDto(String name, Long parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
