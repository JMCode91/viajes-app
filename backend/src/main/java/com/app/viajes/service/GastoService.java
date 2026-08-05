package com.app.viajes.service;

import com.app.viajes.dto.GastoRequestDTO;
import com.app.viajes.dto.GastoResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de gastos de un viaje.
 */
public interface GastoService {

    GastoResponseDTO crearGasto(UUID viajeId, GastoRequestDTO dto);

    List<GastoResponseDTO> obtenerGastos(UUID viajeId);

    void eliminarGasto(UUID viajeId, UUID gastoId);
}