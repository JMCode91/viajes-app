package com.app.viajes.service.impl;

import com.app.viajes.dto.response.ClimaResponseDTO;
import com.app.viajes.service.ClimaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Implementacion del servicio de clima usando la API publica de Open-Meteo
 * (sin necesidad de API key). Si la fecha queda fuera de los proximos 14 dias
 * se devuelve un fallback historico generico (condiciones mensuales de Japon).
 *
 * <p>Disena resiliente: fail-fast con un timeout estricto y corto, y respuesta
 * segura ("Clima no disponible") ante cualquier fallo de la API externa para
 * que el frontend oculte la seccion del tiempo sin romperse.</p>
 */
@Service
public class ClimaServiceImpl implements ClimaService {

    private static final Logger log = LoggerFactory.getLogger(ClimaServiceImpl.class);
    private static final String OPEN_METEO_ENDPOINT = "https://api.open-meteo.com";
    private static final long PLAZO_PRONOSTICO_DIAS = 14L;
    private static final int TIMEOUT_MILISEGUNDOS = 3000;

    private final RestClient restClient;

    public ClimaServiceImpl() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MILISEGUNDOS);
        requestFactory.setReadTimeout(TIMEOUT_MILISEGUNDOS);

        this.restClient = RestClient.builder()
                .baseUrl(OPEN_METEO_ENDPOINT)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public ClimaResponseDTO obtenerClima(Double latitud, Double longitud, LocalDate fecha) {
        LocalDate hoy = LocalDate.now();
        boolean dentroDePlazo = !fecha.isBefore(hoy)
                && fecha.isBefore(hoy.plusDays(PLAZO_PRONOSTICO_DIAS));

        if (!dentroDePlazo) {
            return fallbackHistorico(fecha);
        }

        try {
            Map<String, Object> respuesta = restClient.get()
                    .uri(uriBuilder -> construirUrlClima(uriBuilder, latitud, longitud, fecha))
                    .retrieve()
                    .body(Map.class);

            return parsearRespuestaClima(respuesta, fecha);
        } catch (Exception ex) {
            // Timeout, error de conexion o 5xx de la API externa: se devuelve
            // una respuesta segura para que el frontend no explote y simplemente
            // oculte la seccion del tiempo.
            log.error("Fallo critico en la API externa de Clima: {}", ex.getMessage(), ex);
            return climaNoDisponible();
        }
    }

    /**
     * Construye la URL del pronostico diario ({@code /v1/forecast}) con los
     * parametros de ubicacion y la ventana de un solo dia.
     */
    private URI construirUrlClima(UriBuilder uriBuilder,
                                  Double latitud,
                                  Double longitud,
                                  LocalDate fecha) {
        return uriBuilder.path("/v1/forecast")
                .queryParam("latitude", latitud)
                .queryParam("longitude", longitud)
                .queryParam("daily", "weathercode,temperature_2m_max,temperature_2m_min")
                .queryParam("timezone", "auto")
                .queryParam("start_date", fecha)
                .queryParam("end_date", fecha)
                .build();
    }

    /**
     * Parsea la respuesta JSON de Open-Meteo. Si el payload no trae los datos
     * diarios esperados, degrada al fallback historico en lugar de fallar.
     */
    @SuppressWarnings("unchecked")
    private ClimaResponseDTO parsearRespuestaClima(Map<String, Object> respuesta, LocalDate fecha) {
        if (respuesta == null) {
            return fallbackHistorico(fecha);
        }

        Map<String, Object> diario = (Map<String, Object>) respuesta.get("daily");
        if (diario == null) {
            return fallbackHistorico(fecha);
        }

        List<Number> codigos = (List<Number>) diario.get("weathercode");
        List<Number> tempMax = (List<Number>) diario.get("temperature_2m_max");
        List<Number> tempMin = (List<Number>) diario.get("temperature_2m_min");

        if (codigos == null || codigos.isEmpty()
                || tempMax == null || tempMax.isEmpty()
                || tempMin == null || tempMin.isEmpty()) {
            return fallbackHistorico(fecha);
        }

        CondicionClima condicion = traducirCondicion(codigos.get(0).intValue());
        return new ClimaResponseDTO(
                tempMax.get(0).doubleValue(),
                tempMin.get(0).doubleValue(),
                condicion.texto(),
                condicion.icono(),
                false);
    }

    /**
     * Respuesta segura usada cuando la API externa falla (timeout, conexion o
     * 5xx). Con temperaturas null y el mensaje "Clima no disponible" el
     * frontend simplemente oculta la seccion del tiempo.
     */
    private ClimaResponseDTO climaNoDisponible() {
        return new ClimaResponseDTO(null, null, "Clima no disponible", "nube", false);
    }

    private ClimaResponseDTO fallbackHistorico(LocalDate fecha) {
        log.info("Fecha {} fuera del plazo de pronostico; se devuelve fallback historico", fecha);
        return new ClimaResponseDTO(28.0, 20.0, "Historico Mensual", "historial", true);
    }

    private CondicionClima traducirCondicion(int codigo) {
        return switch (codigo) {
            case 0 -> new CondicionClima("Despejado", "sol");
            case 1, 2, 3 -> new CondicionClima("Nublado", "nube");
            case 45, 48 -> new CondicionClima("Niebla", "niebla");
            case 51, 53, 55, 56, 57 -> new CondicionClima("Llovizna", "lluvia");
            case 61, 63, 65, 66, 67 -> new CondicionClima("Lluvia", "lluvia");
            case 71, 73, 75, 77 -> new CondicionClima("Nieve", "nieve");
            case 80, 81, 82 -> new CondicionClima("Chubascos", "lluvia");
            case 85, 86 -> new CondicionClima("Nieve", "nieve");
            case 95, 96, 99 -> new CondicionClima("Tormenta", "tormenta");
            default -> new CondicionClima("Parcialmente nublado", "nube");
        };
    }

    private record CondicionClima(String texto, String icono) {
    }
}
