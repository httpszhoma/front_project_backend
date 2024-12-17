package zhoma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zhoma.models.Basket;
import zhoma.models.User;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {
    Optional<Basket> findByUser(User user);
}
