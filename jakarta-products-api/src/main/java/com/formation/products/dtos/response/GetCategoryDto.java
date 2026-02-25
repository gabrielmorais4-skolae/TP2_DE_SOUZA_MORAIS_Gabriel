package com.formation.products.dtos.response;

import java.util.List;

public class GetCategoryDto {
    private String id;
    private String name;
    private String description;
    private List<String> productNames;

    public GetCategoryDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getProductNames() { return productNames; }
    public void setProductNames(List<String> productNames) { this.productNames = productNames; }
}