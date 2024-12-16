package zhoma.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import zhoma.models.Brand;
import zhoma.models.Category;
import zhoma.models.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByBrandEntity(Brand brand, Pageable pageable);
    Page<Product> findByCategoryEntity(Category category, Pageable pageable);

}
