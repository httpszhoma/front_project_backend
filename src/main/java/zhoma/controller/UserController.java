package zhoma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import zhoma.dto.SellerRequestForUserDto;
import zhoma.dto.UserUpdateDto;
import zhoma.models.Product;
import zhoma.models.SellerRequest;
import zhoma.models.User;
import zhoma.responses.ProductResponseDto;
import zhoma.responses.UserResponseDto;
import zhoma.service.AzureBlobService;
import zhoma.service.OrderService;
import zhoma.service.ProductService;
import zhoma.service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/users")
@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final AzureBlobService azureBlobService;


    @Operation(summary = "Get authenticated user", description = "Fetches the details of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched authenticated user details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user is not authenticated")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        UserResponseDto responseDto = mapToUserResponseDto(currentUser);


        return ResponseEntity.ok(responseDto);
    }
    @PutMapping("/update/profile")
    public ResponseEntity<UserResponseDto> updateUser(@RequestBody UserUpdateDto userUpdateDto) {
        try {
            // Get the currently authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username);

            // Update the user's profile
            if (userUpdateDto.getFirstname() != null && !userUpdateDto.getFirstname().isEmpty()) {
                currentUser.setFirstname(userUpdateDto.getFirstname());
            }
            if (userUpdateDto.getLastname() != null && !userUpdateDto.getLastname().isEmpty()) {
                currentUser.setLastname(userUpdateDto.getLastname());
            }

            // Save the updated user
            User updatedUser = userService.saveUser(currentUser);

            // Map the updated user to UserResponseDto
            UserResponseDto responseDto = mapToUserResponseDto(updatedUser);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null); // Return 500 in case of an internal server error
        }
    }

    @PostMapping("/upload/profile-image")
    public ResponseEntity<UserResponseDto> uploadProfileImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            User currentUser = userService.getAuthenticatedUser();

            String contentType = imageFile.getContentType();
            if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
                return ResponseEntity.badRequest().body(null);
            }

            String imageUrl;
            try (InputStream inputStream = imageFile.getInputStream()) {
                User user = userService.getAuthenticatedUser();
                if(user.getImageUrl()!=null)
                    azureBlobService.deleteFile(user.getImageUrl());

                imageUrl = azureBlobService.uploadImage(imageFile.getOriginalFilename(), inputStream, imageFile.getSize());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            currentUser.setImageUrl(imageUrl);
            User updatedUser = userService.saveUser(currentUser);

            UserResponseDto responseDto = mapToUserResponseDto(updatedUser);
            return ResponseEntity.ok(responseDto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }




    @Operation(summary = "Get products added by the authenticated user", description = "Fetches the products added by the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched products added by the authenticated user"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user is not authenticated")
    })
    @GetMapping("/myproducts")
    public ResponseEntity<Page<ProductResponseDto>> getMyProducts(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "20") int pageSize) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);

        if (currentUser == null) {
            return ResponseEntity.status(404).body(null);  // Если пользователь не найден
        }

        try {
            Page<Product> products = productService.getProductsByUser(currentUser, page, pageSize);
            Page<ProductResponseDto> productResponseDtos = products.map(this::convertToDto);
            return ResponseEntity.ok(productResponseDtos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);  // Ошибка сервера
        }
    }


    private UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getSellerRequests().stream()
                        .map(this::mapToSellerRequestForUserDto)
                        .toList(),
                user.getImageUrl()

        );
    }

    private SellerRequestForUserDto mapToSellerRequestForUserDto(SellerRequest request) {
        return new SellerRequestForUserDto(
                request.getId(),
                request.getStatus().name()
        );
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
