package project.NovaCart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.NovaCart.entity.CartItem;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, Long> {

    // Get all cart items of a user
    List<CartItem> findByUserId(Long userId);

    // Check if the product already exists in user's cart
    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);

}