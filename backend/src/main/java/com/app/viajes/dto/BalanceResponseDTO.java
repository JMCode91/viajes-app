package com.app.viajes.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resumen financiero de un viaje para el usuario autenticado.
 * Incluye los presupuestos (global e individual), lo gastado por el
 * grupo y por el usuario, y la lista simplificada de deudas pendientes
 * entre los participantes ("Usuario A le debe X a Usuario B").
 */
public record BalanceResponseDTO(
        BigDecimal presupuestoGlobal,
        BigDecimal totalGastadoGrupo,
        BigDecimal presupuestoIndividualUsuario,
        BigDecimal totalGastadoUsuario,
        List<DeudaResumenDTO> deudas
) {
}