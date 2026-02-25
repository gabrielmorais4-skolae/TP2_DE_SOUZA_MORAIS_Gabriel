package com.formation.products.repository;

import java.util.List;
import java.util.Optional;

import com.formation.products.model.Order;
import com.formation.products.model.OrderStatus;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class JpaOrderRepository implements IOrderRepository {

    @PersistenceContext(unitName = "productsPU")
    private EntityManager em;

    @Override
    public Order save(Order order) {
        if (order.getId() == null || em.find(Order.class, order.getId()) == null) {
            em.persist(order);
            return order;
        }
        return em.merge(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        TypedQuery<Order> query = em.createQuery(
            "SELECT o FROM Order o WHERE o.id = :id",
            Order.class);
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst();
    }

    @Override
    public List<Order> findAll() {
        return em.createQuery("SELECT o FROM Order o", Order.class)
                 .getResultList();
    }

    @Override
    public List<Order> findByCustomerEmail(String email) {
        TypedQuery<Order> query = em.createQuery(
            "SELECT o FROM Order o WHERE o.customerEmail = :email",
            Order.class);
        query.setParameter("email", email);
        return query.getResultList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        TypedQuery<Order> query = em.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status",
            Order.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    @Override
    public List<Order> findOrdersWithItems() {
        return em.createQuery(
            "SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items",
            Order.class)
            .getResultList();
    }

    @Override
    public void delete(Long id) {
        Order order = em.find(Order.class, id);
        if (order != null) {
            em.remove(order);
        }
    }
}
