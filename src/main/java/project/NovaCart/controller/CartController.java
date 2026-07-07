package project.NovaCart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import project.NovaCart.dto.CartRequest;
import project.NovaCart.dto.CartResponse;
import project.NovaCart.service.CartService;

@RestController
@RequestMapping("/api/cart")
@Validated
public class CartController {

    private final CartService service;

    public CartController(CartService service) {
        this.service = service;
    }

    // Add Product To Cart
    @PostMapping
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody CartRequest request) {

        return new ResponseEntity<>(
                service.addToCart(request),
                HttpStatus.CREATED);
    }

    // Get Logged-in User Cart
    @GetMapping
    public ResponseEntity<List<CartResponse>> getCart() {

        return ResponseEntity.ok(
                service.getCart());
    }

    // Remove Item
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long cartItemId) {

        service.removeFromCart(cartItemId);

        return ResponseEntity.noContent().build();
    }
}