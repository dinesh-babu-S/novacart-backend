package project.NovaCart.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import project.NovaCart.dto.CategoryRequest;
import project.NovaCart.dto.CategoryResponse;
import project.NovaCart.entity.Category;
import project.NovaCart.entity.SystemUser;
import project.NovaCart.exception.BadRequestException;
import project.NovaCart.exception.ResourceNotFoundException;
import project.NovaCart.exception.UnauthorizedException;
import project.NovaCart.repository.CategoryRepo;

@Service
public class CategoryService {

    private final CategoryRepo repo;
    private final SecurityService securityService;

    public CategoryService(CategoryRepo repo, SecurityService securityService) {
        this.repo = repo;
        this.securityService = securityService;
    }

    // Create Category
    public CategoryResponse createCategory(CategoryRequest request) {

        if (repo.findBySlug(request.getSlug()).isPresent()) {
            throw new BadRequestException("Category slug already exists.");
        }

        Category category = new Category();

        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());
        category.setCreatedBy(securityService.getCurrentUser());

        return mapToResponse(repo.save(category));
    }

    // Get All Categories
    public List<CategoryResponse> getAllCategories() {

        return repo.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Get Category By ID
    public CategoryResponse getCategoryById(Long id) {

        Category category = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        return mapToResponse(category);
    }

    // Update Category
    public CategoryResponse updateCategory(Long id,
                                           CategoryRequest request) {

        Category category = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        SystemUser currentUser = securityService.getCurrentUser();
        if (category.getCreatedBy() != null && !category.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to update this category.");
        }

        category.setName(request.getName());
        category.setSlug(request.getSlug());
        category.setDescription(request.getDescription());

        return mapToResponse(repo.save(category));
    }

    // Delete Category
    public void deleteCategory(Long id) {

        Category category = repo.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        SystemUser currentUser = securityService.getCurrentUser();
        if (category.getCreatedBy() != null && !category.getCreatedBy().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You are not authorized to delete this category.");
        }

        repo.delete(category);
    }
    public CategoryResponse getCategoryBySlug(String slug) {

    Category category = repo.findBySlug(slug)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Category not found."));

    return mapToResponse(category);
}

    // Entity -> DTO
    private CategoryResponse mapToResponse(Category category) {

        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getCreatedBy() != null ? category.getCreatedBy().getId() : null);
    }

}