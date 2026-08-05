package com.app.viajes.dto;

import com.app.viajes.model.enums.TipoActividad;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de una actividad y su viaje.
 */
public record ActividadResponseDTO(
        UUID id,
        UUID viajeId,
        String titulo,
        String descripcion,
        TipoActividad tipoActividad,
        LocalDateTime fechaHoraInicio,
        LocalDateTime fechaHoraFin,
        Double latitud,
        Double longitud,
        String urlDocumento
) {
}