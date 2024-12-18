package zhoma.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Brand> brands;

    @OneToMany(mappedBy = "categoryEntity", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Product> products;

    public Category() {}

    public Category(String name) {
        this.name = name;
    }
}
