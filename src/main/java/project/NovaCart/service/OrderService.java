package project.NovaCart.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.NovaCart.dto.OrderItemResponse;
import project.NovaCart.dto.OrderResponse;
import project.NovaCart.entity.CartItem;
import project.NovaCart.entity.Order;
import project.NovaCart.entity.OrderItem;
import project.NovaCart.entity.OrderStatus;
import project.NovaCart.entity.Product;
import project.NovaCart.entity.SystemUser;
import project.NovaCart.exception.BadRequestException;
import project.NovaCart.exception.ResourceNotFoundException;
import project.NovaCart.repository.CartItemRepo;
import project.NovaCart.repository.OrderItemRepo;
import project.NovaCart.repository.OrderRepo;
import project.NovaCart.repository.ProductRepo;
import project.NovaCart.repository.UserRepo;

@Service
@Transactional
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderItemRepo orderItemRepo;
    private final CartItemRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final SecurityService securityService;

   public OrderService(OrderRepo orderRepo,
                    OrderItemRepo orderItemRepo,
                    CartItemRepo cartRepo,
                    UserRepo userRepo,
                    ProductRepo productRepo,
                    SecurityService securityService) {

    this.orderRepo = orderRepo;
    this.orderItemRepo = orderItemRepo;
    this.cartRepo = cartRepo;
    this.userRepo = userRepo;
    this.productRepo = productRepo;
    this.securityService = securityService;
}
    

    // ===========================
    // CHECKOUT
    // ===========================
    public OrderResponse checkout() {

  SystemUser user = securityService.getCurrentUser();

Long userId = user.getId();

        List<CartItem> cartItems = cartRepo.findByUserId(userId);

        if (cartItems.isEmpty()) {
            throw new ResourceNotFoundException("Cart is empty.");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            BigDecimal subtotal = item.getProduct()
                    .getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            total = total.add(subtotal);
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(total);

        order = orderRepo.save(order);

        List<OrderItemResponse> responseItems = new ArrayList<>();

        for (CartItem cart : cartItems) {

            Product product = cart.getProduct();

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItemRepo.save(orderItem);

            responseItems.add(

                    new OrderItemResponse(

                            product.getName(),

                            cart.getQuantity(),

                            product.getPrice(),

                            product.getPrice().multiply(
                                    BigDecimal.valueOf(
                                            cart.getQuantity()))
                    )
            );
        }

        // Clear Cart
        cartRepo.deleteAll(cartItems);

        return new OrderResponse(

                order.getId(),

                order.getOrderDate(),

                order.getTotalAmount(),

                order.getStatus(),

                responseItems
        );
    }

    // ===========================
    // GET ORDER HISTORY
    // ===========================
   public List<OrderResponse> getMyOrders() {

    SystemUser user = securityService.getCurrentUser();

    List<Order> orders =
            orderRepo.findByUserIdOrderByOrderDateDesc(user.getId());

    return orders.stream()
            .map(this::mapToOrderResponse)
            .toList();
}

    // ===========================
    // GET ORDER BY ID
    // ===========================
    public OrderResponse getOrder(Long orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found."));

        return mapToOrderResponse(order);
    }

    // ===========================
    // ENTITY -> DTO
    // ===========================
    private OrderResponse mapToOrderResponse(Order order) {

        List<OrderItem> items =
                orderItemRepo.findByOrderId(order.getId());

        List<OrderItemResponse> responses =
                items.stream()
                        .map(item ->

                                new OrderItemResponse(

                                        item.getProduct().getName(),

                                        item.getQuantity(),

                                        item.getPrice(),

                                        item.getPrice().multiply(
                                                BigDecimal.valueOf(
                                                        item.getQuantity()))
                                )

                        ).toList();

        return new OrderResponse(

                order.getId(),

                order.getOrderDate(),

                order.getTotalAmount(),

                order.getStatus(),

                responses
        );
    }
    // ===========================
// CANCEL ORDER
// ===========================
public OrderResponse cancelOrder(Long orderId) {

    Order order = orderRepo.findById(orderId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Order not found."));

    if (order.getStatus() == OrderStatus.SHIPPED ||
            order.getStatus() == OrderStatus.DELIVERED) {

        throw new BadRequestException(
                "Order cannot be cancelled.");
    }

    if (order.getStatus() == OrderStatus.CANCELLED) {

        throw new BadRequestException(
                "Order already cancelled.");
    }

    List<OrderItem> items =
            orderItemRepo.findByOrderId(orderId);

    // Restore Stock
    for (OrderItem item : items) {

        Product product = item.getProduct();

        product.setStock(
                product.getStock() + item.getQuantity());

        productRepo.save(product);
    }

    order.setStatus(OrderStatus.CANCELLED);

    orderRepo.save(order);

    return mapToOrderResponse(order);
}
}