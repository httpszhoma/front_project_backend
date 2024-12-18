package zhoma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhoma.exceptions.BrandNotFoundException;
import zhoma.exceptions.CategoryNotFoundException;
import zhoma.models.Brand;
import zhoma.models.Category;
import zhoma.models.Product;
import zhoma.repository.BrandRepository;
import zhoma.repository.CategoryRepository;
import zhoma.repository.ProductRepository;
import zhoma.responses.ProductResponseDto;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/brands")
@RequiredArgsConstructor
@CrossOrigin("*")
public class BrandController {

    private final  BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;




    // Get a brand by ID
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        try {
            Optional<Brand> brandOptional = brandRepository.findById(id);
            return brandOptional.map(ResponseEntity::ok).orElseThrow(() -> new BrandNotFoundException("This brand doesn't exists !!!"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Get all brands
    @GetMapping("/list")
    public ResponseEntity<List<Brand>> getAllBrands() {
        try {
            List<Brand> brands = brandRepository.findAll();

            return ResponseEntity.ok(brands);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    // Get products by brand ID with pagination
    @GetMapping("/{categoryId}/{brandId}/products")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByCategoryAndBrand(
            @PathVariable Long categoryId,
            @PathVariable Long brandId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        try {
            // Find the category by ID
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException("This category doesn't exist!"));

            // Find the brand by ID
            Brand brand = brandRepository.findById(brandId)
                    .orElseThrow(() -> new BrandNotFoundException("This brand doesn't exist!"));

            // Create a Pageable object for pagination
            Pageable pageable = PageRequest.of(page, pageSize);

            // Fetch the products belonging to the given category and brand with pagination
            Page<Product> products = productRepository.findByCategoryEntityAndBrandEntity(category, brand, pageable);
            Page<ProductResponseDto> productResponseDtos = products.map(this::convertToDto);

            return ResponseEntity.ok(productResponseDtos);
        } catch (CategoryNotFoundException | BrandNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


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
