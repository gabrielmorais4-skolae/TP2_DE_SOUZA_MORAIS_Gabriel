package com.formation.products.controller;

import java.net.URI;
import java.util.List;

import com.formation.products.dtos.request.CreateProductDto;
import com.formation.products.dtos.response.CategoryStats;
import com.formation.products.dtos.response.GetProductDto;
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
    public Response getAllProducts(@QueryParam("category") String category) {
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
    public Response getAllProductsSlow() {
        return Response.ok(productService.getAllProductsSlow()).build();
    }

    @GET
    @Path("/fast")
    public Response getAllProductsFast() {
        return Response.ok(productService.getAllProducts()).build();
    }

    @GET
    @Path("/{id}")
    public Response getProductById(@PathParam("id") String id) {
        GetProductDto dto = productService.getProductById(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return Response.ok(dto).build();
    }

    @GET
    @Path("/{id}/full")
    public Response getProductByIdFull(@PathParam("id") String id) {
        GetProductDto dto = productService.getProductByIdWithGraph(id)
            .orElseThrow(() -> new ProductNotFoundException(id));
        return Response.ok(dto).build();
    }

    @POST
    public Response createProduct(@Valid CreateProductDto productDTO) {
        GetProductDto created = productService.createProduct(productDTO);
        URI location = uriInfo.getAbsolutePathBuilder()
                      .path(created.getId())
                      .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateProduct(@PathParam("id") String id,
                                  @Valid CreateProductDto productDTO) {
        GetProductDto dto = productService.updateProduct(id, productDTO);
        return Response.ok(dto).build();
    }

    @PATCH
    @Path("/{id}/stock")
    public Response updateStock(@PathParam("id") String id,
                                StockUpdateRequest request) {
        GetProductDto dto = productService.updateStockQuantity(id, request.getQuantity());
        return Response.ok(dto).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteProduct(@PathParam("id") String id) {
        productService.deleteProduct(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/stats/count-by-category")
    public Response countByCategory() {
        return Response.ok(productService.countByCategory()).build();
    }

    @GET
    @Path("/stats/avg-price-by-category")
    public Response avgPriceByCategory() {
        return Response.ok(productService.averagePriceByCategory()).build();
    }

    @GET
    @Path("/stats/top-expensive")
    public Response topExpensive(@QueryParam("limit") @jakarta.ws.rs.DefaultValue("10") int limit) {
        return Response.ok(productService.findTopExpensive(limit)).build();
    }

    @GET
    @Path("/stats/never-ordered")
    public Response neverOrdered() {
        return Response.ok(productService.findNeverOrderedProducts()).build();
    }

    @GET
    @Path("/stats/category-stats")
    public Response categoryStats() {
        List<CategoryStats> stats = productService.findCategoryStats();
        return Response.ok(stats).build();
    }

    @GET
    @Path("/stats/categories-with-min-products")
    public Response categoriesWithMinProducts(@QueryParam("min") @jakarta.ws.rs.DefaultValue("1") int min) {
        return Response.ok(productService.findCategoriesWithMinProducts(min)).build();
    }

    public static class StockUpdateRequest {
        private int quantity;

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
}
