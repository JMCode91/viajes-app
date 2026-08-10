package com.app.viajes.service;

import com.app.viajes.dto.BalanceResponseDTO;
import com.app.viajes.dto.GastoRequestDTO;
import com.app.viajes.dto.GastoResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de gastos y balances de un viaje.
 */
public interface GastoService {

    GastoResponseDTO crearGasto(UUID viajeId, GastoRequestDTO dto);

    List<GastoResponseDTO> obtenerGastos(UUID viajeId);

    GastoResponseDTO actualizarGasto(UUID viajeId, UUID gastoId, GastoRequestDTO dto);

    void eliminarGasto(UUID viajeId, UUID gastoId);

    /**
     * Resumen financiero del viaje para el usuario autenticado:
     * presupuestos, total gastado y deudas simplificadas.
     */
    BalanceResponseDTO obtenerBalance(UUID viajeId);
}