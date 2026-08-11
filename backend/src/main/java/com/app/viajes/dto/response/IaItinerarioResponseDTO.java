package com.app.viajes.dto.response;

/**
 * DTO de respuesta con el itinerario generado por la IA en formato JSON crudo.
 * El frontend es responsable de parsear el JSON (array de dias con actividades).
 */
public record IaItinerarioResponseDTO(
        String rawJson
) {
}
