package ru.rutmiit.services;

import ru.rutmiit.dto.AddProductDto;
import ru.rutmiit.dto.ShowDetailedProductInfoDto;
import ru.rutmiit.dto.ShowProductInfoDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    List<ShowProductInfoDto> getAllProducts();
    ShowDetailedProductInfoDto getProductById(Long id);
    ShowProductInfoDto addProduct(AddProductDto addProductDto);
    ShowProductInfoDto updateProduct(Long id, AddProductDto addProductDto);
    void deleteProduct(Long id); // РЕАЛИЗУЕМ ЭТОТ МЕТОД
    List<ShowProductInfoDto> getProductsByCategory(Long categoryId);
    List<ShowProductInfoDto> searchProducts(String name);
    List<ShowProductInfoDto> getAvailableProducts();
    List<ShowProductInfoDto> getTop3CheapestProducts();
    // Методы для пагинации
    Page<ShowProductInfoDto> getAllProductsPaginated(Pageable pageable);
    Page<ShowProductInfoDto> searchProductsPaginated(String name, Pageable pageable);
    Page<ShowProductInfoDto> getProductsByCategoryPaginated(Long categoryId, Pageable pageable);
}