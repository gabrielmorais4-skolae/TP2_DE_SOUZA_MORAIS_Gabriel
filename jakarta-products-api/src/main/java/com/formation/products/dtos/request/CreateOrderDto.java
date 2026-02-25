package com.formation.products.dtos.request;

import java.util.Map;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class CreateOrderDto {

    @NotBlank
    private String customerName;

    @Email
    private String customerEmail;

    @NotEmpty
    private Map<String, Integer> productsAndQuantities;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public Map<String, Integer> getProductsAndQuantities() { return productsAndQuantities; }
    public void setProductsAndQuantities(Map<String, Integer> productsAndQuantities) {
        this.productsAndQuantities = productsAndQuantities;
    }
}
