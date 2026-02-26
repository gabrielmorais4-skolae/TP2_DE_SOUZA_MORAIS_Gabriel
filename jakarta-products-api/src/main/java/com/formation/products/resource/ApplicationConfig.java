package com.formation.products.resource;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
    info = @Info(
        title = "Products API",
        version = "1.0.0",
        description = "REST API for managing products, categories, suppliers and orders — Jakarta EE 10 / WildFly"
    ),
    servers = @Server(url = "http://localhost:8080/api/v1"),
    tags = {
        @Tag(name = "Products",    description = "Product management operations"),
        @Tag(name = "Categories",  description = "Category management operations"),
        @Tag(name = "Suppliers",   description = "Supplier management operations"),
        @Tag(name = "Orders",      description = "Order management operations")
    }
)
@ApplicationPath("/api/v1")
public class ApplicationConfig extends Application {
}
