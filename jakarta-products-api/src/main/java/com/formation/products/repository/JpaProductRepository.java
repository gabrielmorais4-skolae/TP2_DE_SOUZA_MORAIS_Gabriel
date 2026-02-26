package com.formation.products.repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.formation.products.dtos.response.CategoryStats;
import com.formation.products.model.Product;
import com.formation.products.model.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class JpaProductRepository implements IProductRepository {

    @PersistenceContext(unitName = "productsPU")
    private EntityManager em;

    @Override
    public Product save(Product product) {
        if (product.getId() == null || em.find(Product.class, product.getId()) == null) {
            em.persist(product);
            return product;
        }
        return em.merge(product);
    }

    @Override
    public Optional<Product> findById(String id) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.id = :id",
            Product.class);
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Product> findAll() {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier",
            Product.class);
        return query.getResultList();
    }

    @Override
    public List<Product> findByCategory(String categoryId) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.category.id = :categoryId",
            Product.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }

    @Override
    public List<Product> findBySupplier(Supplier supplier) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier WHERE p.supplier = :supplier",
            Product.class);
        query.setParameter("supplier", supplier);
        return query.getResultList();
    }

    @Override
    public List<Product> findByPriceRange(BigDecimal min, BigDecimal max) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier" +
            " WHERE p.price BETWEEN :min AND :max ORDER BY p.price",
            Product.class);
        query.setParameter("min", min);
        query.setParameter("max", max);
        return query.getResultList();
    }

    @Override
    public List<Product> searchByName(String keyword) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier" +
            " WHERE LOWER(p.name) LIKE LOWER(:keyword)",
            Product.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    @Override
    public void delete(String id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }

    @Override
    public long count() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Product p", Long.class);
        return query.getSingleResult();
    }

    @Override
    public List<Object[]> countByCategory() {
        return em.createQuery(
            "SELECT p.category.name, COUNT(p) FROM Product p GROUP BY p.category",
            Object[].class)
            .getResultList();
    }

    @Override
    public List<Object[]> averagePriceByCategory() {
        return em.createQuery(
            "SELECT p.category.name, AVG(p.price) FROM Product p GROUP BY p.category",
            Object[].class)
            .getResultList();
    }

    @Override
    public List<Product> findTopExpensive(int limit) {
        return em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier ORDER BY p.price DESC",
            Product.class)
            .setMaxResults(limit)
            .getResultList();
    }

    @Override
    public List<Product> findNeverOrderedProducts() {
        return em.createQuery(
            "SELECT p FROM Product p WHERE p NOT IN (SELECT oi.product FROM OrderItem oi)",
            Product.class)
            .getResultList();
    }

    @Override
    public List<CategoryStats> findCategoryStats() {
        return em.createQuery(
            "SELECT NEW com.formation.products.dtos.response.CategoryStats(" +
            "p.category.name, COUNT(p), AVG(p.price)) " +
            "FROM Product p GROUP BY p.category",
            CategoryStats.class)
            .getResultList();
    }

    @Override
    public List<Product> findPaged(int page, int size) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p LEFT JOIN FETCH p.category LEFT JOIN FETCH p.supplier ORDER BY p.name",
            Product.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    @Override
    public List<Product> findAllSlow() {
        return em.createQuery("SELECT p FROM Product p", Product.class)
            .getResultList();
    }

    @Override
    public boolean existsBySku(String sku) {
        Long count = em.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.sku = :sku", Long.class)
            .setParameter("sku", sku)
            .getSingleResult();
        return count > 0;
    }

    @Override
    public Optional<Product> findByIdWithGraph(String id) {
        Map<String, Object> hints = new HashMap<>();
        hints.put("jakarta.persistence.fetchgraph", em.getEntityGraph("Product.full"));
        return Optional.ofNullable(em.find(Product.class, id, hints));
    }
}
