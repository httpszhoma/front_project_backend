package zhoma.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import zhoma.models.SellerRequest;
import zhoma.models.User;
import zhoma.repository.SellerRequestRepository;
import zhoma.repository.UserRepository;

import java.util.List;

@Service
public class SellerRequestService {

    @Autowired
    private SellerRequestRepository sellerRequestRepository;

    @Autowired
    private UserRepository userRepository;

    public void  submitSellerRequest(String username, String description) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        SellerRequest existingRequest = sellerRequestRepository.findByUserId(user.getId())
                .stream()
                .filter(request -> request.getStatus() == SellerRequest.RequestStatus.PENDING ||request.getStatus() == SellerRequest.RequestStatus.APPROVED)
                .findFirst()
                .orElse(null);
        if (existingRequest != null) {
            throw new RuntimeException("Pending request already exists for this user.");
        }
        SellerRequest sellerRequest = new SellerRequest(user);
        sellerRequest.setDescription(description);
        sellerRequestRepository.save(sellerRequest);
    }

    public List<SellerRequest> getAllPendingRequests() {
        return sellerRequestRepository.findByStatus(SellerRequest.RequestStatus.PENDING);
    }

    public void approveRequest(Long requestId) {
        SellerRequest request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found "));

        request.setStatus(SellerRequest.RequestStatus.APPROVED);
        sellerRequestRepository.save(request);

        User user = request.getUser();

        user.setRole(zhoma.models.Role.ROLE_SELLER);
        userRepository.save(user);
    }

    public void rejectRequest(Long requestId) {
        SellerRequest request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));
        request.setStatus(SellerRequest.RequestStatus.REJECTED);
        sellerRequestRepository.save(request);
    }
}
