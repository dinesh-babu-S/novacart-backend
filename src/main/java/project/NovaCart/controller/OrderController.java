package project.NovaCart.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import project.NovaCart.dto.OrderResponse;
import project.NovaCart.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // Checkout
    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {

        return new ResponseEntity<>(
                service.checkout(),
                HttpStatus.CREATED);
    }

    // Get My Orders
    @GetMapping("/my-orders")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {

        return ResponseEntity.ok(
                service.getMyOrders());
    }

    // Get Order Details
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                service.getOrder(orderId));
    }

    // Cancel Order
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                service.cancelOrder(orderId));
    }
}