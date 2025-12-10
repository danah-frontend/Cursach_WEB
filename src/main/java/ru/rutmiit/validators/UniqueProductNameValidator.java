package ru.rutmiit.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import ru.rutmiit.repositories.ProductRepository;
import ru.rutmiit.annotations.UniqueProductName;

public class UniqueProductNameValidator implements ConstraintValidator<UniqueProductName, String> {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public void initialize(UniqueProductName constraintAnnotation) {
    }

    @Override
    public boolean isValid(String productName, ConstraintValidatorContext context) {
        if (productName == null || productName.trim().isEmpty()) {
            return true; // Пустые значения проверяются другими аннотациями
        }
        return !productRepository.existsByName(productName);
    }
}
