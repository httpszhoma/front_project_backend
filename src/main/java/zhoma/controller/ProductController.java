package zhoma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhoma.models.Product;
import zhoma.repository.ProductRepository;
import zhoma.responses.ProductResponseDto;
import zhoma.service.ProductService;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@CrossOrigin("*")

public class ProductController {


    private final ProductService productService;
    private final ProductRepository productRepository;
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ProductResponseDto productResponseDto = convertToDto(product);
        return ResponseEntity.ok(productResponseDto);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<ProductResponseDto>> getProducts(@RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "20") int pageSize) {
        try {
            Page<Product> products = productService.getProducts(page, pageSize);
            Page<ProductResponseDto> productResponseDtos = products.map(this::convertToDto);
            return ResponseEntity.ok(productResponseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }



    @GetMapping("/count")
    public ResponseEntity<Long> getCountOfProducts() {
        try {
            long count = productService.getCountOfProducts();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(0L);
        }
    }

    @GetMapping("/search/price")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByPriceRange(
            @RequestParam("minPrice") double minPrice,
            @RequestParam("maxPrice") double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.findByPriceRange(minPrice, maxPrice, pageable);

        Page<ProductResponseDto> productDtos = products.map(this::convertToDto);

        return ResponseEntity.ok(productDtos);
    }

    @GetMapping("/get/minmax/price")
    public ResponseEntity<HashMap<String, Double>> getMinMaxPrice() {
        double minPrice = productRepository.getMinPrice();
        double maxPrice = productRepository.getMaxPrice();

        HashMap<String, Double> priceMap = new HashMap<>();
        priceMap.put("minPrice", minPrice);
        priceMap.put("maxPrice", maxPrice);

        return ResponseEntity.ok(priceMap);
    }



    @GetMapping("/search/{keyword}")
    public ResponseEntity<Page<ProductResponseDto>> getProductsByKeyword(
            @PathVariable("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> products = productRepository.searchByKeyword(keyword, pageable);

        Page<ProductResponseDto> productDtos = products.map(this::convertToDto);

        return ResponseEntity.ok(productDtos);
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
