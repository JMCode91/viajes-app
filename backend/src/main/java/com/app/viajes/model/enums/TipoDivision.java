package com.app.viajes.model.enums;

/**
 * Enum de los sistemas de division de un gasto compartido.
 * PARTES_IGUALES: el importe se reparte a partes iguales entre los
 *   participantes involucrados.
 * PORCENTAJE: cada participante asume un porcentaje del importe
 *   (covertura reflejada en {@code porcentajeAsignado} de la deuda).
 * DEUDA_TERCERO: la deuda se registra contra un tercero involucrado en
 *   el gasto (no necesariamente el pagador).
 */
public enum TipoDivision {
    PARTES_IGUALES,
    PORCENTAJE,
    DEUDA_TERCERO
}