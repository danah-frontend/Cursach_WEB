package ru.rutmiit.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.AddProductDto;
import ru.rutmiit.dto.ShowDetailedProductInfoDto;
import ru.rutmiit.dto.ShowProductInfoDto;
import ru.rutmiit.models.entities.Category;
import ru.rutmiit.models.entities.Product;
import ru.rutmiit.models.exceptions.ProductNotFoundException;
import ru.rutmiit.repositories.CategoryRepository;
import ru.rutmiit.repositories.ProductRepository;
import ru.rutmiit.services.ProductService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Cacheable(value = "products", key = "'top3Cheapest'")
    public List<ShowProductInfoDto> getTop3CheapestProducts() {
        log.debug("Получение топ-3 самых дешевых товаров через Query");

        Pageable pageable = PageRequest.of(0, 3);
        List<Product> cheapestProducts = productRepository.findTop3CheapestProducts(pageable);

        log.info("Найдено {} самых дешевых товаров через Query", cheapestProducts.size());

        return cheapestProducts.stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ShowProductInfoDto addProduct(AddProductDto addProductDto) {
        log.debug("=== НАЧАЛО добавления товара: {} ===", addProductDto.getName());

        try {
            Category category = categoryRepository.findById(addProductDto.getCategoryId())
                    .orElseThrow(() -> {
                        log.error("Категория с ID {} не найдена", addProductDto.getCategoryId());
                        return new IllegalArgumentException("Категория не найдена");
                    });

            Product product = new Product();
            product.setName(addProductDto.getName());
            product.setPrice(addProductDto.getPrice());
            product.setCategory(category);
            product.setImage(addProductDto.getImage());
            product.setStock(addProductDto.getStock());

            Product savedProduct = productRepository.save(product);
            log.debug("Продукт сохранен с ID: {}", savedProduct.getId());

            ShowProductInfoDto result = new ShowProductInfoDto();
            result.setId(savedProduct.getId());
            result.setName(savedProduct.getName());
            result.setPrice(savedProduct.getPrice());
            result.setCategoryName(savedProduct.getCategory().getName());
            result.setImage(savedProduct.getImage());
            result.setStock(savedProduct.getStock());

            log.info("=== УСПЕШНО добавлен товар: {} с ID: {} ===", result.getName(), result.getId());
            return result;

        } catch (Exception e) {
            log.error("=== ОШИБКА при добавлении товара: {} ===", addProductDto.getName(), e);
            throw new RuntimeException("Ошибка при добавлении товара: " + e.getMessage(), e);
        }
    }

    @Override
    @Cacheable(value = "products", key = "'all'")
    public List<ShowProductInfoDto> getAllProducts() {
        log.debug("Получение списка всех товаров из базы данных");

        List<Product> products = productRepository.findAll();

        log.info("Найдено товаров в базе: {}", products.size());

        return products.stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "product", key = "#id")
    public ShowDetailedProductInfoDto getProductById(Long id) {
        log.debug("Получение товара по ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товар с ID " + id + " не найден"));

        ShowDetailedProductInfoDto dto = modelMapper.map(product, ShowDetailedProductInfoDto.class);

        if (product.getCategory() != null) {
            ru.rutmiit.dto.ShowCategoryInfoDto categoryDto = modelMapper.map(product.getCategory(), ru.rutmiit.dto.ShowCategoryInfoDto.class);
            dto.setCategory(categoryDto);
        }

        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public ShowProductInfoDto updateProduct(Long id, AddProductDto addProductDto) {
        log.debug("Обновление товара с ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Товар с ID " + id + " не найден"));

        product.setName(addProductDto.getName());
        product.setPrice(addProductDto.getPrice());
        product.setImage(addProductDto.getImage());
        product.setStock(addProductDto.getStock());

        if (addProductDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(addProductDto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Категория не найдена"));
            product.setCategory(category);
        }

        Product updatedProduct = productRepository.save(product);

        ShowProductInfoDto dto = modelMapper.map(updatedProduct, ShowProductInfoDto.class);
        dto.setCategoryName(updatedProduct.getCategory().getName());
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"products", "product"}, allEntries = true)
    public void deleteProduct(Long id) {
        log.debug("=== БЕЗОПАСНОЕ удаление товара с ID: {} ===", id);

        try {
            // 1. Проверяем существование товара
            if (!productRepository.existsById(id)) {
                throw new ProductNotFoundException("Товар с ID " + id + " не найден");
            }

            // 2. Проверяем наличие АКТИВНЫХ заказов (не отмененных)
            Long activeOrderCount = productRepository.countActiveOrderItemsByProductId(id);
            if (activeOrderCount > 0) {
                log.warn("Товар ID {} нельзя удалить - есть {} активных заказов", id, activeOrderCount);
                throw new RuntimeException("Невозможно удалить товар, который присутствует в активных заказах. " +
                        "Отмените заказы или дождитесь их выполнения.");
            }

            // 3. Удаляем товар из истории отмененных заказов
            log.debug("Удаление товара из истории отмененных заказов ID: {}", id);
            productRepository.deleteOrderItemsFromCancelledOrders(id);

            // Проверяем, что всё удалилось (опционально)
            Long totalOrderCount = productRepository.countOrderItemsByProductId(id);
            if (totalOrderCount > 0) {
                log.warn("После очистки истории остались заказы в других статусах. " +
                        "Товар ID {} всё ещё в заказах со статусами: PENDING, CONFIRMED, SHIPPED, DELIVERED", id);
            }


            // 5. Удаляем товар
            log.debug("Удаление товара ID: {}", id);
            productRepository.deleteById(id);

            // 6. Явно синхронизируем с БД
            productRepository.flush();

            log.info("=== УСПЕШНО удален товар с ID: {} ===", id);

        } catch (ProductNotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            log.error("=== ОШИБКА при удалении товара с ID: {} ===", id, e);
            throw e;
        } catch (Exception e) {
            log.error("=== НЕОЖИДАННАЯ ОШИБКА при удалении товара с ID: {} ===", id, e);
            throw new RuntimeException("Неожиданная ошибка при удалении товара: " + e.getMessage(), e);
        }
    }

    @Override
    @Cacheable(value = "products", key = "'category_' + #categoryId")
    public List<ShowProductInfoDto> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId)
                .stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ShowProductInfoDto> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "products", key = "'available'")
    public List<ShowProductInfoDto> getAvailableProducts() {
        return productRepository.findByStockGreaterThan(0)
                .stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShowProductInfoDto> getAllProductsPaginated(Pageable pageable) {
        log.debug("Получение пагинированного списка товаров: страница {}", pageable.getPageNumber());

        Page<Product> productPage = productRepository.findAll(pageable);

        List<ShowProductInfoDto> productDtos = productPage.getContent()
                .stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(productDtos, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ShowProductInfoDto> searchProductsPaginated(String name, Pageable pageable) {
        log.debug("Поиск товаров по запросу: '{}'", name);

        Page<Product> productPage = productRepository.findByNameContainingIgnoreCase(name, pageable);

        List<ShowProductInfoDto> productDtos = productPage.getContent()
                .stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(productDtos, pageable, productPage.getTotalElements());
    }

    @Override
    public Page<ShowProductInfoDto> getProductsByCategoryPaginated(Long categoryId, Pageable pageable) {
        log.debug("Получение товаров категории: {}", categoryId);

        Page<Product> productPage = productRepository.findByCategoryId(categoryId, pageable);

        List<ShowProductInfoDto> productDtos = productPage.getContent()
                .stream()
                .map(product -> {
                    ShowProductInfoDto dto = modelMapper.map(product, ShowProductInfoDto.class);
                    dto.setCategoryName(product.getCategory().getName());
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(productDtos, pageable, productPage.getTotalElements());
    }
}