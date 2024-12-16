package zhoma.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductRequestDto {

    private String name;
    private  String description;
    private double price;
    private Long categoryId;
    private Long brandId;
    private String status;
    private int quantity;




}
