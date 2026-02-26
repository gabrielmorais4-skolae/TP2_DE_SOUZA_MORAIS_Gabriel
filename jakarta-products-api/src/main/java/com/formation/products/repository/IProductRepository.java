package com.formation.products.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.formation.products.dtos.response.CategoryStats;
import com.formation.products.model.Product;
import com.formation.products.model.Supplier;

public interface IProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();

    List<Product> findPaged(int page, int size);

    List<Product> findAllSlow();

    List<Product> findByCategory(String categoryId);

    List<Product> findBySupplier(Supplier supplier);

    List<Product> findByPriceRange(BigDecimal min, BigDecimal max);

    List<Product> searchByName(String keyword);

    void delete(String id);

    long count();

    List<Object[]> countByCategory();

    List<Object[]> averagePriceByCategory();

    List<Product> findTopExpensive(int limit);

    List<Product> findNeverOrderedProducts();

    List<CategoryStats> findCategoryStats();

    Optional<Product> findByIdWithGraph(String id);

    boolean existsBySku(String sku);
}
