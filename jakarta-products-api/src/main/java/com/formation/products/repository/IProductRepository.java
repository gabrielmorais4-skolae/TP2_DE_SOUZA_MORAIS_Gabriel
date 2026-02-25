package com.formation.products.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.formation.products.model.Category;
import com.formation.products.model.Product;
import com.formation.products.model.Supplier;

public interface IProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();

    List<Product> findByCategory(String categoryId);

    List<Product> findBySupplier(Supplier supplier);

    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);

    List<Product> searchByName(String keyword);

    void delete(String id);

    long count();
}
