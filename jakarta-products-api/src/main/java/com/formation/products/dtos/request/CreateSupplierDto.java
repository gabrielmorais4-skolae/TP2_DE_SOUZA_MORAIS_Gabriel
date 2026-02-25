package com.formation.products.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateSupplierDto {

    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    public CreateSupplierDto() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
