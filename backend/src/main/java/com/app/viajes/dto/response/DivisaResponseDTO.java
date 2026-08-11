package com.app.viajes.dto.response;

/**
 * DTO de respuesta con el resultado de una conversion de divisas.
 */
public record DivisaResponseDTO(
        Double montoOriginal,
        String monedaOriginal,
        Double montoConvertido,
        String monedaDestino,
        Double ratioCambio,
        String fechaActualizacion
) {
}
