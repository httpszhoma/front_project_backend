package zhoma.dto;

import lombok.Data;

@Data
public class OrderItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    private double totalPrice;
    private String brand;
    private String productImage;

    public OrderItemDto(Long productId, String productName, int quantity, double price, double totalPrice, String brand, String productImage) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
        this.brand = brand;
        this.productImage = productImage;
    }
}
