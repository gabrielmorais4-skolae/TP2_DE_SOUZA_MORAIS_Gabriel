package com.formation.products.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.formation.products.dtos.request.CreateProductDto;
import com.formation.products.dtos.response.GetCategoryDto;
import com.formation.products.dtos.response.GetProductDto;
import com.formation.products.dtos.response.GetSupplierDto;
import com.formation.products.model.Category;
import com.formation.products.model.Product;
import com.formation.products.model.Supplier;
import com.formation.products.repository.ICategoryRepository;
import com.formation.products.repository.IProductRepository;
import com.formation.products.repository.ISupplierRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class ProductService {

    @Inject
    private IProductRepository productRepository;

    @Inject
    private ICategoryRepository categoryRepository;

    @Inject
    private ISupplierRepository supplierRepository;

    public GetProductDto createProduct(CreateProductDto dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
            .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryId()));

        Supplier supplier = null;
        if (dto.getSupplierId() != null && !dto.getSupplierId().isBlank()) {
            supplier = supplierRepository.findById(dto.getSupplierId())
                .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getSupplierId()));
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        product.setCategory(category);
        product.setSupplier(supplier);

        return toResponseDto(productRepository.save(product));
    }

    public Optional<GetProductDto> getProductById(String id) {
        return productRepository.findById(id).map(this::toResponseDto);
    }

    public List<GetProductDto> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public List<GetProductDto> getProductsByCategory(String categoryId) {
        return productRepository.findByCategory(categoryId).stream()
            .map(this::toResponseDto)
            .collect(Collectors.toList());
    }

    public Optional<GetProductDto> updateProduct(String id, CreateProductDto dto) {
        return productRepository.findById(id).map(product -> {
            Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryId()));

            Supplier supplier = null;
            if (dto.getSupplierId() != null && !dto.getSupplierId().isBlank()) {
                supplier = supplierRepository.findById(dto.getSupplierId())
                    .orElseThrow(() -> new IllegalArgumentException("Supplier not found: " + dto.getSupplierId()));
            }

            product.setName(dto.getName());
            product.setDescription(dto.getDescription());
            product.setPrice(dto.getPrice());
            product.setStockQuantity(dto.getStockQuantity());
            product.setCategory(category);
            product.setSupplier(supplier);

            return toResponseDto(productRepository.save(product));
        });
    }

    public Optional<GetProductDto> updateStockQuantity(String id, int newQuantity) {
        return productRepository.findById(id).map(product -> {
            product.setStockQuantity(newQuantity);
            return toResponseDto(productRepository.save(product));
        });
    }

    public void deleteProduct(String id) {
        if (productRepository.count(id) > 0) {
            productRepository.delete(id);
        }
    }

    /**
     * Crée un produit en cherchant ou créant la catégorie par son nom.
     * Tout se passe dans UNE seule transaction : si une étape échoue, tout est annulé.
     */
    @Transactional
    public GetProductDto createProductWithCategory(Product product, String categoryName) {
        // 1. Chercher ou créer la catégorie
        Category category = categoryRepository.findByName(categoryName)
            .orElseGet(() -> {
                Category newCategory = new Category();
                newCategory.setName(categoryName);
                return categoryRepository.save(newCategory);
            });

        // 2. Assigner la catégorie au produit
        product.setCategory(category);

        // 3. Sauvegarder le produit
        return toResponseDto(productRepository.save(product));
    }

    /**
     * Met à jour le stock d'un produit.
     * L'entité est "managed" dans la transaction : pas besoin d'appeler save()
     * explicitement, le flush automatique en fin de transaction suffit.
     * On appelle save() ici pour être explicite et compatible avec notre repo.
     */
    @Transactional
    public void updateStock(String productId, int quantity) {
        // 1. Charger le produit (entité managed dans la transaction)
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // 2. Modifier le stock
        product.setStockQuantity(quantity);

        // 3. Pas besoin d'appeler save() : l'entité est managed, le changement
        //    sera flushé automatiquement à la fin de la transaction.
        //    On l'appelle quand même pour la clarté avec notre implémentation.
        productRepository.save(product);
    }

    /**
     * Déplace tous les produits d'une catégorie vers une autre.
     * Transaction atomique : soit tout est déplacé, soit rien ne l'est.
     */
    @Transactional
    public void transferProducts(String fromCategoryId, String toCategoryId) {
        Category toCategory = categoryRepository.findById(toCategoryId)
            .orElseThrow(() -> new IllegalArgumentException("Target category not found: " + toCategoryId));

        List<Product> products = productRepository.findByCategory(fromCategoryId);
        for (Product product : products) {
            product.setCategory(toCategory);
            productRepository.save(product);
        }
    }

    /**
     * Méthode de démonstration du rollback.
     * Le produit est persisté puis une exception est levée → rollback automatique.
     * Le produit NE doit PAS apparaître en base après l'appel.
     */
    @Transactional
    public void testRollback() {
        Product p = new Product();
        p.setName("Rollback Test Product");
        p.setPrice(java.math.BigDecimal.ONE);
        p.setStockQuantity(0);
        productRepository.save(p);

        // L'exception déclenche le rollback : le produit ci-dessus ne sera pas commité
        throw new RuntimeException("Test rollback : cette transaction doit être annulée");
    }

    private GetProductDto toResponseDto(Product entity) {
        GetProductDto dto = new GetProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setStockQuantity(entity.getStockQuantity());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getCategory() != null) {
            GetCategoryDto catDto = new GetCategoryDto();
            catDto.setId(entity.getCategory().getId());
            catDto.setName(entity.getCategory().getName());
            dto.setCategory(catDto);
        }

        if (entity.getSupplier() != null) {
            GetSupplierDto supDto = new GetSupplierDto();
            supDto.setId(entity.getSupplier().getId());
            supDto.setName(entity.getSupplier().getName());
            dto.setSupplier(supDto);
        }

        return dto;
    }
}
