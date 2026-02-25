package com.formation.products.repository;

import java.util.List;
import java.util.Optional;

import com.formation.products.model.Category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class JpaCategoryRepository implements ICategoryRepository {

    @PersistenceContext(unitName = "productsPU")
    private EntityManager em;

    @Override
    public Category save(Category category) {
        if (category.getId() == null || em.find(Category.class, category.getId()) == null) {
            em.persist(category);
            return category;
        }
        return em.merge(category);
    }

    @Override
    public Optional<Category> findById(String id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    @Override
    public List<Category> findAll() {
        TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c", Category.class);
        return query.getResultList();
    }

    @Override
    public Optional<Category> findByName(String name) {
        TypedQuery<Category> query = em.createQuery(
            "SELECT c FROM Category c WHERE c.name = :name", Category.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public Optional<Category> findWithProducts(String id) {
        TypedQuery<Category> query = em.createQuery(
            "SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id", Category.class);
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public void deleteById(String id) {
        Category category = em.find(Category.class, id);
        if (category != null) {
            em.remove(category);
        }
    }
}
