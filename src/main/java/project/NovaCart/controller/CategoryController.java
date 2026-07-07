
package project.NovaCart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import project.NovaCart.dto.CategoryRequest;
import project.NovaCart.dto.CategoryResponse;
import project.NovaCart.service.CategoryService;

@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService service;

    public CategoryController(CategoryService service) {
        this.service = service;
    }

    // Create Category
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory( @Valid @RequestBody CategoryRequest request) {

        return new ResponseEntity<>(
                service.createCategory(request),
                HttpStatus.CREATED);
    }

    // Get All Categories
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        return ResponseEntity.ok(
                service.getAllCategories());
    }

    // Get Category By Id
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                service.getCategoryById(id));
    }

    // Update Category
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                service.updateCategory(id, request));
    }

    // Delete Category
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        service.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
    @GetMapping("/slug/{slug}")
public ResponseEntity<CategoryResponse> getCategoryBySlug(
        @PathVariable String slug) {

    return ResponseEntity.ok(
            service.getCategoryBySlug(slug));
}

}