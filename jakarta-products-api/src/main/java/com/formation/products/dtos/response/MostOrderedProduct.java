package com.formation.products.dtos.response;

public class MostOrderedProduct {

    private String productId;
    private String productName;
    private Long totalQuantity;

    public MostOrderedProduct(String productId, String productName, Long totalQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantity = totalQuantity;
    }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public Long getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Long totalQuantity) { this.totalQuantity = totalQuantity; }
}
