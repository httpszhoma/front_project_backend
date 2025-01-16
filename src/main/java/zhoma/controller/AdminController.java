package zhoma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhoma.dto.BrandCreateDto;
import zhoma.dto.CategoryCreateDto;
import zhoma.dto.SellerRequestForUserDto;
import zhoma.exceptions.CategoryNotFoundException;
import zhoma.models.Brand;
import zhoma.models.Category;
import zhoma.models.SellerRequest;
import zhoma.models.User;
import zhoma.repository.BrandRepository;
import zhoma.repository.CategoryRepository;
import zhoma.repository.ProductRepository;
import zhoma.responses.UserResponseDto;
import zhoma.responses.UserSellerResponseDto;
import zhoma.service.SellerRequestService;
import zhoma.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {

    private final UserService userService;
    private final SellerRequestService sellerRequestService;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;

    @PostMapping("/create/brand")
    public ResponseEntity<HashMap<String, Object>> createBrand(@RequestBody BrandCreateDto brandDto) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Find the category by ID
            Category category = categoryRepository.findById(brandDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found!"));

            // Create a new Brand entity
            Brand brand = new Brand();
            brand.setName(brandDto.getName());
            brand.setCategory(category);

            // Save the brand
            Brand savedBrand = brandRepository.save(brand);
            response.put("brandId", savedBrand.getId());
            response.put("message", "Brand created successfully!");
            return ResponseEntity.ok(response);
        } catch (CategoryNotFoundException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(404).body(response);
        } catch (Exception e) {
            response.put("error", "Error creating brand: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    // Delete a brand by ID
    @DeleteMapping("/delete/brand/{id}")
    public ResponseEntity<HashMap<String, String>> deleteBrand(@PathVariable Long id) {
        HashMap<String, String> response = new HashMap<>();
        try {
            Optional<Brand> brandOptional = brandRepository.findById(id);
            if (brandOptional.isPresent()) {
                brandRepository.deleteById(id);
                response.put("message", "Brand deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Brand not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error deleting brand: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/create/category")
    public ResponseEntity<HashMap<String, Object>> createCategory(@RequestBody CategoryCreateDto categoryDto) {
        HashMap<String, Object> response = new HashMap<>();
        try {
            // Create a new Category entity
            Category category = new Category();
            category.setName(categoryDto.getName());

            // Save the new category
            Category savedCategory = categoryRepository.save(category);

            // Prepare response
            response.put("categoryId", savedCategory.getId());
            response.put("message", "Category created successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Error creating category: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete/category/{id}")
    public ResponseEntity<HashMap<String, String>> deleteCategory(@PathVariable Long id) {
        HashMap<String, String> response = new HashMap<>();
        try {
            Optional<Category> categoryOptional = categoryRepository.findById(id);
            if (categoryOptional.isPresent()) {
                categoryRepository.deleteById(id);
                response.put("message", "Category deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Category not found");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Error deleting category: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }


    // Helper method to map User to UserResponseDto
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
// mapstruct or create class which convert
    // Helper method to map SellerRequest to SellerRequestForUserDto
    private SellerRequestForUserDto mapToSellerRequestForUserDto(SellerRequest request) {
        return new SellerRequestForUserDto(
                request.getId(),
                request.getStatus().name()
        );
    }

    // Helper method to map SellerRequest to UserSellerResponseDto
    private UserSellerResponseDto mapToUserSellerResponseDto(SellerRequest request) {
        return new UserSellerResponseDto(
                request.getUser().getId(), // User ID
                request.getUser().getEmail(), // Email
                request.getUser().getUsername(), // Username
                request.getCreatedAt(), // Created at timestamp
                request.getDescription() // Description of the request
        );
    }


    @Operation(summary = "Get all users", description = "Fetches the list of all users in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all users"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user does not have access to this resource")
    })
    @GetMapping("/allUser")
    public ResponseEntity<List<UserResponseDto>> allUsers() {
        List<User> users = userService.allUsers();
        List<UserResponseDto> response = users.stream()
                .map(this::mapToUserResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all seller requests", description = "Fetches all requests for becoming a seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all seller requests"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user does not have access to this resource")
    })
    @GetMapping("/allRequests")
    public ResponseEntity<List<UserSellerResponseDto>> allSellerRequests() {
        List<SellerRequest> requests = sellerRequestService.getAllPendingRequests();
        List<UserSellerResponseDto> response = requests.stream()
                .map(this::mapToUserSellerResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Approve seller request", description = "Approve a user's request for becoming a seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request approved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user does not have access to this resource"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PatchMapping("/approveRequest/{requestId}")
    public ResponseEntity<String> approveSellerRequest(@PathVariable Long requestId) {
        sellerRequestService.approveRequest(requestId);
        return ResponseEntity.ok("Seller request approved.");
    }

    @Operation(summary = "Reject seller request", description = "Reject a user's request for becoming a seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request rejected successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user does not have access to this resource"),
            @ApiResponse(responseCode = "404", description = "Request not found")
    })
    @PatchMapping("/rejectRequest/{requestId}")
    public ResponseEntity<String> rejectSellerRequest(@PathVariable Long requestId) {
        sellerRequestService.rejectRequest(requestId);
        return ResponseEntity.ok("Seller request rejected.");
    }

    @Operation(summary = "Delete user", description = "Deletes a user from the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user does not have access to this resource"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/deleteUser/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully.");
    }

    @Operation(summary = "Get user details", description = "Fetches all information about a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched user information"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user does not have access to this resource"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/userInfo/{userId}")
    public ResponseEntity<UserResponseDto> getUserInfo(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        UserResponseDto response = mapToUserResponseDto(user);
        return ResponseEntity.ok(response);
    }


}
