package com.formation.products.mapper;

import com.formation.products.exception.ErrorResponse;
import com.formation.products.exception.InsufficientStockException;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InsufficientStockExceptionMapper implements ExceptionMapper<InsufficientStockException> {

    @Override
    public Response toResponse(InsufficientStockException exception) {
        ErrorResponse error = new ErrorResponse(400, "Bad Request", exception.getMessage());

        return Response.status(Response.Status.BAD_REQUEST)
                       .entity(error)
                       .build();
    }
}
