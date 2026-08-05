package com.app.viajes.dto;

import java.util.UUID;

/**
 * DTO de respuesta de una entrada del diccionario.
 */
public record DiccionarioResponseDTO(
        UUID id,
        String fraseEspanol,
        String fraseJapones,
        String pronunciacion,
        String categoria,
        String idioma
) {
}