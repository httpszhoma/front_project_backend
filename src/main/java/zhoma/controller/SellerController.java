package zhoma.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhoma.dto.ProductRequestDto;
import zhoma.responses.ProductResponseDto;
import zhoma.models.Product;
import zhoma.service.ProductService;
import zhoma.service.AzureBlobService;

import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/seller/products")
@RequiredArgsConstructor

public class SellerController {

    private final ProductService productService;

    @PostMapping("/create")
    public ResponseEntity<HashMap<String, Object>> createProduct(@ModelAttribute ProductRequestDto productRequestDto) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            System.out.println(productRequestDto.toString());

            // Call the service method to create the product
            long productId = productService.createProduct(productRequestDto);  // Assuming this method returns productId

            // On success, include the productId in the response
            response.put("productId", productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // On error, return the error message
            response.put("error", "Error creating product: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }



    @PostMapping("{id}/files")
    public ResponseEntity<HashMap<String, String>> addProductFiles(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        HashMap<String, String> response = new HashMap<>();
        try {
            // Get the product by id
            Product product = productService.getProductById(id);

            // Call the service method to add files to the product
            String result = productService.addFileProduct(files, product);

            // On success, include the result (file URLs or error message) in the response
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // On error, return the error message
            response.put("error", "Error uploading files: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            ProductResponseDto productResponseDto = convertToDto(product);
            return ResponseEntity.ok(productResponseDto);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
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

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        String result = productService.deleteProduct(id);
        return ResponseEntity.ok(result);
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