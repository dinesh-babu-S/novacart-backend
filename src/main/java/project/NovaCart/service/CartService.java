package project.NovaCart.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import project.NovaCart.dto.CartRequest;
import project.NovaCart.dto.CartResponse;
import project.NovaCart.entity.CartItem;
import project.NovaCart.entity.Product;
import project.NovaCart.entity.SystemUser;
import project.NovaCart.exception.ResourceNotFoundException;
import project.NovaCart.repository.CartItemRepo;
import project.NovaCart.repository.ProductRepo;

@Service
public class CartService {

    private final CartItemRepo cartRepo;
    private final ProductRepo productRepo;
    private final SecurityService securityService;

    public CartService(CartItemRepo cartRepo,
            ProductRepo productRepo,
            SecurityService securityService) {

        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.securityService = securityService;
    }

    // Add Product to Cart
    public CartResponse addToCart(CartRequest request) {

        SystemUser user = securityService.getCurrentUser();

        Long userId = user.getId();

        Product product = productRepo.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found."));

        CartItem cartItem = cartRepo
                .findByUserIdAndProductId(userId, request.getProductId())
                .orElse(null);

        if (cartItem != null) {

            cartItem.setQuantity(
                    cartItem.getQuantity() + request.getQuantity());

        } else {

            cartItem = new CartItem();

            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.getQuantity());
        }

        return mapToResponse(cartRepo.save(cartItem));
    }

    // Get Current User Cart
    public List<CartResponse> getCart() {

        SystemUser user = securityService.getCurrentUser();

        return cartRepo.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Remove Cart Item
    public void removeFromCart(Long cartItemId) {

        CartItem item = cartRepo.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart Item not found."));

        cartRepo.delete(item);
    }

    // Entity -> DTO
    private CartResponse mapToResponse(CartItem item) {

        BigDecimal subtotal = item.getProduct()
                .getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        return new CartResponse(

                item.getId(),

                item.getProduct().getId(),

                item.getProduct().getName(),

                item.getProduct().getPrice(),

                item.getQuantity(),

                subtotal);
    }
}