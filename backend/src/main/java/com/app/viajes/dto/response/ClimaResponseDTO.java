package com.app.viajes.dto.response;

/**
 * DTO de respuesta con la prevision del clima para una ubicacion y fecha concreta.
 */
public record ClimaResponseDTO(
        Double tempMax,
        Double tempMin,
        String condicion,
        String icono,
        Boolean esHistorico
) {
}
