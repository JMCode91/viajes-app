package com.app.viajes.service.impl;

import com.app.viajes.dto.ViajeRequestDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.dto.request.DiaIaDTO;
import com.app.viajes.dto.request.IaGuardarViajeRequestDTO;
import com.app.viajes.dto.request.IaPromptRequestDTO;
import com.app.viajes.dto.response.IaItinerarioResponseDTO;
import com.app.viajes.exception.IaException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.Actividad;
import com.app.viajes.model.Viaje;
import com.app.viajes.model.enums.TipoActividad;
import com.app.viajes.repository.ActividadRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.IaService;
import com.app.viajes.service.ViajeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementacion del servicio de generacion de itinerarios con la API de Google Gemini
 * (modelo gemini-3.6-flash). La API key se inyecta desde la propiedad 'gemini.api.key'
 * (debe sobrescribirse via variable de entorno en produccion).
 */
@Service
public class IaServiceImpl implements IaService {

    private static final Logger log = LoggerFactory.getLogger(IaServiceImpl.class);
    private static final String GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent";

    private final String apiKey;
    private final RestClient restClient;
    private final ViajeService viajeService;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;

    public IaServiceImpl(@Value("${gemini.api.key:}") String apiKey,
                         ViajeService viajeService,
                         ViajeRepository viajeRepository,
                         UsuarioRepository usuarioRepository,
                         ActividadRepository actividadRepository) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(GEMINI_ENDPOINT)
                .build();
        this.viajeService = viajeService;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.actividadRepository = actividadRepository;
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

            log.info("Llamando a la API de Gemini con la API key enmascarada: {}",
                    enmascararApiKey(apiKey));

            Map<String, Object> respuesta = restClient.post()
                    .uri(uriBuilder -> uriBuilder.queryParam("key", apiKey).build())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(cuerpo)
                    .retrieve()
                    .body(Map.class);

            if (respuesta == null) {
                log.error("Gemini devolvio una respuesta vacia (body null)");
                throw new IaException("Gemini no devolvio respuesta");
            }

            List<Map<String, Object>> candidatos =
                    (List<Map<String, Object>>) respuesta.get("candidates");
            if (candidatos == null || candidatos.isEmpty()) {
                log.error("Gemini no devolvio candidatos. Cuerpo recibido: {}", respuesta);
                throw new IaException("Gemini no devolvio candidatos");
            }

            Map<String, Object> contenido = (Map<String, Object>) candidatos.get(0).get("content");
            if (contenido == null) {
                log.error("Gemini no devolvio contenido. Cuerpo recibido: {}", respuesta);
                throw new IaException("Gemini no devolvio contenido");
            }

            List<Map<String, Object>> partes = (List<Map<String, Object>>) contenido.get("parts");
            if (partes == null || partes.isEmpty()) {
                log.error("Gemini no devolvio partes de texto. Cuerpo recibido: {}", respuesta);
                throw new IaException("Gemini no devolvio partes de texto");
            }

            Object texto = partes.get(0).get("text");
            if (texto == null) {
                log.error("Gemini no devolvio texto. Cuerpo recibido: {}", respuesta);
                throw new IaException("Gemini no devolvio texto");
            }

            return new IaItinerarioResponseDTO(limpiarJson(texto.toString()));
        } catch (IaException ex) {
            throw ex;
        } catch (HttpStatusCodeException ex) {
            log.error("Gemini devolvio un error HTTP {} ({}) al llamar a la API: {}",
                    ex.getStatusCode().value(),
                    ex.getStatusCode(),
                    ex.getResponseBodyAsString());
            throw new IaException("No se pudo generar el itinerario en este momento", ex);
        } catch (Exception ex) {
            log.error("Error al llamar a la API de Gemini. Mensaje exacto: {}",
                    ex.getMessage(), ex);
            throw new IaException("No se pudo generar el itinerario en este momento", ex);
        }
    }

    @Override
    @Transactional
    public ViajeResponseDTO guardarViajeGenerado(IaGuardarViajeRequestDTO request, UUID usuarioId) {
        // Verifica que el usuario autenticado exista en la base de datos.
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));

        LocalDate fechaInicio = LocalDate.now();
        int numDias = request.itinerario().size();
        LocalDate fechaFin = fechaInicio.plusDays(numDias - 1L);

        // Crea el viaje y asocia al usuario autenticado como PROPIETARIO.
        // ViajeService.crearViaje resuelve el usuario desde el SecurityContext
        // (mismo usuario autenticado que se validó arriba por su id).
        ViajeResponseDTO viajeCreado = viajeService.crearViaje(new ViajeRequestDTO(
                "Viaje a " + request.destino() + " (IA)",
                fechaInicio,
                fechaFin,
                BigDecimal.ZERO));

        Viaje viaje = viajeRepository.findById(viajeCreado.id())
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        // Guarda una actividad por cada item del itinerario generado.
        for (DiaIaDTO dia : request.itinerario()) {
            LocalDate fecha = fechaInicio.plusDays(dia.dia() - 1L);
            for (String texto : dia.actividades()) {
                Actividad actividad = new Actividad();
                actividad.setViaje(viaje);
                actividad.setTitulo(texto);
                actividad.setTipoActividad(TipoActividad.OTRO);
                actividad.setFechaHoraInicio(fecha.atStartOfDay());
                actividad.setFechaHoraFin(fecha.atTime(23, 59));
                actividad.setNotas(dia.titulo());
                actividadRepository.save(actividad);
            }
        }

        return viajeCreado;
    }

    private String enmascararApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "(vacia o null)";
        }
        if (apiKey.length() < 8) {
            return apiKey.charAt(0) + "...";
        }
        return apiKey.substring(0, 4) + "...";
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
