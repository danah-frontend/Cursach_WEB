package ru.rutmiit.services;

import ru.rutmiit.dto.AddCategoryDto;
import ru.rutmiit.dto.ShowCategoryInfoDto;
import ru.rutmiit.dto.ShowDetailedCategoryInfoDto;
import java.util.List;

public interface CategoryService {
    List<ShowCategoryInfoDto> getAllCategories();
    ShowDetailedCategoryInfoDto getCategoryById(Long id);
    ShowCategoryInfoDto addCategory(AddCategoryDto addCategoryDto);
    ShowCategoryInfoDto updateCategory(Long id, AddCategoryDto addCategoryDto);
    void deleteCategory(Long id);
    List<ShowCategoryInfoDto> getRootCategories();
    List<ShowCategoryInfoDto> getSubcategories(Long parentId);
}
