package com.app.viajes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para cambiar la contrasena del usuario autenticado.
 */
public record CambiarPasswordRequest(
        @NotBlank(message = "La contrasena actual es obligatoria")
        String passwordActual,

        @NotBlank(message = "La nueva contrasena es obligatoria")
        @Size(min = 6, max = 255, message = "La nueva contrasena debe tener entre 6 y 255 caracteres")
        String nuevaPassword
) {
}