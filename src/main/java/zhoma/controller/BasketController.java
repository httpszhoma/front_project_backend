package zhoma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import zhoma.dto.BasketItemDto;
import zhoma.dto.BasketResponseDto;
import zhoma.dto.ProductQuantityUpdateDto;
import zhoma.models.Basket;
import zhoma.models.Product;
import zhoma.models.User;
import zhoma.service.BasketService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import zhoma.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/basket")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;
    private final UserService userService;



    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.getUserByUsername(username);
    }

    @GetMapping("/view")
    public ResponseEntity<BasketResponseDto> viewBasket() {
        User currentUser = getCurrentUser();
        Basket basket = basketService.getBasketForUser(currentUser);
        BasketResponseDto basketResponseDto = basketService.convertBasketToDto(basket);
        return ResponseEntity.ok(basketResponseDto);
    }

    // Add a product to the basket
    @PostMapping("/add")
    public ResponseEntity<String> addProductToBasket(@RequestParam Long productId, @RequestParam int quantity) {
        User currentUser = getCurrentUser();
        basketService.addProductToBasket(currentUser, productId, quantity);
        return ResponseEntity.ok("Product added to basket");
    }

    // Remove a product from the basket
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeProductFromBasket(@RequestParam Long productId) {
        User currentUser = getCurrentUser();
        basketService.removeProductFromBasket(currentUser, productId);
        return ResponseEntity.ok("Product removed from basket");
    }

    // Update the quantity of a product in the basket
    @PutMapping("/update")
    public ResponseEntity<String> updateProductQuantities(@RequestBody List<ProductQuantityUpdateDto> updates) {
        User currentUser = getCurrentUser();
        basketService.updateProductQuantities(currentUser, updates);
        return ResponseEntity.ok("Product quantities updated");
    }
    @DeleteMapping("/deleteAll")
    public ResponseEntity<String> removeProductFromBasket() {
        User currentUser = getCurrentUser();
        basketService.removeAllProductFromBasket(currentUser);
        return ResponseEntity.ok("Product removed from basket");
    }


}
