package com.app.viajes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de un viaje, el rol del usuario
 * autenticado dentro del mismo, la foto de portada y la lista de
 * participantes con sus roles.
 */
public record ViajeResponseDTO(
        UUID id,
        String titulo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal presupuestoBase,
        String rolEnViaje,
        String fotoPortada,
        List<ParticipanteResponseDTO> participantes
) {
}