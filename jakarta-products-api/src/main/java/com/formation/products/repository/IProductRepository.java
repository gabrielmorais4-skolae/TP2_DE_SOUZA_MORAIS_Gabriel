package com.formation.products.repository;

import java.util.List;
import java.util.Optional;

import com.formation.products.model.Product;

public interface IProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();

    List<Product> findByCategory(String category);

    long count(String id);

    void delete(String id);
}
