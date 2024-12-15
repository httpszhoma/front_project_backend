package zhoma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhoma.dto.SellerRequestForUserDto;
import zhoma.models.SellerRequest;
import zhoma.models.User;
import zhoma.responses.UserResponseDto;
import zhoma.responses.UserSellerResponseDto;
import zhoma.service.SellerRequestService;
import zhoma.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final SellerRequestService sellerRequestService;

    // Helper method to map User to UserResponseDto
    private UserResponseDto mapToUserResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getSellerRequests().stream()
                        .map(this::mapToSellerRequestForUserDto)
                        .toList()
        );
    }

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
                request.getId(),
                request.getUser().getRole().name(),
                request.getUser().getId(),
                request.getUser().getEmail(),
                request.getUser().getUsername(),
                request.getCreatedAt()
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
