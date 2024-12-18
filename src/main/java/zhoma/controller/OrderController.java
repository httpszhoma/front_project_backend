package zhoma.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhoma.models.*;
import zhoma.repository.BasketRepository;
import zhoma.repository.OrderRepository;
import zhoma.repository.ProductRepository;
import zhoma.service.UserService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final BasketRepository basketRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username);
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

        orderRepository.save(order);

        basket.getItems().clear();
        basketRepository.save(basket);

        return ResponseEntity.ok("Order placed successfully.");

    }




}
