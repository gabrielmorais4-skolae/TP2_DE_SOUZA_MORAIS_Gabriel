package com.formation.products.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.formation.products.dtos.request.CreateOrderDto;
import com.formation.products.dtos.response.GetOrderDto;
import com.formation.products.dtos.response.GetOrderItemDto;
import com.formation.products.dtos.response.MostOrderedProduct;
import com.formation.products.model.Order;
import com.formation.products.model.OrderItem;
import com.formation.products.model.OrderStatus;
import com.formation.products.service.OrderService;

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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Tag(name = "Orders")
@Path("/orders")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderController {

    @Inject
    private OrderService orderService;

    @POST
    public Response createOrder(@Valid CreateOrderDto dto) {
        Order order = orderService.createOrder(
            dto.getCustomerName(),
            dto.getCustomerEmail(),
            dto.getProductsAndQuantities()
        );
        return Response.status(Response.Status.CREATED).entity(toDto(order)).build();
    }

    @GET
    public Response getAllOrders(@QueryParam("status") String status,
                                 @QueryParam("email") String email) {
        List<Order> orders;
        if (status != null && !status.isBlank()) {
            orders = orderService.getOrdersByStatus(OrderStatus.valueOf(status.toUpperCase()));
        } else if (email != null && !email.isBlank()) {
            orders = orderService.getOrdersByEmail(email);
        } else {
            orders = orderService.getOrdersWithItems();
        }
        return Response.ok(orders.stream().map(this::toDto).collect(Collectors.toList())).build();
    }

    @GET
    @Path("/{id}")
    public Response getOrderById(@PathParam("id") Long id) {
        return orderService.getOrderById(id)
            .map(o -> Response.ok(toDto(o)).build())
            .orElse(Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Order not found")).build());
    }

    @PUT
    @Path("/{id}/status")
    public Response updateStatus(@PathParam("id") Long id, StatusUpdateRequest request) {
        orderService.updateOrderStatus(id, request.getStatus());
        return orderService.getOrderById(id)
            .map(o -> Response.ok(toDto(o)).build())
            .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteOrder(@PathParam("id") Long id) {
        orderService.deleteOrder(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/stats/total-revenue")
    public Response totalRevenue() {
        return Response.ok(orderService.getTotalRevenue()).build();
    }

    @GET
    @Path("/stats/count-by-status")
    public Response countByStatus() {
        return Response.ok(orderService.countByStatus()).build();
    }

    @GET
    @Path("/stats/most-ordered-products")
    public Response mostOrderedProducts(@QueryParam("limit") @jakarta.ws.rs.DefaultValue("5") int limit) {
        List<MostOrderedProduct> result = orderService.findMostOrderedProducts(limit);
        return Response.ok(result).build();
    }

    private GetOrderDto toDto(Order order) {
        GetOrderDto dto = new GetOrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setStatus(order.getStatus());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setItems(order.getItems().stream().map(this::toItemDto).collect(Collectors.toList()));
        return dto;
    }

    private GetOrderItemDto toItemDto(OrderItem item) {
        GetOrderItemDto dto = new GetOrderItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        if (item.getProduct() != null) {
            dto.setProductId(item.getProduct().getId());
            dto.setProductName(item.getProduct().getName());
        }
        return dto;
    }

    public static class StatusUpdateRequest {
        private OrderStatus status;
        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
    }

    public static class ErrorMessage {
        private String message;
        public ErrorMessage(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
