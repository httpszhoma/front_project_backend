package zhoma.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserSellerResponseDto {
    private Long id;
    private String role;
    private Long userId; // Matches `id` of the user for clarity
    private String email;
    private String username;
    private LocalDateTime createdAt;
}
