package project.NovaCart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.NovaCart.entity.OrderItem;

@Repository
public interface OrderItemRepo extends JpaRepository<OrderItem, Long> {

    // Get all items of an order
    List<OrderItem> findByOrderId(Long orderId);

}