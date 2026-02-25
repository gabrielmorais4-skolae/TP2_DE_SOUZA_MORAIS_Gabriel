package com.formation.products.controller;

import java.net.URI;
import java.util.List;

import com.formation.products.dtos.request.CreateCategoryDto;
import com.formation.products.dtos.response.GetCategoryDto;
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
    public Response getAllCategories() {
        List<GetCategoryDto> categories = categoryService.getAllCategories();
        return Response.ok(categories).build();
    }

    @GET
    @Path("/{id}")
    public Response getCategoryById(@PathParam("id") String id) {
        return categoryService.getCategoryById(id)
            .map(dto -> Response.ok(dto).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Catégorie non trouvée"))
                    .build());
    }

    @POST
    public Response createCategory(@Valid CreateCategoryDto dto) {
        GetCategoryDto created = categoryService.createCategory(dto);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateCategory(@PathParam("id") String id, @Valid CreateCategoryDto dto) {
        return categoryService.updateCategory(id, dto)
            .map(updated -> Response.ok(updated).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Catégorie non trouvée"))
                    .build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCategory(@PathParam("id") String id) {
        categoryService.deleteCategory(id);
        return Response.noContent().build();
    }

    public static class ErrorMessage {
        private String message;

        public ErrorMessage(String message) { this.message = message; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
