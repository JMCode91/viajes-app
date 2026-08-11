package com.app.viajes.service.impl;

import com.app.viajes.dto.ViajeRequestDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.dto.request.ActividadIaDTO;
import com.app.viajes.dto.request.DiaIaDTO;
import com.app.viajes.dto.request.IaGuardarViajeRequestDTO;
import com.app.viajes.dto.request.IaPromptRequestDTO;
import com.app.viajes.dto.response.IaItinerarioResponseDTO;
import com.app.viajes.exception.DatosInvalidosException;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
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
        validarFechas(request.fechaInicio(), request.fechaFin());

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

        validarFechas(request.fechaInicio(), request.fechaFin());

        LocalDate fechaInicio = request.fechaInicio();
        LocalDate fechaFin = request.fechaFin();

        // Crea el viaje con las fechas reales del viajero y asocia al usuario
        // autenticado como PROPIETARIO. ViajeService.crearViaje resuelve el usuario
        // desde el SecurityContext (mismo usuario autenticado que se valido arriba).
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
            for (ActividadIaDTO item : dia.actividades()) {
                Actividad actividad = new Actividad();
                actividad.setViaje(viaje);
                actividad.setTitulo(item.titulo());
                actividad.setTipoActividad(TipoActividad.OTRO);
                LocalDateTime inicio = fecha.atTime(parseHoraActividad(item.hora()));
                actividad.setFechaHoraInicio(inicio);
                actividad.setFechaHoraFin(inicio.plusHours(1));
                // El lugar exacto (nombre del sitio, ciudad y pais) alimenta el
                // campo 'direccion', que el frontend geolocaliza en el mapa.
                actividad.setDireccion(item.lugar());
                // Si Gemini devuelve coordenadas, se persisten para que el mapa
                // del frontend pueda renderizar los marcadores sin geocodificar.
                if (item.latitud() != null) {
                    actividad.setLatitud(item.latitud());
                }
                if (item.longitud() != null) {
                    actividad.setLongitud(item.longitud());
                }
                // Si Gemini devuelve una valoracion estimada, se persiste para que
                // el frontend pueda mostrar las estrellas en las tarjetas.
                if (item.rating() != null) {
                    actividad.setRating(item.rating());
                }
                actividad.setNotas(item.descripcion());
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
        long numDias = ChronoUnit.DAYS.between(request.fechaInicio(), request.fechaFin()) + 1;
        String detallesAdicionales = request.detallesAdicionales();
        String instruccionDetalles = "DETALLES IMPORTANTES DEL VIAJERO: "
                + (detallesAdicionales == null || detallesAdicionales.isBlank()
                        ? ""
                        : detallesAdicionales.trim())
                + ". (Si esta vacio, ignorarlo)";

        return """
                Eres un experto planificador de viajes. Genera un itinerario de viaje \
                para %s desde el %s hasta el %s (un total de %d dias) con la tematica: %s.

                %s

                ACTUA COMO UN GEOCODER: Es OBLIGATORIO incluir la latitud y longitud GPS \
                real y precisa de cada lugar en grados decimales (por ejemplo 35.6585, 139.7454).

                Responde UNICAMENTE con un JSON valido, sin markdown, sin texto adicional \
                y sin comentarios. El JSON debe ser un array con exactamente %d elementos \
                (uno por dia), cada uno con esta estructura exacta:
                [{"dia": 1, "titulo": "Titulo del dia", "actividades": [{"hora": "09:00", "titulo": "Titulo de la actividad", "lugar": "Nombre exacto del sitio, Ciudad", "latitud": 35.6585, "longitud": 139.7454, "rating": 4.7, "descripcion": "Descripcion breve de la actividad"}]}]

                El campo 'hora' debe tener formato HH:MM (por ejemplo "09:30"). El campo \
                'lugar' debe ser el nombre exacto del sitio seguido de la ciudad y el pais \
                para que pueda geolocalizarse en el mapa. Los campos 'latitud' y 'longitud' \
                deben ser numericos y corresponder a las coordenadas GPS reales del lugar. \
                Incluye un campo numerico para la valoracion (ej. 'rating') estimado entre \
                1.0 y 5.0 basado en la popularidad real del lugar. Las actividades deben ser \
                realistas, variadas y con horarios aproximados. Incluye siempre al menos \
                3 actividades por dia.
                """.formatted(
                request.destino(),
                request.fechaInicio(),
                request.fechaFin(),
                numDias,
                request.tematica(),
                instruccionDetalles,
                numDias);
    }

    private void validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio != null && fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new DatosInvalidosException(
                    "La fecha de fin no puede ser anterior a la fecha de inicio");
        }
    }

    private LocalTime parseHoraActividad(String hora) {
        try {
            return LocalTime.parse(hora);
        } catch (DateTimeParseException | NullPointerException ex) {
            log.warn("Hora invalida '{}' en el itinerario generado por IA; se usa 09:00", hora);
            return LocalTime.of(9, 0);
        }
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
