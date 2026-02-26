package com.formation.products.mapper;

import com.formation.products.exception.CategoryNotEmptyException;
import com.formation.products.exception.ErrorResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CategoryNotEmptyExceptionMapper implements ExceptionMapper<CategoryNotEmptyException> {

    @Override
    public Response toResponse(CategoryNotEmptyException exception) {
        ErrorResponse error = new ErrorResponse(409, "Conflict", exception.getMessage());

        return Response.status(Response.Status.CONFLICT)
                       .entity(error)
                       .build();
    }
}
