package com.christian.escuela.dto;

public record ErrorResponse(
        int codigo,
        String mensaje
) {}