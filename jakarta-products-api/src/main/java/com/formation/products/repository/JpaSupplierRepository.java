package com.formation.products.repository;

import java.util.List;
import java.util.Optional;

import com.formation.products.model.Supplier;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class JpaSupplierRepository implements ISupplierRepository {

    @PersistenceContext(unitName = "productsPU")
    private EntityManager em;

    @Override
    public Supplier save(Supplier supplier) {
        if (supplier.getId() == null || em.find(Supplier.class, supplier.getId()) == null) {
            em.persist(supplier);
            return supplier;
        }
        return em.merge(supplier);
    }

    @Override
    public Optional<Supplier> findById(String id) {
        return Optional.ofNullable(em.find(Supplier.class, id));
    }

    @Override
    public List<Supplier> findAll() {
        TypedQuery<Supplier> query = em.createQuery("SELECT s FROM Supplier s", Supplier.class);
        return query.getResultList();
    }

    @Override
    public void deleteById(String id) {
        Supplier supplier = em.find(Supplier.class, id);
        if (supplier != null) {
            em.remove(supplier);
        }
    }
}
