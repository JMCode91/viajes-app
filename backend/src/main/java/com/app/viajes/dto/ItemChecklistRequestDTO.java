package com.app.viajes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creacion de un item de checklist.
 */
public record ItemChecklistRequestDTO(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 200, message = "El nombre no puede superar los 200 caracteres")
        String nombre
) {
}