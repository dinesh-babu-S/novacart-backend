package project.NovaCart.dto;

import java.math.BigDecimal;
import project.NovaCart.entity.OrderStatus;

public class OrderItemResponse {

    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    private OrderStatus status;
    private Long productId;

    public OrderItemResponse() {
    }

    public OrderItemResponse(Long id,
                             String productName,
                             Integer quantity,
                             BigDecimal price,
                             BigDecimal subtotal,
                             OrderStatus status,
                             Long productId) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
        this.status = status;
        this.productId = productId;
    }

    // Deprecated but kept for backwards compatibility if needed
    public OrderItemResponse(String productName,
                             Integer quantity,
                             BigDecimal price,
                             BigDecimal subtotal) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
        this.status = OrderStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
