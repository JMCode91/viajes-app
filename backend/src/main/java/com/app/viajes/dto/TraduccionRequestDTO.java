package com.app.viajes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de entrada para traducir un texto mediante la API de DeepL.
 */
public record TraduccionRequestDTO(
        @NotBlank(message = "El texto a traducir es obligatorio")
        @Size(max = 2000, message = "El texto no puede superar los 2000 caracteres")
        String texto,

        @NotBlank(message = "El idioma destino es obligatorio")
        @Size(min = 2, max = 5, message = "El idioma destino debe ser un codigo ISO valido")
        String idiomaDestino
) {
}