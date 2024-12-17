package zhoma.dto;


import lombok.Data;

@Data
public class ProductQuantityUpdateDto {
    private Long productId;
    private int quantity;

}
