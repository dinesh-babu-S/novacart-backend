package project.NovaCart.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import project.NovaCart.dto.ProductRequest;
import project.NovaCart.dto.ProductResponse;
import project.NovaCart.service.ProductService;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    // Create Product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {

        return new ResponseEntity<>(
                service.createProduct(request),
                HttpStatus.CREATED);
    }

    // Get All Products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        return ResponseEntity.ok(
                service.getAllProducts());
    }

    // Get Logged-in Admin's Products
    @GetMapping("/mine")
    public ResponseEntity<List<ProductResponse>> getMyProducts() {

        return ResponseEntity.ok(
                service.getMyProducts());
    }

    // Get Product By Id
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getProductById(id));
    }

    // Update Product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                service.updateProduct(id, request));
    }

    // Delete Product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long id) {

        service.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }

    // Search Products
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                service.searchProducts(keyword));
    }

    // Products By Category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(
                service.getProductsByCategory(categoryId));
    }
@GetMapping("/page")
public ResponseEntity<Page<ProductResponse>> getProducts(

        @RequestParam(defaultValue = "0") int page,

        @RequestParam(defaultValue = "5") int size,

        @RequestParam(defaultValue = "id") String sortBy,

        @RequestParam(defaultValue = "asc") String direction) {

    return ResponseEntity.ok(
            service.getProducts(page, size, sortBy, direction));
}
}