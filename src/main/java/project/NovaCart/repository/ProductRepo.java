package project.NovaCart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import project.NovaCart.entity.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {

    // Search products by name
    List<Product> findByNameContainingIgnoreCase(String name);

    // Search products by name or category name
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByNameOrCategoryName(@Param("keyword") String keyword);

    // Get products by category
    List<Product> findByCategoryId(Long categoryId);
  // Get products by creator (admin)
    List<Product> findByCreatedById(Long id);
}
