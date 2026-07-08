package project.NovaCart.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private String productName;

    private Integer quantity;

    private BigDecimal price;

    private BigDecimal subtotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(String productName,
                             Integer quantity,
                             BigDecimal price,
                             BigDecimal subtotal) {

        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
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
}