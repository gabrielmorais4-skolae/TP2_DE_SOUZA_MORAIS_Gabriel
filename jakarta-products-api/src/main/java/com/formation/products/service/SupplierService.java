package com.formation.products.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.formation.products.dtos.request.CreateSupplierDto;
import com.formation.products.dtos.response.GetSupplierDto;
import com.formation.products.model.Supplier;
import com.formation.products.repository.ISupplierRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class SupplierService {

    @Inject
    private ISupplierRepository supplierRepository;

    public GetSupplierDto createSupplier(CreateSupplierDto dto) {
        Supplier supplier = new Supplier();
        supplier.setName(dto.getName());
        Supplier saved = supplierRepository.save(supplier);
        return toDto(saved);
    }

    public Optional<GetSupplierDto> getSupplierById(String id) {
        return supplierRepository.findById(id).map(this::toDto);
    }

    public List<GetSupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public Optional<GetSupplierDto> updateSupplier(String id, CreateSupplierDto dto) {
        return supplierRepository.findById(id).map(supplier -> {
            supplier.setName(dto.getName());
            return toDto(supplierRepository.save(supplier));
        });
    }

    public void deleteSupplier(String id) {
        supplierRepository.deleteById(id);
    }

    private GetSupplierDto toDto(Supplier supplier) {
        GetSupplierDto dto = new GetSupplierDto();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        return dto;
    }
}
