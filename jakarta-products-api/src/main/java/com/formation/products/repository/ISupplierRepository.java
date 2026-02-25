package com.formation.products.repository;

import java.util.List;
import java.util.Optional;

import com.formation.products.model.Supplier;

public interface ISupplierRepository {
    Supplier save(Supplier supplier);

    Optional<Supplier> findById(String id);

    List<Supplier> findAll();

    void deleteById(String id);
}
