package com.formation.products.dtos.response;

public class GetSupplierDto {
    private String id;
    private String name;

    public GetSupplierDto() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
