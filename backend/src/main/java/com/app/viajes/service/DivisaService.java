package com.app.viajes.service;

import com.app.viajes.dto.response.DivisaResponseDTO;

import java.util.Map;

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

    /**
     * Devuelve el catalogo de monedas soportadas por Frankfurter.
     *
     * @return mapa con el codigo ISO de cada moneda como clave y su nombre en
     *         ingles como valor (ej. {@code {"AUD": "Australian Dollar"}})
     */
    Map<String, String> obtenerMonedas();
}
