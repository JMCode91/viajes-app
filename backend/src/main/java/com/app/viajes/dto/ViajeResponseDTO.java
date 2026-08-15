package com.app.viajes.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de un viaje, el rol del usuario
 * autenticado dentro del mismo, la foto de portada, la lista de
 * participantes con sus roles y las paradas (ciudades) de la ruta
 * multidestino.
 */
public record ViajeResponseDTO(
        UUID id,
        String titulo,
        LocalDate fechaInicio,
        LocalDate fechaFin,
        BigDecimal presupuestoBase,
        BigDecimal presupuestoGlobal,
        String rolEnViaje,
        String fotoPortada,
        List<ParticipanteResponseDTO> participantes,
        List<ParadaViajeDTO> paradas
) {
}