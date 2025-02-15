package zhoma.dto;


import lombok.Data;

public record BasketItemDto (
    // record
     Long productId,
     String productName,
     int quantity,
     double price,
     double totalPrice,
     String brand,
     String productImage

){

}