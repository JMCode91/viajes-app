package com.app.viajes.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Resumen de una deuda neta entre dos participantes: el deudor le debe
 * {@code importe} al acreedor. Resulta de simplificar los {@code DeudaGasto}
 * del viaje (se compensan deudas cruzadas entre la misma pareja).
 */
public record DeudaResumenDTO(
        UUID deudorId,
        String deudorNombre,
        UUID acreedorId,
        String acreedorNombre,
        BigDecimal importe
) {
}