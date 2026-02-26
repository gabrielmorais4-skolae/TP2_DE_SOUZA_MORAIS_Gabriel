package com.formation.products.controller;

import java.net.URI;
import java.util.List;

import com.formation.products.dtos.request.CreateCategoryDto;
import com.formation.products.dtos.response.GetCategoryDto;
import com.formation.products.exception.CategoryNotFoundException;
import com.formation.products.service.CategoryService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Tag(name = "Categories")
@Path("/categories")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CategoryController {

    @Inject
    private CategoryService categoryService;

    @Context
    UriInfo uriInfo;

    @GET
    @Operation(summary = "List all categories")
    @APIResponse(responseCode = "200", description = "Categories retrieved successfully")
    public Response getAllCategories() {
        List<GetCategoryDto> categories = categoryService.getAllCategories();
        return Response.ok(categories).build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get category by ID")
    @APIResponse(responseCode = "200", description = "Category found")
    @APIResponse(responseCode = "404", description = "Category not found")
    public Response getCategoryById(@PathParam("id") String id) {
        GetCategoryDto dto = categoryService.getCategoryById(id)
            .orElseThrow(() -> new CategoryNotFoundException(id));
        return Response.ok(dto).build();
    }

    @POST
    @Operation(summary = "Create a category")
    @APIResponse(responseCode = "201", description = "Category created successfully")
    @APIResponse(responseCode = "400", description = "Validation error")
    public Response createCategory(@Valid CreateCategoryDto dto) {
        GetCategoryDto created = categoryService.createCategory(dto);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update a category")
    @APIResponse(responseCode = "200", description = "Category updated successfully")
    @APIResponse(responseCode = "400", description = "Validation error")
    @APIResponse(responseCode = "404", description = "Category not found")
    public Response updateCategory(@PathParam("id") String id, @Valid CreateCategoryDto dto) {
        GetCategoryDto updated = categoryService.updateCategory(id, dto);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a category")
    @APIResponse(responseCode = "204", description = "Category deleted")
    @APIResponse(responseCode = "404", description = "Category not found")
    @APIResponse(responseCode = "409", description = "Category still contains products")
    public Response deleteCategory(@PathParam("id") String id) {
        categoryService.deleteCategory(id);
        return Response.noContent().build();
    }
}
