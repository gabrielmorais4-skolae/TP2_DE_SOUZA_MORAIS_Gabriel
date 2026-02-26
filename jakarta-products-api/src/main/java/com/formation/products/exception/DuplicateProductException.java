package com.formation.products.exception;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String sku) {
        super("A product already exists with SKU: " + sku);
    }
}
