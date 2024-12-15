package zhoma.responses;


import lombok.AllArgsConstructor;
import lombok.Data;
import zhoma.dto.SellerRequestForUserDto;

import java.util.List;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String username;
    private String email;
    private String role;
    private List<SellerRequestForUserDto> sellerRequests;
}