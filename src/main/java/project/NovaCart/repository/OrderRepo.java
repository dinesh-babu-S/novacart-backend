package project.NovaCart.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.NovaCart.entity.Order;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    // Get all orders of a user
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

}