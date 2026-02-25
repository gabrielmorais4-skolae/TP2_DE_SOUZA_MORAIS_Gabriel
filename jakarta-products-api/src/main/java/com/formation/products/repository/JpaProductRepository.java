package com.formation.products.repository;

import java.util.List;
import java.util.Optional;

import com.formation.products.model.Product;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class JpaProductRepository implements IProductRepository {

    @PersistenceContext(unitName = "ProductsPU")
    private EntityManager em;

    @Override
    public Product save(Product product) {
        if (em.find(Product.class, product.getId()) == null) {
            em.persist(product);
            return product;
        }
        return em.merge(product);
    }

    @Override
    public Optional<Product> findById(String id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    @Override
    public List<Product> findAll() {
        TypedQuery<Product> query = em.createQuery("SELECT p FROM Product p", Product.class);
        return query.getResultList();
    }

    @Override
    public List<Product> findByCategory(String category) {
        TypedQuery<Product> query = em.createQuery(
            "SELECT p FROM Product p WHERE LOWER(p.category) = LOWER(:category)", Product.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Override
    public boolean exists(String id) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.id = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult() > 0;
    }

    @Override
    public long count() {
        TypedQuery<Long> query = em.createQuery("SELECT COUNT(p) FROM Product p", Long.class);
        return query.getSingleResult();
    }

    @Override
    public void delete(String id) {
        Product product = em.find(Product.class, id);
        if (product != null) {
            em.remove(product);
        }
    }
}
