package zhoma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhoma.dto.SellerRequestForUserDto;
import zhoma.models.SellerRequest;
import zhoma.models.User;
import zhoma.responses.UserResponseDto;
import zhoma.service.UserService;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get authenticated user", description = "Fetches the details of the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched authenticated user details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user is not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User currentUser = userService.getUserByUsername(username);
        UserResponseDto responseDto = mapToUserResponseDto(currentUser);


        return ResponseEntity.ok(responseDto);
    }

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

    private SellerRequestForUserDto mapToSellerRequestForUserDto(SellerRequest request) {
        return new SellerRequestForUserDto(
                request.getId(),
                request.getStatus().name()
        );
    }





}
