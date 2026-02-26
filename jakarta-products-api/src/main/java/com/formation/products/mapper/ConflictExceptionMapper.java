package com.formation.products.mapper;

import com.formation.products.exception.DuplicateProductException;
import com.formation.products.exception.ErrorResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ConflictExceptionMapper implements ExceptionMapper<DuplicateProductException> {

    @Override
    public Response toResponse(DuplicateProductException exception) {
        ErrorResponse error = new ErrorResponse(409, "Conflict", exception.getMessage());

        return Response.status(Response.Status.CONFLICT)
                       .entity(error)
                       .build();
    }
}
