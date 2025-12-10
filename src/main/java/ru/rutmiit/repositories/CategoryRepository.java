package ru.rutmiit.repositories;

import ru.rutmiit.models.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    List<Category> findByParentId(Long parentId);
    boolean existsByName(String name);
    Optional<Category> findByName(String name);
}
