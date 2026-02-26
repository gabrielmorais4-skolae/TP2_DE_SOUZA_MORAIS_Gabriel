package com.formation.products.mapper;

import com.formation.products.exception.CategoryNotFoundException;
import com.formation.products.exception.ErrorResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CategoryNotFoundExceptionMapper implements ExceptionMapper<CategoryNotFoundException> {

    @Override
    public Response toResponse(CategoryNotFoundException exception) {
        ErrorResponse error = new ErrorResponse(404, "Not Found", exception.getMessage());

        return Response.status(Response.Status.NOT_FOUND)
                       .entity(error)
                       .build();
    }
}
