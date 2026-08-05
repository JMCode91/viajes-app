package com.app.viajes.dto;

/**
 * DTO de respuesta con el texto traducido.
 */
public record TraduccionResponseDTO(
        String textoTraducido,
        String idiomaDestino
) {
}