package zhoma.dto;


import lombok.Data;

@Data
public class BasketItemDto {
    private Long productId;
    private String productName;
    private int quantity;
    private double price;
    private double totalPrice;
    private String brand;
    private String productImage;

}