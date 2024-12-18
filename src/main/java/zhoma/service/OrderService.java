package zhoma.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import zhoma.dto.OrderItemDto;
import zhoma.dto.OrderResponseDto;
import zhoma.exceptions.OrderNotFoundException;
import zhoma.models.Order;
import zhoma.models.OrderItem;
import zhoma.models.User;
import zhoma.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class OrderService {
    private final OrderRepository orderRepository;

    public List<OrderResponseDto> getMyOrders(User user) {
        List<Order> orders = orderRepository.findByBuyer(user);

        return orders.stream()
                .map(this::convertToOrderResponseDto)
                .collect(Collectors.toList());
    }
    public List<OrderResponseDto> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return orders.stream()
                .map(this::convertToOrderResponseDto)
                .collect(Collectors.toList());
    }
    public OrderResponseDto getOrderDetails(Long orderId, User user) throws OrderNotFoundException {
        Order order = orderRepository.findByIdAndBuyer(orderId, user)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        return convertToOrderResponseDto(order);
    }




    private OrderResponseDto convertToOrderResponseDto(Order order) {
        List<OrderItemDto> items = order.getItems().stream()
                .map(this::convertToOrderItemDto)
                .collect(Collectors.toList());

        return new OrderResponseDto(
                order.getId(),
                order.getBuyer().getId(),
                order.getStatus(),
                order.getOrderDate().toString(),
                items,
                order.calculateTotalPrice()
        );
    }
    private OrderItemDto convertToOrderItemDto(OrderItem orderItem) {
        return new OrderItemDto(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getSubtotal(),
                orderItem.getProduct().getBrandEntity().getName(),
                orderItem.getProduct().getImages().isEmpty() ? "" : orderItem.getProduct().getImages().get(0).getImageUrl() // Get product image if exists
        );
    }

}
