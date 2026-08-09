package com.app.viajes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para la creacion de un album de fotos de un viaje.
 */
public record AlbumRequestDTO(
        @NotBlank(message = "El nombre del album es obligatorio")
        @Size(max = 200, message = "El nombre del album no puede superar los 200 caracteres")
        String nombre
) {
}