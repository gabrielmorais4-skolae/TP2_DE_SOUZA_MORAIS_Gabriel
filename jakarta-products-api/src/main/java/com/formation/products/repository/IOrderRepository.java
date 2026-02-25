package com.formation.products.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.formation.products.dtos.response.MostOrderedProduct;
import com.formation.products.model.Order;
import com.formation.products.model.OrderStatus;

public interface IOrderRepository {

    Order save(Order order);

    Optional<Order> findById(Long id);

    List<Order> findAll();

    List<Order> findByCustomerEmail(String email);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findOrdersWithItems();

    void delete(Long id);

    BigDecimal getTotalRevenue();

    List<Object[]> countByStatus();

    List<MostOrderedProduct> findMostOrderedProducts(int limit);
}
