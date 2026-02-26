package com.formation.products.exception;

public class CategoryNotEmptyException extends RuntimeException {
    public CategoryNotEmptyException(String categoryName) {
        super("Cannot delete category '" + categoryName + "' because it still contains products");
    }
}
