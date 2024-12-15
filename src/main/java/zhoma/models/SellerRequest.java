package zhoma.models;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_requests")
@Data
public class SellerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDateTime createdAt;

    private String description;
    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    public SellerRequest() {}

    public SellerRequest(User user) {
        this.user = user;
        this.createdAt = LocalDateTime.now();
    }
}
