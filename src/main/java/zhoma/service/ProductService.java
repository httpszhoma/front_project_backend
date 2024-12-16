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
import zhoma.models.Brand;
import zhoma.models.Category;
import zhoma.models.Product;
import zhoma.models.ProductImage;
import zhoma.repository.BrandRepository;
import zhoma.repository.CategoryRepository;
import zhoma.repository.ProductRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final AzureBlobService azureBlobService;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;


    // Service: Create Product
    public Long createProduct(ProductRequestDto productRequestDto) {
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
                allUrl.append(imageUrl+ "\n");
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

    public long getCountOfProducts() {
        return productRepository.count();
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct != null) {
            existingProduct.setName(updatedProduct.getName());
            existingProduct.setDescription(updatedProduct.getDescription());
            existingProduct.setPrice(updatedProduct.getPrice());
            existingProduct.setStatus(updatedProduct.getStatus());
            existingProduct.setQuantity(updatedProduct.getQuantity());
            existingProduct.setCategoryEntity(updatedProduct.getCategoryEntity());
            existingProduct.setBrandEntity(updatedProduct.getBrandEntity());
            return productRepository.save(existingProduct);
        }
        return null;
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
