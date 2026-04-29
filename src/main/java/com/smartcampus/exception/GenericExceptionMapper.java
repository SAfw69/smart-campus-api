package com.smartcampus.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GenericExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        // Log the actual exception for internal debugging but return generic response to client
        exception.printStackTrace(); 
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR) // 500
                .entity("{\"error\": \"An unexpected internal server error occurred.\"}")
                .type("application/json")
                .build();
    }
}
