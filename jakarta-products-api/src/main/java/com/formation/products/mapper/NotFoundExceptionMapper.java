package com.formation.products.mapper;

import com.formation.products.exception.ErrorResponse;
import com.formation.products.exception.ProductNotFoundException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<ProductNotFoundException> {

    @Override
    public Response toResponse(ProductNotFoundException exception) {
        ErrorResponse error = new ErrorResponse(404, "Not Found", exception.getMessage());

        return Response.status(Response.Status.NOT_FOUND)
                       .entity(error)
                       .build();
    }
}
