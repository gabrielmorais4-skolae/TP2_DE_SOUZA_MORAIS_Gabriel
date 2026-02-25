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

@ApplicationScoped
public class CategoryService {

    @Inject
    private ICategoryRepository categoryRepository;

    public GetCategoryDto createCategory(CreateCategoryDto dto) {
        Category category = new Category();
        category.setName(dto.getName());
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }

    public Optional<GetCategoryDto> getCategoryById(String id) {
        return categoryRepository.findById(id).map(this::toDto);
    }

    public List<GetCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
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
        return dto;
    }
}
