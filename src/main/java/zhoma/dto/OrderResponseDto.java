package zhoma.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderResponseDto {
    private Long orderId;
    private Long userId;
    private String status;
    private String orderDate;
    private List<OrderItemDto> items;
    private double totalPrice;

    public OrderResponseDto(Long orderId, Long userId, String status, String orderDate, List<OrderItemDto> items, double totalPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.status = status;
        this.orderDate = orderDate;
        this.items = items;
        this.totalPrice = totalPrice;
    }
}
