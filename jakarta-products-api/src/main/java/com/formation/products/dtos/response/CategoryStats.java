package com.formation.products.dtos.response;

import java.math.BigDecimal;

public class CategoryStats {

    private String categoryName;
    private Long productCount;
    private BigDecimal averagePrice;

    public CategoryStats(String categoryName, Long productCount, Double averagePrice) {
        this.categoryName = categoryName;
        this.productCount = productCount;
        this.averagePrice = averagePrice != null ? BigDecimal.valueOf(averagePrice) : null;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Long getProductCount() { return productCount; }
    public void setProductCount(Long productCount) { this.productCount = productCount; }

    public BigDecimal getAveragePrice() { return averagePrice; }
    public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }
}
