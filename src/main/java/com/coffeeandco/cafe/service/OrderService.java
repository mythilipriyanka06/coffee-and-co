package com.coffeeandco.cafe.service;

import com.coffeeandco.cafe.model.*;
import com.coffeeandco.cafe.repository.OrderRepository;
import com.coffeeandco.cafe.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order placeOrder(User user, Cart cart) {
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty!");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(user.getFullName());
        order.setOrderDate(LocalDateTime.now());
        order.setSubTotal(cart.getSubTotal());
        order.setGstAmount(cart.getGstAmount());
        order.setGrandTotal(cart.getGrandTotal());
        order.setStatus("PENDING");
        order.setBillNumber(generateBillNumber());

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProduct().getName()));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName() + ". Available: " + product.getStock());
            }

            // Deduct stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            if (product.getStock() <= 0) {
                product.setAvailable(false);
            }
            productRepository.save(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());
            order.addItem(orderItem);
        }

        return orderRepository.save(order);
    }

    private String generateBillNumber() {
        LocalDateTime now = LocalDateTime.now();
        String dateStr = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = new Random().nextInt(9000) + 1000; // 4 digit random number
        return "CC-" + dateStr + "-" + rand;
    }

    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUserOrderByOrderDateDesc(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Order getOrderByBillNumber(String billNumber) {
        return orderRepository.findByBillNumber(billNumber).orElse(null);
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setStatus(status);
            return orderRepository.save(order);
        }
        return null;
    }

    public Double getTotalRevenue() {
        Double rev = orderRepository.getTotalRevenue();
        return rev != null ? rev : 0.0;
    }

    public Long getPendingOrdersCount() {
        return orderRepository.countPendingOrders();
    }

    public Long getTotalOrdersCount() {
        return orderRepository.count();
    }
}
