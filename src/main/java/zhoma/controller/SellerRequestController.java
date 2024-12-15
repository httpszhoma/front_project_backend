package zhoma.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import zhoma.dto.SellerRequestDto;
import zhoma.service.SellerRequestService;

@RestController
@RequestMapping("/request")
@RequiredArgsConstructor
public class SellerRequestController {

    private final SellerRequestService sellerRequestService;

    @Operation(summary = "Submit seller request", description = "Allows a user to request becoming a seller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Request submitted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized, the user does not have access to this resource")
    })
    @PostMapping("/seller")
    public ResponseEntity<String> submitSellerRequest(@RequestBody SellerRequestDto sellerRequestDto) {

        User authenticatedUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();


        sellerRequestService.submitSellerRequest(authenticatedUser.getUsername(), sellerRequestDto.getDescription());
        return ResponseEntity.ok("Seller request submitted successfully.");
    }
}


