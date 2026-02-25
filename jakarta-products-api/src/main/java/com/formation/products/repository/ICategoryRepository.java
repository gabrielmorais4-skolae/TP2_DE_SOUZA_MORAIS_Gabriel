package com.formation.products.repository;

import java.util.List;
import java.util.Optional;

import com.formation.products.model.Category;

public interface ICategoryRepository {
    Category save(Category category);

    Optional<Category> findById(String id);

    Optional<Category> findByName(String name);

    Optional<Category> findWithProducts(String id);

    List<Category> findAll();

    void deleteById(String id);

    List<Category> findCategoriesWithMinProducts(int minProducts);
}
