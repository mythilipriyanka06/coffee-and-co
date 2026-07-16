package com.coffeeandco.cafe.repository;

import com.coffeeandco.cafe.model.Order;
import com.coffeeandco.cafe.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserOrderByOrderDateDesc(User user);
    List<Order> findAllByOrderByOrderDateDesc();
    Optional<Order> findByBillNumber(String billNumber);

    @Query("SELECT SUM(o.grandTotal) FROM Order o WHERE o.status = 'COMPLETED'")
    Double getTotalRevenue();

    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = 'PENDING'")
    Long countPendingOrders();
}
