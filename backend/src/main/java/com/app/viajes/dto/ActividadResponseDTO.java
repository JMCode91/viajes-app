package com.app.viajes.dto;

import com.app.viajes.model.enums.TipoActividad;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta con los datos de una actividad, su viaje y los
 * documentos asociados (relacion 1:N).
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
        String direccion,
        List<DocumentoDTO> documentos
) {
}