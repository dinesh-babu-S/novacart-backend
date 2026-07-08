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

    // ==========================================
    // MULTI-VENDOR / ORDER ITEM APPROVAL WORKFLOW
    // ==========================================

    // Fetch incoming order items for logged-in admin
    @GetMapping("/admin/items")
    public ResponseEntity<List<project.NovaCart.dto.OrderItemResponse>> getAdminOrderItems() {
        return ResponseEntity.ok(service.getAdminOrderItems());
    }

    // Admin accepts order item (deducts stock, sets status to CONFIRMED)
    @PutMapping("/items/{itemId}/accept")
    public ResponseEntity<project.NovaCart.dto.OrderItemResponse> acceptOrderItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.acceptOrderItem(itemId));
    }

    // Admin rejects/cancels order item (sets status to CANCELLED)
    @PutMapping("/items/{itemId}/reject")
    public ResponseEntity<project.NovaCart.dto.OrderItemResponse> rejectOrderItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.rejectOrderItem(itemId));
    }

    // Admin marks order item as delivered (sets status to DELIVERED)
    @PutMapping("/items/{itemId}/deliver")
    public ResponseEntity<project.NovaCart.dto.OrderItemResponse> deliverOrderItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.deliverOrderItem(itemId));
    }

    // Customer requests return (sets status to RETURN_REQUESTED)
    @PutMapping("/items/{itemId}/return")
    public ResponseEntity<project.NovaCart.dto.OrderItemResponse> requestReturn(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.requestReturn(itemId));
    }

    // Admin accepts return (restores stock, sets status to RETURNED)
    @PutMapping("/items/{itemId}/accept-return")
    public ResponseEntity<project.NovaCart.dto.OrderItemResponse> acceptReturn(@PathVariable Long itemId) {
        return ResponseEntity.ok(service.acceptReturn(itemId));
    }
}
