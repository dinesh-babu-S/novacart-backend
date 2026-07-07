package project.NovaCart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import project.NovaCart.dto.ProductRequest;
import project.NovaCart.dto.ProductResponse;
import project.NovaCart.entity.Category;
import project.NovaCart.entity.Product;
import project.NovaCart.exception.ResourceNotFoundException;
import project.NovaCart.repository.CategoryRepo;
import project.NovaCart.repository.ProductRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    public ProductService(ProductRepo productRepo,
                          CategoryRepo categoryRepo) {

        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
    }

    // Create Product
    public ProductResponse createProduct(ProductRequest request) {

        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        return mapToResponse(productRepo.save(product));
    }

    // Get All Products
    public List<ProductResponse> getAllProducts() {

        return productRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Product By Id
    public ProductResponse getProductById(Long id) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found."));

        return mapToResponse(product);
    }

    // Update Product
    public ProductResponse updateProduct(Long id,
                                         ProductRequest request) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found."));

        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImageUrl(request.getImageUrl());
        product.setCategory(category);

        return mapToResponse(productRepo.save(product));
    }

    // Delete Product
    public void deleteProduct(Long id) {

        Product product = productRepo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found."));

        productRepo.delete(product);
    }

    // Search Product
    public List<ProductResponse> searchProducts(String keyword) {

        return productRepo.searchByNameOrCategoryName(keyword)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Products By Category
    public List<ProductResponse> getProductsByCategory(Long categoryId) {

        return productRepo.findByCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<ProductResponse> getProducts(
        int page,
        int size,
        String sortBy,
        String direction) {

    Sort sort = direction.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();

    Pageable pageable = PageRequest.of(page, size, sort);

    return productRepo.findAll(pageable)
            .map(this::mapToResponse);
}

    // Entity -> DTO
    private ProductResponse mapToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory().getName()
        );
    }

}