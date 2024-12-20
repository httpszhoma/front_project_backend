package zhoma.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import zhoma.models.Brand;
import zhoma.models.Category;
import zhoma.models.Product;
import zhoma.models.User;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAll(Pageable pageable);
    Page<Product> findByBrandEntity(Brand brand, Pageable pageable);
    Page<Product> findByCategoryEntity(Category category, Pageable pageable);
        Page<Product> findByCreator(User creator, Pageable pageable);



    Page<Product> findByCategoryEntityAndBrandEntity(Category category, Brand brand, Pageable pageable);



    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice, Pageable pageable);

    @Query("SELECT p.price FROM Product p ORDER BY p.price ASC LIMIT 1")
    double getMinPrice();

    @Query("SELECT p.price FROM Product p ORDER BY p.price DESC LIMIT 1")
    double getMaxPrice();

}
