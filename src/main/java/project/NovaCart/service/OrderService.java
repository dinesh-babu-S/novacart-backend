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
import project.NovaCart.exception.UnauthorizedException;
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
            orderItem.setStatus(OrderStatus.PENDING); // Initialize status

            orderItem = orderItemRepo.save(orderItem);

            responseItems.add(
                    new OrderItemResponse(
                            orderItem.getId(),
                            product.getName(),
                            cart.getQuantity(),
                            product.getPrice(),
                            product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                            orderItem.getStatus(),
                            product.getId()
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
                                        item.getId(),
                                        item.getProduct().getName(),
                                        item.getQuantity(),
                                        item.getPrice(),
                                        item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                                        item.getStatus(),
                                        item.getProduct().getId()
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

    // Restore Stock only for confirmed/shipped/delivered items
    for (OrderItem item : items) {
        if (item.getStatus() == OrderStatus.CONFIRMED || item.getStatus() == OrderStatus.SHIPPED || item.getStatus() == OrderStatus.DELIVERED) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepo.save(product);
        }
        item.setStatus(OrderStatus.CANCELLED);
        orderItemRepo.save(item);
    }

    order.setStatus(OrderStatus.CANCELLED);
    orderRepo.save(order);

    return mapToOrderResponse(order);
}

    // ===========================
    // MULTI-VENDOR/APPROVAL METHODS
    // ===========================

    public List<OrderItemResponse> getAdminOrderItems() {
        SystemUser admin = securityService.getCurrentUser();
        List<OrderItem> items = orderItemRepo.findByProductCreatedById(admin.getId());
        return items.stream()
                .map(this::mapToOrderItemResponse)
                .toList();
    }

    public OrderItemResponse acceptOrderItem(Long itemId) {
        OrderItem item = orderItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found."));
        
        SystemUser admin = securityService.getCurrentUser();
        if (item.getProduct().getCreatedBy() == null || !item.getProduct().getCreatedBy().getId().equals(admin.getId())) {
            throw new UnauthorizedException("You are not authorized to accept this order item.");
        }

        if (item.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Order item is not pending.");
        }

        Product product = item.getProduct();
        if (product.getStock() < item.getQuantity()) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }

        // Deduct stock upon acceptance
        product.setStock(product.getStock() - item.getQuantity());
        productRepo.save(product);

        item.setStatus(OrderStatus.CONFIRMED);
        orderItemRepo.save(item);

        updateParentOrderStatus(item.getOrder());

        return mapToOrderItemResponse(item);
    }

    public OrderItemResponse rejectOrderItem(Long itemId) {
        OrderItem item = orderItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found."));
        
        SystemUser admin = securityService.getCurrentUser();
        if (item.getProduct().getCreatedBy() == null || !item.getProduct().getCreatedBy().getId().equals(admin.getId())) {
            throw new UnauthorizedException("You are not authorized to reject this order item.");
        }

        // If it was already confirmed, restore stock
        if (item.getStatus() == OrderStatus.CONFIRMED || item.getStatus() == OrderStatus.SHIPPED || item.getStatus() == OrderStatus.DELIVERED) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepo.save(product);
        }

        item.setStatus(OrderStatus.CANCELLED);
        orderItemRepo.save(item);

        updateParentOrderStatus(item.getOrder());

        return mapToOrderItemResponse(item);
    }

    public OrderItemResponse deliverOrderItem(Long itemId) {
        OrderItem item = orderItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found."));
        
        SystemUser admin = securityService.getCurrentUser();
        if (item.getProduct().getCreatedBy() == null || !item.getProduct().getCreatedBy().getId().equals(admin.getId())) {
            throw new UnauthorizedException("You are not authorized to deliver this order item.");
        }

        if (item.getStatus() != OrderStatus.CONFIRMED && item.getStatus() != OrderStatus.SHIPPED) {
            throw new BadRequestException("Order item must be confirmed or shipped first.");
        }

        item.setStatus(OrderStatus.DELIVERED);
        orderItemRepo.save(item);

        updateParentOrderStatus(item.getOrder());

        return mapToOrderItemResponse(item);
    }

    public OrderItemResponse requestReturn(Long itemId) {
        OrderItem item = orderItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found."));
        
        SystemUser user = securityService.getCurrentUser();
        if (!item.getOrder().getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not authorized to request return for this item.");
        }

        if (item.getStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Only delivered items can be returned.");
        }

        item.setStatus(OrderStatus.RETURN_REQUESTED);
        orderItemRepo.save(item);

        updateParentOrderStatus(item.getOrder());

        return mapToOrderItemResponse(item);
    }

    public OrderItemResponse acceptReturn(Long itemId) {
        OrderItem item = orderItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found."));
        
        SystemUser admin = securityService.getCurrentUser();
        if (item.getProduct().getCreatedBy() == null || !item.getProduct().getCreatedBy().getId().equals(admin.getId())) {
            throw new UnauthorizedException("You are not authorized to accept return for this order item.");
        }

        if (item.getStatus() != OrderStatus.RETURN_REQUESTED) {
            throw new BadRequestException("Return is not requested for this item.");
        }

        // Restore stock
        Product product = item.getProduct();
        product.setStock(product.getStock() + item.getQuantity());
        productRepo.save(product);

        item.setStatus(OrderStatus.RETURNED);
        orderItemRepo.save(item);

        updateParentOrderStatus(item.getOrder());

        return mapToOrderItemResponse(item);
    }

    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice(),
                item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
                item.getStatus(),
                item.getProduct().getId()
        );
    }

    private void updateParentOrderStatus(Order order) {
        List<OrderItem> allItems = orderItemRepo.findByOrderId(order.getId());
        if (allItems.isEmpty()) return;

        boolean hasPending = allItems.stream().anyMatch(i -> i.getStatus() == OrderStatus.PENDING);
        boolean allCancelledOrReturned = allItems.stream().allMatch(i -> i.getStatus() == OrderStatus.CANCELLED || i.getStatus() == OrderStatus.RETURNED);
        boolean allDeliveredOrEnded = allItems.stream().allMatch(i -> i.getStatus() == OrderStatus.DELIVERED || i.getStatus() == OrderStatus.CANCELLED || i.getStatus() == OrderStatus.RETURNED);

        if (hasPending) {
            order.setStatus(OrderStatus.PENDING);
        } else if (allCancelledOrReturned) {
            order.setStatus(OrderStatus.CANCELLED);
        } else if (allDeliveredOrEnded) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            order.setStatus(OrderStatus.CONFIRMED);
        }
        orderRepo.save(order);
    }
}
