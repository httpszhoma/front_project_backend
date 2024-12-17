package zhoma.dto;

import lombok.Data;

import java.util.List;

@Data

public class BasketResponseDto {
    private Long userId;
    private List<BasketItemDto> items;
    private double totalPrice;

}