package zhoma.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SellerRequestForUserDto {
    private Long id;
    private String status; // This will store the string representation of RequestStatus
}
