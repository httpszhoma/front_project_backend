package zhoma.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhoma.dto.OrderItemDto;
import zhoma.dto.OrderResponseDto;
import zhoma.models.*;
import zhoma.repository.BasketRepository;
import zhoma.repository.OrderRepository;
import zhoma.repository.ProductRepository;
import zhoma.service.BasketService;
import zhoma.service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BasketRepository basketRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;
    private final BasketService basketService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }


    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {
        User currentUser = getCurrentUser();

        // Fetch orders for the current user
        List<Order> orders = orderRepository.findByBuyer(currentUser);

        // Map orders to OrderResponseDto
        List<OrderResponseDto> responseDtos = orders.stream()
                .map(this::mapToOrderResponseDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDtos);
    }

    // Helper method to map Order to OrderResponseDto
    private OrderResponseDto mapToOrderResponseDto(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<OrderItemDto> items = order.getItems().stream()
                .map(item -> new OrderItemDto(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubtotal(),
                        item.getProduct().getBrandEntity().getName(),
                        item.getProduct().getImages().isEmpty() ? null : item.getProduct().getImages().get(0).getImageUrl()
                ))
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getBuyer().getId(),
                order.getStatus(),
                order.getOrderDate().format(formatter),
                items,
                order.calculateTotalPrice()
        );
    }
    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(){
        Basket basket = basketRepository.findByUser(getCurrentUser()).get();
        if (basket == null || basket.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Basket is empty.");
        }


        Order order = new Order();
        order.setBuyer(getCurrentUser());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("PENDING");

        for (BasketItem basketItem : basket.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(basketItem.getProduct());
            orderItem.setQuantity(basketItem.getQuantity());
            orderItem.setPrice(basketItem.getProduct().getPrice());
            orderItem.setSeller(basketItem.getProduct().getCreator());
            order.addItem(orderItem);
        }

        basketService.clearBasket(userService.getAuthenticatedUser());

        return ResponseEntity.ok("Order placed successfully.");

    }




}
