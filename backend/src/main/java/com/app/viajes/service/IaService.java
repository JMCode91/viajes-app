package com.app.viajes.service;

import com.app.viajes.dto.request.IaPromptRequestDTO;
import com.app.viajes.dto.response.IaItinerarioResponseDTO;

/**
 * Servicio de generacion de itinerarios de viaje usando la API de Google Gemini.
 */
public interface IaService {

    /**
     * Genera un itinerario de viaje con IA a partir de un prompt.
     *
     * @param request destino, numero de dias y tematica del viaje
     * @return itinerario generado en formato JSON crudo (array de dias)
     */
    IaItinerarioResponseDTO generarItinerario(IaPromptRequestDTO request);
}
