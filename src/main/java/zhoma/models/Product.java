package zhoma.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private double price;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryEntity;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brandEntity;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private int quantity;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> images = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnore
    private User creator;

    public Product() {}

    public Product(String name, String description, double price, Category categoryEntity, Brand brandEntity, String status, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryEntity = categoryEntity;
        this.brandEntity = brandEntity;
        this.status = status;
        this.quantity = quantity;
    }

    public void addImage(String imageUrl) {
        ProductImage image = new ProductImage();
        image.setImageUrl(imageUrl);
        image.setProduct(this);
        images.add(image);
    }
}
