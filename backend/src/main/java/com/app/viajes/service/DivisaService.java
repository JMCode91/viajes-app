package com.app.viajes.service;

import com.app.viajes.dto.response.DivisaResponseDTO;

/**
 * Servicio de conversion de divisas usando la API publica de Frankfurter.
 */
public interface DivisaService {

    /**
     * Convierte un monto entre dos monedas (codigos ISO 4217).
     *
     * @param monto cantidad a convertir
     * @param from  codigo ISO de la moneda origen
     * @param to    codigo ISO de la moneda destino
     * @return resultado de la conversion
     */
    DivisaResponseDTO convertir(Double monto, String from, String to);
}
