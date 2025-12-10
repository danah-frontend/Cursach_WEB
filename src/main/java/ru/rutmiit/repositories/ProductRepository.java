package ru.rutmiit.repositories;

import jakarta.transaction.Transactional;
import ru.rutmiit.models.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByStockGreaterThan(Integer stock);
    boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE p.stock > 0 ORDER BY p.price ASC")
    List<Product> findTop3CheapestProducts(Pageable pageable);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
    int decreaseStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.id = :id AND NOT EXISTS (SELECT 1 FROM OrderItem oi WHERE oi.product.id = :id)")
    int safeDeleteProduct(@Param("id") Long id);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.product.id = :productId")
    Long countOrderItemsByProductId(@Param("productId") Long productId);

    @Query("SELECT COUNT(oi) FROM OrderItem oi JOIN oi.order o " +
            "WHERE oi.product.id = :productId AND o.status != 'CANCELLED'")
    Long countActiveOrderItemsByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.product.id = :productId " +
            "AND oi.order.status = 'CANCELLED'")
    @Transactional
    void deleteOrderItemsFromCancelledOrders(@Param("productId") Long productId);

    // УДАЛЕНО: метод deleteReviewsByProductId

    @Modifying
    @Query("UPDATE Product p SET p.category = null WHERE p.id = :productId")
    @Transactional
    void detachProductFromCategory(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM Product p WHERE p.id = :id")
    @Transactional
    void deleteProductById(@Param("id") Long id);
}