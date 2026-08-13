package com.app.viajes.dto;

/**
 * DTO de respuesta con el texto traducido y, en idiomas con sistema
 * de escritura no latino (p.ej. japones), su pronunciacion fonetica (Romaji).
 */
public record TraduccionResponseDTO(
        String textoTraducido,
        String pronunciacion
) {
}