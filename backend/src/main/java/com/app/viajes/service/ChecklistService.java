package com.app.viajes.service;

import com.app.viajes.dto.ItemChecklistRequestDTO;
import com.app.viajes.dto.ItemChecklistResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de checklist de un viaje.
 */
public interface ChecklistService {

    ItemChecklistResponseDTO crearItem(UUID viajeId, ItemChecklistRequestDTO dto);

    List<ItemChecklistResponseDTO> obtenerItems(UUID viajeId);

    ItemChecklistResponseDTO marcarCompletado(UUID viajeId, UUID itemId, boolean completado);

    void eliminarItem(UUID viajeId, UUID itemId);
}