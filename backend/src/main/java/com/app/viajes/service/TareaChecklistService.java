package com.app.viajes.service;

import com.app.viajes.dto.TareaChecklistRequestDTO;
import com.app.viajes.dto.TareaChecklistResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de checklist de un viaje (tareas).
 * Todas las operaciones validan que el usuario autenticado pertenezca
 * al viaje y aplican el filtro de visibilidad de listas privadas.
 */
public interface TareaChecklistService {

    List<TareaChecklistResponseDTO> obtenerTareas(UUID viajeId);

    TareaChecklistResponseDTO crearTarea(UUID viajeId, TareaChecklistRequestDTO dto);

    TareaChecklistResponseDTO cambiarEstado(UUID viajeId, Long tareaId, boolean completada);

    TareaChecklistResponseDTO asignarUsuario(UUID viajeId, Long tareaId, UUID asignadoId);

    void eliminarTarea(UUID viajeId, Long tareaId);
}