package com.formation.products.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.formation.products.dtos.response.MostOrderedProduct;
import com.formation.products.exception.InsufficientStockException;
import com.formation.products.exception.ProductNotFoundException;
import com.formation.products.model.Order;
import com.formation.products.model.OrderItem;
import com.formation.products.model.OrderStatus;
import com.formation.products.model.Product;
import com.formation.products.repository.IOrderRepository;
import com.formation.products.repository.IProductRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrderService {

    @Inject
    private IOrderRepository orderRepository;

    @Inject
    private IProductRepository productRepository;

    @Transactional
    public Order createOrder(String customerName, String customerEmail,
                             Map<String, Integer> productsAndQuantities) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);

        for (Map.Entry<String, Integer> entry : productsAndQuantities.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                .orElseThrow(() -> new ProductNotFoundException(entry.getKey()));

            int requested = entry.getValue();
            if (product.getStockQuantity() < requested) {
                throw new InsufficientStockException(product.getName(), requested, product.getStockQuantity());
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(requested);
            item.setUnitPrice(product.getPrice());

            order.addItem(item);
        }

        order.calculateTotal();
        return orderRepository.save(order);
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        order.setStatus(newStatus);
        orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Order> getOrdersWithItems() {
        return orderRepository.findOrdersWithItems();
    }

    @Transactional
    public void deleteOrder(Long id) {
        orderRepository.delete(id);
    }

    public BigDecimal getTotalRevenue() {
        return orderRepository.getTotalRevenue();
    }

    public List<Object[]> countByStatus() {
        return orderRepository.countByStatus();
    }

    public List<MostOrderedProduct> findMostOrderedProducts(int limit) {
        return orderRepository.findMostOrderedProducts(limit);
    }

}
