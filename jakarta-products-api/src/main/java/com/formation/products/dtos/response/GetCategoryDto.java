package com.formation.products.dtos.response;

public class GetCategoryDto {
    private String id;
    private String name;

    public GetCategoryDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}