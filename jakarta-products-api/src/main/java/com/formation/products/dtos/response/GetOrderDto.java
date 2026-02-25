package com.formation.products.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.formation.products.model.OrderStatus;

public class GetOrderDto {

    private Long id;
    private String orderNumber;
    private String customerName;
    private String customerEmail;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private List<GetOrderItemDto> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public List<GetOrderItemDto> getItems() { return items; }
    public void setItems(List<GetOrderItemDto> items) { this.items = items; }
}
