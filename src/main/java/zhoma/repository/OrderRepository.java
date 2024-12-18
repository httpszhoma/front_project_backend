package zhoma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zhoma.models.Order;
import zhoma.models.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByBuyer(User buyer);
    List<Order> findByStatus(String status);
    Optional<Order> findByIdAndBuyer(Long id, User buyer);


}
