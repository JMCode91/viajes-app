package com.app.viajes.service;

import com.app.viajes.dto.ActividadRequestDTO;
import com.app.viajes.dto.ActividadResponseDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Servicio de actividades de un viaje. Cada operacion valida que el
 * usuario autenticado pertenece al viaje antes de operar.
 */
public interface ActividadService {

    ActividadResponseDTO crearActividad(UUID viajeId, ActividadRequestDTO dto);

    List<ActividadResponseDTO> obtenerActividadesPorViaje(UUID viajeId);

    List<ActividadResponseDTO> obtenerTimelinePorFecha(UUID viajeId, LocalDate fecha);

    void eliminarActividad(UUID viajeId, UUID actividadId);

    ActividadResponseDTO guardarUrlDocumento(UUID viajeId, UUID actividadId, String urlDocumento);
}