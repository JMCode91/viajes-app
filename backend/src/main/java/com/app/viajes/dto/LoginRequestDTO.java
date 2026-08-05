package com.app.viajes.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para el inicio de sesion.
 */
public record LoginRequestDTO(
        @NotBlank(message = "El email es obligatorio")
        String email,

        @NotBlank(message = "La contrasena es obligatoria")
        String password
) {
}
