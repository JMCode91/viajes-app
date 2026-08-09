package com.app.viajes.model.enums;

/**
 * Enum que clasifica un gasto de un viaje segun su ambito economico.
 * PERSONAL: lo abona un participante y no se reparte entre el grupo.
 * COMPARTIDO: se divide entre los participantes del viaje segun un
 * sistema de reparto definido en {@link TipoDivision}.
 */
public enum TipoGasto {
    PERSONAL,
    COMPARTIDO
}