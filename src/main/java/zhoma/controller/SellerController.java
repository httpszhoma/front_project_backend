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
    public ResponseEntity<HashMap<String, Object>> createProduct(@RequestBody ProductRequestDto productRequestDto) {
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

    @PutMapping("/{id}/update")
    public ResponseEntity<HashMap<String, Object>> updateProduct(@PathVariable Long id, @RequestBody ProductRequestDto productRequestDto) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Call the service method to update the product
            Product updatedProduct = productService.updateProduct(id, productRequestDto);

            // Return the updated product's ID in the response
            response.put("productId", updatedProduct.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Return an error message if something goes wrong
            response.put("error", "Error updating product: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    @PostMapping("{id}/updatefiles")
    public ResponseEntity<HashMap<String, String>> updateProductFiles(@PathVariable Long id, @RequestParam("files") MultipartFile[] files) {
        HashMap<String, String> response = new HashMap<>();
        try {
            Product product = productService.getProductById(id);

            String result = productService.updateFiles(files, product);

            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error updating files: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }



}