package com.app.viajes.service.impl;

import com.app.viajes.dto.request.IaPromptRequestDTO;
import com.app.viajes.dto.response.IaItinerarioResponseDTO;
import com.app.viajes.exception.IaException;
import com.app.viajes.service.IaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Implementacion del servicio de generacion de itinerarios con la API de Google Gemini
 * (modelo gemini-1.5-flash). La API key se inyecta desde la propiedad 'gemini.api.key'
 * (debe sobrescribirse via variable de entorno en produccion).
 */
@Service
public class IaServiceImpl implements IaService {

    private static final Logger log = LoggerFactory.getLogger(IaServiceImpl.class);
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";

    private final String apiKey;
    private final RestClient restClient;

    public IaServiceImpl(@Value("${gemini.api.key:}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(GEMINI_ENDPOINT)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public IaItinerarioResponseDTO generarItinerario(IaPromptRequestDTO request) {
        if (!StringUtils.hasText(apiKey)) {
            throw new IaException("El servicio de IA no esta configurado (falta gemini.api.key)");
        }

        try {
            String prompt = construirPrompt(request);
            Map<String, Object> cuerpo = Map.of(
                    "contents", List.of(
                            Map.of("parts", List.of(Map.of("text", prompt)))));

            Map<String, Object> respuesta = restClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .body(Map.class);

            if (respuesta == null) {
                throw new IaException("Gemini no devolvio respuesta");
            }

            List<Map<String, Object>> candidatos =
                    (List<Map<String, Object>>) respuesta.get("candidates");
            if (candidatos == null || candidatos.isEmpty()) {
                throw new IaException("Gemini no devolvio candidatos");
            }

            Map<String, Object> contenido = (Map<String, Object>) candidatos.get(0).get("content");
            if (contenido == null) {
                throw new IaException("Gemini no devolvio contenido");
            }

            List<Map<String, Object>> partes = (List<Map<String, Object>>) contenido.get("parts");
            if (partes == null || partes.isEmpty()) {
                throw new IaException("Gemini no devolvio partes de texto");
            }

            Object texto = partes.get(0).get("text");
            if (texto == null) {
                throw new IaException("Gemini no devolvio texto");
            }

            return new IaItinerarioResponseDTO(limpiarJson(texto.toString()));
        } catch (IaException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error al llamar a la API de Gemini", ex);
            throw new IaException("No se pudo generar el itinerario en este momento", ex);
        }
    }

    private String construirPrompt(IaPromptRequestDTO request) {
        return """
                Eres un experto planificador de viajes. Genera un itinerario de viaje \
                para %s de %d dias con la tematica: %s.

                Responde UNICAMENTE con un JSON valido, sin markdown, sin texto adicional \
                y sin comentarios. El JSON debe ser un array con exactamente %d elementos \
                (uno por dia), cada uno con esta estructura exacta:
                [{"dia": 1, "titulo": "Titulo del dia", "actividades": ["Actividad 1", "Actividad 2"]}]

                Las actividades deben ser realistas, variadas y con horarios aproximados. \
                Incluye siempre al menos 3 actividades por dia.
                """.formatted(
                request.destino(), request.dias(), request.tematica(), request.dias());
    }

    private String limpiarJson(String texto) {
        String limpio = texto.trim();
        if (limpio.startsWith("```")) {
            limpio = limpio.replaceFirst("^```[a-zA-Z]*\\s*", "");
            if (limpio.endsWith("```")) {
                limpio = limpio.substring(0, limpio.length() - 3);
            }
            limpio = limpio.trim();
        }
        return limpio;
    }
}
