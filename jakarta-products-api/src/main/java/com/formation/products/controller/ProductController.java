package com.formation.products.controller;

import java.net.URI;
import java.util.List;

import com.formation.products.dtos.request.CreateProductDto;
import com.formation.products.dtos.response.CategoryStats;
import com.formation.products.dtos.response.GetProductDto;
import com.formation.products.dtos.response.PagedResponse;
import com.formation.products.exception.ProductNotFoundException;
import com.formation.products.service.ProductService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Products")
@Path("/products")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {

    @Inject
    private ProductService productService;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(
        summary = "List products",
        description = "Returns all products. Pass ?page=0&size=10 to get a paginated response, or ?category={id} to filter by category."
    )
    @APIResponse(responseCode = "200", description = "Products retrieved successfully")
    public Response getAllProducts(
            @Parameter(description = "Filter by category ID") @QueryParam("category") String category,
            @Parameter(description = "Zero-based page index (enables pagination)") @QueryParam("page") Integer page,
            @Parameter(description = "Page size (default 10)") @QueryParam("size") @jakarta.ws.rs.DefaultValue("10") int size) {

        if (page != null) {
            PagedResponse<GetProductDto> paged = productService.getProductsPaged(page, size);
            return Response.ok(paged).build();
        }

        List<GetProductDto> products;
        if (category != null && !category.isBlank()) {
            products = productService.getProductsByCategory(category);
        } else {
            products = productService.getAllProducts();
        }
        return Response.ok(products).build();
    }

    @GET
    @Path("/slow")
    @Operation(summary = "List products without JOIN FETCH (N+1 demo)", description = "Intentionally slow — demonstrates the N+1 query problem.")
    @APIResponse(responseCode = "200", description = "Products retrieved")
    public Response getAllProductsSlow() {
        return Response.ok(productService.getAllProductsSlow()).build();
    }

    @GET
    @Path("/fast")
    @Operation(summary = "List products with JOIN FETCH (N+1 solution)")
    @APIResponse(responseCode = "200", description = "Products retrieved efficiently")
    public Response getAllProductsFast() {
        return Response.ok(productService.getAllProducts()).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get product by ID")
    @APIResponse(responseCode = "200", description = "Product found")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response getProductById(@PathParam("id") String id) {
        GetProductDto dto = productService.getProductById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{id}/full")
    @Operation(summary = "Get product by ID with full entity graph (category + supplier)")
    @APIResponse(responseCode = "200", description = "Product found")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response getProductByIdFull(@PathParam("id") String id) {
        GetProductDto dto = productService.getProductByIdWithGraph(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return Response.ok(dto).build();
    }

    @POST
    @Operation(summary = "Create a product")
    @APIResponse(responseCode = "201", description = "Product created successfully")
    @APIResponse(responseCode = "400", description = "Validation error — missing or invalid fields")
    @APIResponse(responseCode = "409", description = "Conflict — duplicate SKU")
    public Response createProduct(@Valid CreateProductDto productDTO) {
        GetProductDto created = productService.createProduct(productDTO);
        URI location = uriInfo.getAbsolutePathBuilder()
                      .path(created.getId())
                      .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a product")
    @APIResponse(responseCode = "200", description = "Product updated successfully")
    @APIResponse(responseCode = "400", description = "Validation error")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response updateProduct(@PathParam("id") String id,
                                  @Valid CreateProductDto productDTO) {
        GetProductDto dto = productService.updateProduct(id, productDTO);
        return Response.ok(dto).build();
    }

    @PATCH
    @Path("/{id}/stock")
    @Operation(summary = "Update stock quantity for a product")
    @APIResponse(responseCode = "200", description = "Stock updated")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response updateStock(@PathParam("id") String id,
                                StockUpdateRequest request) {
        GetProductDto dto = productService.updateStockQuantity(id, request.getQuantity());
        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a product")
    @APIResponse(responseCode = "204", description = "Product deleted")
    @APIResponse(responseCode = "404", description = "Product not found")
    public Response deleteProduct(@PathParam("id") String id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/stats/count-by-category")
    @Operation(summary = "Count products grouped by category")
    @APIResponse(responseCode = "200", description = "Aggregation result")
    public Response countByCategory() {
        return Response.ok(productService.countByCategory()).build();
    }

    @GET
    @Path("/stats/avg-price-by-category")
    @Operation(summary = "Average price grouped by category")
    @APIResponse(responseCode = "200", description = "Aggregation result")
    public Response avgPriceByCategory() {
        return Response.ok(productService.averagePriceByCategory()).build();
    }

    @GET
    @Path("/stats/top-expensive")
    @Operation(summary = "Top N most expensive products")
    @APIResponse(responseCode = "200", description = "Sorted product list")
    public Response topExpensive(@Parameter(description = "Number of products to return") @QueryParam("limit") @jakarta.ws.rs.DefaultValue("10") int limit) {
        return Response.ok(productService.findTopExpensive(limit)).build();
    }

    @GET
    @Path("/stats/never-ordered")
    @Operation(summary = "Products that have never been ordered")
    @APIResponse(responseCode = "200", description = "Product list")
    public Response neverOrdered() {
        return Response.ok(productService.findNeverOrderedProducts()).build();
    }

    @GET
    @Path("/stats/category-stats")
    @Operation(summary = "Complete statistics per category (count + avg price)")
    @APIResponse(responseCode = "200", description = "CategoryStats list")
    public Response categoryStats() {
        List<CategoryStats> stats = productService.findCategoryStats();
        return Response.ok(stats).build();
    }

    @GET
    @Path("/stats/categories-with-min-products")
    @Operation(summary = "Categories with at least N products")
    @APIResponse(responseCode = "200", description = "Category list")
    public Response categoriesWithMinProducts(@Parameter(description = "Minimum product count") @QueryParam("min") @jakarta.ws.rs.DefaultValue("1") int min) {
        return Response.ok(productService.findCategoriesWithMinProducts(min)).build();
    }

    public static class StockUpdateRequest {
        private int quantity;

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
