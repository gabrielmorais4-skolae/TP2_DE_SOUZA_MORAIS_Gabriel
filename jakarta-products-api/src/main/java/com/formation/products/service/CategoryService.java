package com.formation.products.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.formation.products.dtos.request.CreateCategoryDto;
import com.formation.products.dtos.response.GetCategoryDto;
import com.formation.products.model.Category;
import com.formation.products.repository.ICategoryRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional
public class CategoryService {

    @Inject
    private ICategoryRepository categoryRepository;

    public GetCategoryDto createCategory(String name, String description) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return toDto(categoryRepository.save(category));
    }

    public GetCategoryDto createCategory(CreateCategoryDto dto) {
        return createCategory(dto.getName(), null);
    }

    public Optional<GetCategoryDto> getCategoryById(String id) {
        return categoryRepository.findById(id).map(this::toDto);
    }

    public List<GetCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public Optional<GetCategoryDto> getCategoryWithProducts(String id) {
        return categoryRepository.findWithProducts(id).map(this::toDtoWithProducts);
    }

    public Optional<GetCategoryDto> updateCategory(String id, CreateCategoryDto dto) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(dto.getName());
            return toDto(categoryRepository.save(category));
        });
    }

    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    private GetCategoryDto toDto(Category category) {
        GetCategoryDto dto = new GetCategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    private GetCategoryDto toDtoWithProducts(Category category) {
        GetCategoryDto dto = toDto(category);
        if (category.getProducts() != null) {
            dto.setProductNames(
                category.getProducts().stream()
                    .map(p -> p.getName())
                    .collect(Collectors.toList())
            );
        }
        return dto;
    }
}
