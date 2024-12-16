package zhoma.responses;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponseDto {

    private Long id;
    private String name;
    private String description;
    private double price;
    private CategoryDto categoryEntity;
    private BrandDto brandEntity;
    private String status;
    private int quantity;
    private List<ImageDto> images;

    @Data
    public static class CategoryDto {
        private Long id;
        private String name;
    }

    @Data
    public static class BrandDto {
        private Long id;
        private String name;
    }

    @Data
    public static class ImageDto {
        private String imageUrl;
    }
}
