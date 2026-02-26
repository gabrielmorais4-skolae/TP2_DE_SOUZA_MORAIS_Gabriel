package com.formation.products.controller;

import java.net.URI;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.formation.products.dtos.request.CreateSupplierDto;
import com.formation.products.dtos.response.GetSupplierDto;
import com.formation.products.service.SupplierService;

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

@Tag(name = "Suppliers")
@Path("/suppliers")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SupplierController {

    @Inject
    private SupplierService supplierService;

    @Context
    UriInfo uriInfo;

    @GET
    public Response getAllSuppliers() {
        List<GetSupplierDto> suppliers = supplierService.getAllSuppliers();
        return Response.ok(suppliers).build();
    }

    @GET
    @Path("/{id}")
    public Response getSupplierById(@PathParam("id") String id) {
        return supplierService.getSupplierById(id)
            .map(dto -> Response.ok(dto).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Fournisseur non trouvé"))
                    .build());
    }

    @POST
    public Response createSupplier(@Valid CreateSupplierDto dto) {
        GetSupplierDto created = supplierService.createSupplier(dto);
        URI location = uriInfo.getAbsolutePathBuilder().path(created.getId()).build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateSupplier(@PathParam("id") String id, @Valid CreateSupplierDto dto) {
        return supplierService.updateSupplier(id, dto)
            .map(updated -> Response.ok(updated).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Fournisseur non trouvé"))
                    .build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteSupplier(@PathParam("id") String id) {
        supplierService.deleteSupplier(id);
        return Response.noContent().build();
    }

    public static class ErrorMessage {
        private String message;

        public ErrorMessage(String message) { this.message = message; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
