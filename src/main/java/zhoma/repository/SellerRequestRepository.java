package zhoma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhoma.models.SellerRequest;

import java.util.List;

public interface SellerRequestRepository extends JpaRepository<SellerRequest, Long> {
    List<SellerRequest> findByUserId(Long userId);
    List<SellerRequest> findByStatus(SellerRequest.RequestStatus status);

}
