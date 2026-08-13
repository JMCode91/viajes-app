package com.app.viajes.service;

import com.app.viajes.dto.TraduccionRequestDTO;
import com.app.viajes.dto.TraduccionResponseDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.dto.request.ActividadIaDTO;
import com.app.viajes.dto.request.IaGuardarViajeRequestDTO;
import com.app.viajes.dto.request.IaPromptRequestDTO;
import com.app.viajes.dto.request.IaRegenerarActividadRequestDTO;
import com.app.viajes.dto.response.IaItinerarioResponseDTO;

import java.util.UUID;

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

    /**
     * Persiste en la base de datos un itinerario generado por IA como un
     * viaje nuevo (titulo "Viaje a {destino} (IA)") asociado al usuario
     * autenticado como PROPIETARIO, creando una actividad por cada item.
     *
     * @param request  destino e itinerario dia a dia con sus actividades
     * @param usuarioId id del usuario autenticado que crea el viaje
     * @return el viaje creado con sus datos y participantes
     */
    ViajeResponseDTO guardarViajeGenerado(IaGuardarViajeRequestDTO request, UUID usuarioId);

    /**
     * Regenera (sustituye) una unica actividad de un itinerario generado por IA
     * para una franja horaria concreta, pidiendo a Gemini una alternativa
     * totalmente diferente.
     *
     * @param request contexto del viaje (destino y tematica), la hora de la
     *                franja y la actividad actual que se quiere cambiar
     * @return la nueva actividad generada como alternativa
     */
    ActividadIaDTO regenerarActividad(IaRegenerarActividadRequestDTO request);

    /**
     * Traduce un texto al idioma destino indicado usando la API de Google Gemini.
     * Fuerza a Gemini a devolver un JSON estructurado con el texto traducido y,
     * en el caso del japones, la pronunciacion fonetica (Romaji).
     *
     * @param request texto a traducir e idioma destino (ej. "ja" o "en")
     * @return texto traducido y, si aplica, su pronunciacion fonetica
     */
    TraduccionResponseDTO traducirTexto(TraduccionRequestDTO request);
}
