package com.formation.products.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.formation.products.model.Product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

@Alternative
@ApplicationScoped
public class InMemoryProductRepository implements IProductRepository {

    private final ConcurrentHashMap<String, Product> products = new ConcurrentHashMap<>();

    public InMemoryProductRepository() {
        Product laptop = new Product();
        laptop.setName("Laptop Dell XPS");
        laptop.setDescription("High-performance laptop with 16GB RAM");
        laptop.setPrice(new BigDecimal("999.99"));
        laptop.setCategory("Electronics");
        laptop.setStockQuantity(10);

        Product smartphone = new Product();
        smartphone.setName("iPhone 15 Pro");
        smartphone.setDescription("Latest smartphone with amazing camera");
        smartphone.setPrice(new BigDecimal("1299.99"));
        smartphone.setCategory("Electronics");
        smartphone.setStockQuantity(25);

        Product chair = new Product();
        chair.setName("Ergonomic Office Chair");
        chair.setDescription("Comfortable chair for long work sessions");
        chair.setPrice(new BigDecimal("299.99"));
        chair.setCategory("Furniture");
        chair.setStockQuantity(15);

        products.put(laptop.getId(), laptop);
        products.put(smartphone.getId(), smartphone);
        products.put(chair.getId(), chair);
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null || product.getId().isEmpty()) {
            product.setId(java.util.UUID.randomUUID().toString());
        }
        products.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(products.get(id));
    }

    @Override
    public List<Product> findAll() {
        return List.copyOf(products.values());
    }

    @Override
    public List<Product> findByCategory(String category) {
        return products.values().stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .toList();
    }

    @Override
    public boolean exists(String id) {
        return products.containsKey(id);
    }

    @Override
    public long count() {
        return products.size();
    }

    @Override
    public void delete(String id) {
        products.remove(id);
    }
}
