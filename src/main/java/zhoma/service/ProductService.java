package zhoma.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import zhoma.dto.ProductRequestDto;
import zhoma.exceptions.BrandNotFoundException;
import zhoma.exceptions.CategoryNotFoundException;
import zhoma.exceptions.ProductNotFountException;
import zhoma.models.*;
import zhoma.repository.BrandRepository;
import zhoma.repository.CategoryRepository;
import zhoma.repository.ProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final AzureBlobService azureBlobService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;


    // Service: Create Product
    public Long createProduct(ProductRequestDto productRequestDto, User creater) {
        Brand brand = brandRepository
                .findBrandById(productRequestDto.getBrandId())
                .orElseThrow(() -> new BrandNotFoundException("This brand doesn't exist"));

        Category category = categoryRepository
                .findCategoriesById(productRequestDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("This category doesn't exist"));

        // Create product and save it
        Product product = new Product();
        product.setPrice(productRequestDto.getPrice());
        product.setName(productRequestDto.getName());
        product.setStatus(productRequestDto.getStatus());
        product.setDescription(productRequestDto.getDescription());
        product.setQuantity(productRequestDto.getQuantity());
        product.setBrandEntity(brand);
        product.setCategoryEntity(category);
        product.setCreator(creater);


        productRepository.save(product);

        return product.getId();  // Return the ID of the created product
    }

    // Service: Add files to Product
    public String addFileProduct(MultipartFile[] files, Product product) {

        StringBuilder allUrl = new StringBuilder();
        String imageUrl = null;
        try {
            // Loop through the files and upload them
            for (MultipartFile file : files) {
                imageUrl = azureBlobService.uploadImage(file.getOriginalFilename(), file.getInputStream(), file.getSize());
                product.addImage(imageUrl);
                // Add the image URL to the product
                allUrl.append(imageUrl).append("\n");
            }

            // Save the updated product with the added images
            productRepository.save(product);

            return "Product images uploaded successfully! Your image urls: " + allUrl;
        } catch (Exception e) {
            return "Error uploading files: " + e.getMessage();
        }
    }


    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("This product doesn't exist!"));
    }

    public Page<Product> getProducts(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findAll(pageable);
    }
    public Page<Product> getProductsByUser(User user, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return productRepository.findByCreator(user, pageable);  // Поиск продуктов по пользователю
    }


    public long getCountOfProducts() {
        return productRepository.count();
    }

    public Product updateProduct(Long id, ProductRequestDto productRequestDto) {
        // Fetch the product by ID
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFountException("Product not found"));

        // Update the product's fields with the new data
        product.setName(productRequestDto.getName());
        product.setDescription(productRequestDto.getDescription());
        product.setPrice(productRequestDto.getPrice());
        product.setQuantity(productRequestDto.getQuantity());
        product.setStatus(productRequestDto.getStatus());

        // Update the category and brand
        if (productRequestDto.getCategoryId() != null) {
            Category category = categoryRepository.findCategoriesById(productRequestDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
            product.setCategoryEntity(category);
        }

        if (productRequestDto.getBrandId() != null) {
            Brand brand = brandRepository.findBrandById(productRequestDto.getBrandId())
                    .orElseThrow(() -> new BrandNotFoundException("Brand not found"));
            product.setBrandEntity(brand);
        }

        // Save the updated product
        return productRepository.save(product);  // Return the updated product
    }


    public String updateFiles(MultipartFile[] files, Product product) {
        try {
            for (ProductImage image : product.getImages()) {
                azureBlobService.deleteFile(image.getImageUrl());  // Delete from Azure Blob Storage
            }
            product.getImages().clear();
            String result = addFileProduct(files, product);
            productRepository.save(product);
            return "Product images updated successfully!";
        } catch (Exception e) {
            return "Error updating product images: " + e.getMessage();
        }
    }


    public String deleteProduct(Long id) {
        try {
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFountException("Product not found"));
            for (ProductImage image : product.getImages()) {
                azureBlobService.deleteFile(image.getImageUrl());  // Удаляем изображения из хранилища
            }
            productRepository.deleteById(id);
            return "Product and its images deleted successfully.";
        } catch (Exception e) {
            return "Error deleting product: " + e.getMessage();
        }
    }
}
