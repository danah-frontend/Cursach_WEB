package ru.rutmiit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.rutmiit.models.entities.*;
import ru.rutmiit.models.enums.UserRoles;
import ru.rutmiit.repositories.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class Init implements CommandLineRunner {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultPassword;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Init(UserRepository userRepository,
                UserRoleRepository userRoleRepository,
                PasswordEncoder passwordEncoder,
                CategoryRepository categoryRepository,
                ProductRepository productRepository,
                @Value("${app.default.password}") String defaultPassword) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.defaultPassword = defaultPassword;
    }

    @Override
    public void run(String... args) {
        log.info("Запуск инициализации начальных данных");
        initRoles();
        initUsers();
        initCategories();
        initProducts();
        log.info("Инициализация начальных данных завершена");
    }

    private void initRoles() {
        if (userRoleRepository.count() == 0) {
            log.info("Создание базовых ролей...");
            userRoleRepository.saveAll(List.of(
                    new Role(UserRoles.ADMIN),
                    new Role(UserRoles.MODERATOR),
                    new Role(UserRoles.USER)
            ));
            log.info("Роли созданы: ADMIN, MODERATOR, USER");
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            log.info("Создание пользователей по умолчанию...");
            initAdmin();
            initModerator();
            initNormalUser();
            log.info("Пользователи по умолчанию созданы");
        }
    }

    private void initCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Создание категорий...");

            Category electronics = new Category("Электроинструменты");
            Category handTools = new Category("Ручные инструменты");
            Category measuring = new Category("Измерительные инструменты");
            Category consumables = new Category("Расходные материалы");

            categoryRepository.saveAll(List.of(electronics, handTools, measuring, consumables));
            log.info("Создано {} основных категорий", categoryRepository.count());
        }
    }

    private void initProducts() {
        if (productRepository.count() == 0) {
            log.info("Создание тестовых товаров...");

            Category electronics = categoryRepository.findByName("Электроинструменты")
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            Category handTools = categoryRepository.findByName("Ручные инструменты")
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));
            Category consumables = categoryRepository.findByName("Расходные материалы")
                    .orElseThrow(() -> new RuntimeException("Категория не найдена"));

            List<Product> products = List.of(
                    new Product("Дрель электрическая Makita", new BigDecimal("4500.00"), electronics, 15),
                    new Product("Перфоратор Bosch", new BigDecimal("7800.00"), electronics, 8),
                    new Product("Шуруповерт DeWalt", new BigDecimal("5200.00"), electronics, 12),
                    new Product("Молоток слесарный", new BigDecimal("800.00"), handTools, 25),
                    new Product("Набор отверток", new BigDecimal("1200.00"), handTools, 30),
                    new Product("Плоскогубцы", new BigDecimal("650.00"), handTools, 20),
                    new Product("Набор гаечных ключей", new BigDecimal("1800.00"), handTools, 18),
                    new Product("Сверла по металлу (набор)", new BigDecimal("950.00"), consumables, 50),
                    new Product("Диски отрезные по металлу", new BigDecimal("320.00"), consumables, 100),
                    new Product("Перчатки рабочие", new BigDecimal("250.00"), consumables, 200)
            );

            productRepository.saveAll(products);
            log.info("Создано {} тестовых товаров", productRepository.count());
        }
    }

    private void initAdmin() {
        var adminRole = userRoleRepository.findByName(UserRoles.ADMIN).orElseThrow();
        var moderatorRole = userRoleRepository.findByName(UserRoles.MODERATOR).orElseThrow();
        var userRole = userRoleRepository.findByName(UserRoles.USER).orElseThrow();

        var adminUser = new User("admin@example.com", passwordEncoder.encode(defaultPassword), "Администратор", "893454323");
        adminUser.setRoles(List.of(adminRole, moderatorRole, userRole));
        userRepository.save(adminUser);
        log.info("Создан администратор: admin@example.com");
    }

    private void initModerator() {
        var moderatorRole = userRoleRepository.findByName(UserRoles.MODERATOR).orElseThrow();
        var userRole = userRoleRepository.findByName(UserRoles.USER).orElseThrow();

        var moderatorUser = new User("moderator@example.com", passwordEncoder.encode(defaultPassword), "Модератор", "898765634");
        moderatorUser.setRoles(List.of(moderatorRole, userRole));
        userRepository.save(moderatorUser);
        log.info("Создан модератор: moderator@example.com");
    }

    private void initNormalUser() {
        var userRole = userRoleRepository.findByName(UserRoles.USER).orElseThrow();

        var normalUser = new User("user@example.com", passwordEncoder.encode(defaultPassword), "Пользователь", "898765364");
        normalUser.setRoles(List.of(userRole));
        userRepository.save(normalUser);
        log.info("Создан обычный пользователь: user@example.com");
    }
}