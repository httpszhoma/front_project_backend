package zhoma.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserSellerResponseDto {
    private Long userId;
    private String email;
    private String username;
    private LocalDateTime createdAt;
    private String description;
}
