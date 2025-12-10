package ru.rutmiit.services.Impl;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.rutmiit.dto.CartDto;
import ru.rutmiit.dto.CheckoutDto;
import ru.rutmiit.dto.ShowOrderInfoDto;
import ru.rutmiit.dto.ShowDetailedOrderInfoDto;
import ru.rutmiit.models.entities.Order;
import ru.rutmiit.models.entities.User;
import ru.rutmiit.models.entities.Product;
import ru.rutmiit.models.entities.OrderItem;
import ru.rutmiit.models.enums.OrderStatus;
import ru.rutmiit.models.exceptions.OrderNotFoundException;
import ru.rutmiit.models.exceptions.UserNotFoundException;
import ru.rutmiit.models.exceptions.ProductNotFoundException;
import ru.rutmiit.repositories.OrderRepository;
import ru.rutmiit.repositories.UserRepository;
import ru.rutmiit.repositories.ProductRepository;
import ru.rutmiit.services.CartService;
import ru.rutmiit.services.OrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            CartService cartService,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "userOrders"}, allEntries = true)
    public ShowDetailedOrderInfoDto createOrder(CheckoutDto checkoutDto, String userEmail) {
        log.debug("Создание заказа для пользователя: {}", userEmail);

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        CartDto cart = cartService.getCart();

        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Корзина пуста");
        }

        for (ru.rutmiit.dto.CartItemDto cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(cartItem.getProductId()));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Недостаточно товара '" + product.getName() + "' на складе. Доступно: " + product.getStock());
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setTotal(cart.getTotal());
        order.setStatus(OrderStatus.PENDING);
        order.setAddress(checkoutDto.getAddress());
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderRepository.save(order);

        try {
            for (ru.rutmiit.dto.CartItemDto cartItem : cart.getItems()) {
                final Long productId = cartItem.getProductId();
                final Integer quantity = cartItem.getQuantity();

                int updatedRows = productRepository.decreaseStock(productId, quantity);

                if (updatedRows == 0) {
                    throw new RuntimeException("Не удалось зарезервировать товар. Возможно, остаток изменился. Пожалуйста, попробуйте снова.");
                }

                Product product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException(productId));

                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(savedOrder);
                orderItem.setProduct(product);
                orderItem.setQuantity(quantity);
                orderItem.setPrice(product.getPrice());

                savedOrder.getOrderItems().add(orderItem);
            }

            Order updatedOrder = orderRepository.save(savedOrder);
            cartService.clearCart();

            log.info("Заказ успешно создан с ID: {} для пользователя: {}", updatedOrder.getId(), userEmail);

            return modelMapper.map(updatedOrder, ShowDetailedOrderInfoDto.class);

        } catch (Exception e) {
            orderRepository.delete(savedOrder);
            throw e;
        }
    }

    @Override
    @Cacheable(value = "orders", key = "'all'")
    public List<ShowOrderInfoDto> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(order -> {
                    ShowOrderInfoDto dto = modelMapper.map(order, ShowOrderInfoDto.class);
                    dto.setUserName(order.getUser().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "order", key = "#id")
    public ShowDetailedOrderInfoDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return modelMapper.map(order, ShowDetailedOrderInfoDto.class);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "order", "userOrders"}, allEntries = true)
    public ShowOrderInfoDto updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);

        ShowOrderInfoDto dto = modelMapper.map(updatedOrder, ShowOrderInfoDto.class);
        dto.setUserName(updatedOrder.getUser().getName());
        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(value = {"orders", "order", "userOrders"}, allEntries = true)
    public void cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        for (OrderItem orderItem : order.getOrderItems()) {
            Product product = orderItem.getProduct();
            Product productToUpdate = productRepository.findById(product.getId())
                    .orElseThrow(() -> new ProductNotFoundException(product.getId()));

            productToUpdate.setStock(productToUpdate.getStock() + orderItem.getQuantity());
            productRepository.saveAndFlush(productToUpdate);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    @Override
    @Cacheable(value = "userOrders", key = "#userEmail")
    public List<ShowOrderInfoDto> getOrdersByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));

        return orderRepository.findByUserId(user.getId())
                .stream()
                .map(order -> {
                    ShowOrderInfoDto dto = modelMapper.map(order, ShowOrderInfoDto.class);
                    dto.setUserName(order.getUser().getName());
                    dto.setAddress(order.getAddress());
                    dto.setItemCount(order.getOrderItems().stream()
                            .mapToInt(OrderItem::getQuantity)
                            .sum());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "orders", key = "'status_' + #status.name()")
    public List<ShowOrderInfoDto> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status.name())
                .stream()
                .map(order -> {
                    ShowOrderInfoDto dto = modelMapper.map(order, ShowOrderInfoDto.class);
                    dto.setUserName(order.getUser().getName());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}