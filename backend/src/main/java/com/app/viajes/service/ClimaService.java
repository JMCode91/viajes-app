package com.app.viajes.service;

import com.app.viajes.dto.response.ClimaResponseDTO;

import java.time.LocalDate;

/**
 * Servicio de clima: devuelve la prevision meteorologica para una ubicacion y fecha.
 */
public interface ClimaService {

    /**
     * Obtiene el clima para una ubicacion y una fecha.
     *
     * @param latitud  latitud de la ubicacion
     * @param longitud longitud de la ubicacion
     * @param fecha    fecha a consultar (futura o pasada)
     * @return prevision del clima (fallback historico si la fecha queda fuera
     *         de los proximos 14 dias)
     */
    ClimaResponseDTO obtenerClima(Double latitud, Double longitud, LocalDate fecha);
}
