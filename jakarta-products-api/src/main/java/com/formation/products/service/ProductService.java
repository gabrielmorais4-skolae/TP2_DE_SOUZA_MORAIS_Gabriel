package com.formation.products.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.formation.products.dtos.request.CreateProductDto;
import com.formation.products.dtos.response.GetProductDto;
import com.formation.products.model.Product;
import com.formation.products.repository.IProductRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProductService {

    @Inject
    private IProductRepository productRepository;

    public GetProductDto createProduct(CreateProductDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (dto.getPrice() == null || dto.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        Product product = toEntity(dto);
        Product saved = productRepository.save(product);
        return toResponseDto(saved);
    }

    public Optional<GetProductDto> getProductById(String id) {
        return productRepository.findById(id)
            .map(this::toResponseDto);
    }

    public List<GetProductDto> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public List<GetProductDto> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public boolean productExists(String id) {
        return productRepository.exists(id);
    }

    public long getProductCount() {
        return productRepository.count();
    }

    public void deleteProduct(String id) {
        if (productRepository.exists(id)) {
            productRepository.delete(id);
        }
    }

    public Optional<GetProductDto> updateStockQuantity(String id, int newQuantity) {
        return productRepository.findById(id).map(product -> {
            product.setStockQuantity(newQuantity);
            return toResponseDto(productRepository.save(product));
        });
    }

    public Optional<GetProductDto> updateProduct(String id, CreateProductDto dto) {
        return productRepository.findById(id).map(product -> {
            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setCategory(dto.getCategory());
            product.setStockQuantity(dto.getStockQuantity());
            return toResponseDto(productRepository.save(product));
        });
    }

    private Product toEntity(CreateProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }

    private GetProductDto toResponseDto(Product entity) {
        GetProductDto dto = new GetProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setCategory(entity.getCategory());
        dto.setStockQuantity(entity.getStockQuantity());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
