package zhoma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import zhoma.exceptions.CategoryNotFoundException;
import zhoma.models.Brand;
import zhoma.models.Category;
import zhoma.models.Product;
import zhoma.repository.CategoryRepository;
import zhoma.repository.ProductRepository;
import zhoma.responses.ProductResponseDto;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin("*")

public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    // Get a category by ID
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        try {
            Optional<Category> categoryOptional = categoryRepository.findById(id);
            return categoryOptional.map(ResponseEntity::ok)
                    .orElseThrow(() -> new CategoryNotFoundException("This category doesn't exist!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get all categories
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Category>> getAllCategories() {
        try {
            List<Category> categories = categoryRepository.findAll();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{categoryId}/brands")
    public ResponseEntity<HashMap<Long, String>> getBrandsByCategoryId(@PathVariable Long categoryId) {
        try {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("This category doesn't exist!"));

            HashMap<Long, String> brandMap = category.getBrands().stream()
                    .collect(Collectors.toMap(
                            Brand::getId,
                            Brand::getName,
                            (existing, replacement) -> existing,
                            HashMap::new
                    ));

            return ResponseEntity.ok(brandMap);
        } catch (CategoryNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }





    // Get products by category ID with pagination
    @GetMapping("/{categoryId}/products")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            // Find the category by ID
            Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
            if (categoryOptional.isPresent()) {
                Category category = categoryOptional.get();

                // Create a Pageable object for pagination
                Pageable pageable = PageRequest.of(page, pageSize);

                // Fetch the products belonging to the given category with pagination
                Page<Product> products = productRepository.findByCategoryEntity(category, pageable);
                Page<ProductResponseDto> productResponseDtos = products.map(this::convertToDto);

                return ResponseEntity.ok(productResponseDtos);
            } else {
                return ResponseEntity.status(404).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Helper method to convert Product to ProductResponseDto
    private ProductResponseDto convertToDto(Product product) {
        ProductResponseDto responseDto = new ProductResponseDto();
        responseDto.setId(product.getId());
        responseDto.setName(product.getName());
        responseDto.setDescription(product.getDescription());
        responseDto.setPrice(product.getPrice());
        responseDto.setStatus(product.getStatus());
        responseDto.setQuantity(product.getQuantity());

        // Set category
        ProductResponseDto.CategoryDto categoryDto = new ProductResponseDto.CategoryDto();
        categoryDto.setId(product.getCategoryEntity().getId());
        categoryDto.setName(product.getCategoryEntity().getName());
        responseDto.setCategoryEntity(categoryDto);

        // Set brand
        ProductResponseDto.BrandDto brandDto = new ProductResponseDto.BrandDto();
        brandDto.setId(product.getBrandEntity().getId());
        brandDto.setName(product.getBrandEntity().getName());
        responseDto.setBrandEntity(brandDto);

        // Set images
        List<ProductResponseDto.ImageDto> imageDtos = product.getImages().stream()
                .map(image -> {
                    ProductResponseDto.ImageDto imageDto = new ProductResponseDto.ImageDto();
                    imageDto.setImageUrl(image.getImageUrl());
                    return imageDto;
                })
                .collect(Collectors.toList());
        responseDto.setImages(imageDtos);

        return responseDto;
    }
}
