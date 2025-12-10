package ru.rutmiit.services.Impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddCategoryDto;
import ru.rutmiit.dto.ShowCategoryInfoDto;
import ru.rutmiit.dto.ShowDetailedCategoryInfoDto;
import ru.rutmiit.models.exceptions.CategoryNotFoundException;
import ru.rutmiit.models.entities.Category;
import ru.rutmiit.repositories.CategoryRepository;
import ru.rutmiit.services.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<ShowCategoryInfoDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(category -> {
                    ShowCategoryInfoDto dto = modelMapper.map(category, ShowCategoryInfoDto.class);

                    if (category.getParent() != null) {
                        dto.setParentId(category.getParent().getId());
                        dto.setParentName(category.getParent().getName());
                    }

                    dto.setProductCount(category.getProducts().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "category", key = "#id")
    public ShowDetailedCategoryInfoDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        ShowDetailedCategoryInfoDto dto = modelMapper.map(category, ShowDetailedCategoryInfoDto.class);

        if (category.getParent() != null) {
            ShowCategoryInfoDto parentDto = modelMapper.map(category.getParent(), ShowCategoryInfoDto.class);
            dto.setParent(parentDto);
        }

        if (category.getChildren() != null) {
            List<ShowCategoryInfoDto> childrenDtos = category.getChildren()
                    .stream()
                    .map(child -> modelMapper.map(child, ShowCategoryInfoDto.class))
                    .collect(Collectors.toList());
            dto.setChildren(childrenDtos);
        }

        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public ShowCategoryInfoDto addCategory(AddCategoryDto addCategoryDto) {
        Category category = modelMapper.map(addCategoryDto, Category.class);

        if (addCategoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(addCategoryDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(addCategoryDto.getParentId()));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);

        ShowCategoryInfoDto dto = modelMapper.map(savedCategory, ShowCategoryInfoDto.class);
        if (savedCategory.getParent() != null) {
            dto.setParentId(savedCategory.getParent().getId());
            dto.setParentName(savedCategory.getParent().getName());
        }

        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public ShowCategoryInfoDto updateCategory(Long id, AddCategoryDto addCategoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        modelMapper.map(addCategoryDto, category);

        if (addCategoryDto.getParentId() != null) {
            Category parent = categoryRepository.findById(addCategoryDto.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(addCategoryDto.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);

        ShowCategoryInfoDto dto = modelMapper.map(updatedCategory, ShowCategoryInfoDto.class);
        if (updatedCategory.getParent() != null) {
            dto.setParentId(updatedCategory.getParent().getId());
            dto.setParentName(updatedCategory.getParent().getName());
        }

        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"categories", "category"}, allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getProducts().isEmpty()) {
            throw new RuntimeException("Cannot delete category with existing products");
        }

        categoryRepository.delete(category);
    }

    @Override
    @Cacheable(value = "categories", key = "'root'")
    public List<ShowCategoryInfoDto> getRootCategories() {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(category -> {
                    ShowCategoryInfoDto dto = modelMapper.map(category, ShowCategoryInfoDto.class);
                    dto.setProductCount(category.getProducts().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "categories", key = "'subcategories_' + #parentId")
    public List<ShowCategoryInfoDto> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId)
                .stream()
                .map(category -> {
                    ShowCategoryInfoDto dto = modelMapper.map(category, ShowCategoryInfoDto.class);
                    dto.setProductCount(category.getProducts().size());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}