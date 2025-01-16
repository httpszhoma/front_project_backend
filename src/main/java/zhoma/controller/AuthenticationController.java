package zhoma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhoma.dto.LoginUserDto;
import zhoma.dto.RefreshTokenDto;
import zhoma.dto.RegisterUserDto;
import zhoma.dto.VerifyUserDto;
import zhoma.exceptions.TokenInvalidException;
import zhoma.models.User;
import zhoma.responses.LoginResponse;
import zhoma.service.AuthenticationService;
import zhoma.service.JwtService;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
@CrossOrigin("*")

public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "User registration", description = "Registers a new user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "User login", description = "Authenticates a user and generates a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated and JWT token generated"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto){
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        LoginResponse loginResponse = new LoginResponse(jwtToken, refreshToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @Operation(summary = "Refresh access token", description = "Generates a new access token using a refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenDto refreshToken) {
        try {
            System.out.println("refreshToken = "+ refreshToken.getRefreshToken()+ "\n\n\n");
            String newAccessToken = jwtService.refreshAccessToken(refreshToken.getRefreshToken());
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("expiresIn", String.valueOf(jwtService.getExpirationTime()));
            return ResponseEntity.ok(response);
        } catch (TokenInvalidException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Operation(summary = "User verification", description = "Verifies the user's account with a verification code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account successfully verified"),
            @ApiResponse(responseCode = "400", description = "Invalid verification code or account not found")
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyUser(verifyUserDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Resend verification code", description = "Resends a verification code to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent"),
            @ApiResponse(responseCode = "400", description = "Email not found or an error occurred")
    })
    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot/password")
    @Operation(summary = "Send password reset code", description = "Sends a password reset verification code to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent"),
            @ApiResponse(responseCode = "400", description = "Email not found or an error occurred")
    })
    public ResponseEntity<?> sendForgotPasswordCode(@RequestParam String email) {
        try {
            authenticationService.sendForgotPasswordCode(email);
            return ResponseEntity.ok("Verification code sent to your email.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot/password/verify")
    @Operation(summary = "Verify password reset code", description = "Verifies the password reset verification code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification code")
    })
    public ResponseEntity<?> verifyForgotPasswordCode(
            @RequestParam String email,
            @RequestParam String verificationCode) {
        try {
            authenticationService.verifyForgotPasswordCode(email, verificationCode);
            return ResponseEntity.ok("Verification code verified successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset/password")
    @Operation(summary = "Reset password", description = "Resets the user's password after verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or unverified verification code")
    })
    public ResponseEntity<?> resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword) {
        try {
            authenticationService.resetPassword(email, newPassword);
            return ResponseEntity.ok("Password reset successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
