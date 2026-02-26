package com.formation.products.dtos.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GetProductDto {

    private String id;
    private String name;
    private String description;
    private String sku;
    private BigDecimal price;
    private GetCategoryDto category;
    private GetSupplierDto supplier;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GetProductDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public GetCategoryDto getCategory() { return category; }
    public void setCategory(GetCategoryDto category) { this.category = category; }

    public GetSupplierDto getSupplier() { return supplier; }
    public void setSupplier(GetSupplierDto supplier) { this.supplier = supplier; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
