package com.formation.products.mapper;

import com.formation.products.exception.CategoryNotEmptyException;
import com.formation.products.exception.CategoryNotFoundException;
import com.formation.products.exception.DuplicateProductException;
import com.formation.products.exception.ErrorResponse;
import com.formation.products.exception.InsufficientStockException;
import com.formation.products.exception.ProductNotFoundException;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception exception) {
        Throwable cause = exception;
        while (cause != null) {
            if (cause instanceof WebApplicationException e) {
                return e.getResponse();
            }
            if (cause instanceof ProductNotFoundException e) {
                return new NotFoundExceptionMapper().toResponse(e);
            }
            if (cause instanceof CategoryNotFoundException e) {
                return new CategoryNotFoundExceptionMapper().toResponse(e);
            }
            if (cause instanceof DuplicateProductException e) {
                return new ConflictExceptionMapper().toResponse(e);
            }
            if (cause instanceof CategoryNotEmptyException e) {
                return new CategoryNotEmptyExceptionMapper().toResponse(e);
            }
            if (cause instanceof InsufficientStockException e) {
                return new InsufficientStockExceptionMapper().toResponse(e);
            }
            if (cause instanceof ConstraintViolationException e) {
                return new ValidationExceptionMapper().toResponse(e);
            }
            cause = cause.getCause();
        }

        ErrorResponse error = new ErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred"
        );

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(error)
                       .build();
    }
}
