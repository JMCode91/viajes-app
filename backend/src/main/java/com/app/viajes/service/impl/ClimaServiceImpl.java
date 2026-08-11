package com.app.viajes.service.impl;

import com.app.viajes.dto.response.ClimaResponseDTO;
import com.app.viajes.exception.ClimaException;
import com.app.viajes.service.ClimaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Implementacion del servicio de clima usando la API publica de Open-Meteo
 * (sin necesidad de API key). Si la fecha queda fuera de los proximos 14 dias
 * se devuelve un fallback historico generico (condiciones mensuales de Japon).
 */
@Service
public class ClimaServiceImpl implements ClimaService {

    private static final Logger log = LoggerFactory.getLogger(ClimaServiceImpl.class);
    private static final String OPEN_METEO_ENDPOINT = "https://api.open-meteo.com";
    private static final long PLAZO_PRONOSTICO_DIAS = 14L;

    private final RestClient restClient;

    public ClimaServiceImpl() {
        this.restClient = RestClient.builder()
                .baseUrl(OPEN_METEO_ENDPOINT)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ClimaResponseDTO obtenerClima(Double latitud, Double longitud, LocalDate fecha) {
        LocalDate hoy = LocalDate.now();
        boolean dentroDePlazo = !fecha.isBefore(hoy)
                && fecha.isBefore(hoy.plusDays(PLAZO_PRONOSTICO_DIAS));

        if (!dentroDePlazo) {
            return fallbackHistorico(fecha);
        }

        try {
            Map<String, Object> respuesta = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitud)
                            .queryParam("longitude", longitud)
                            .queryParam("daily", "weathercode,temperature_2m_max,temperature_2m_min")
                            .queryParam("timezone", "auto")
                            .queryParam("start_date", fecha)
                            .queryParam("end_date", fecha)
                            .build())
                    .retrieve()
                    .body(Map.class);

            Map<String, Object> diario = (Map<String, Object>) respuesta.get("daily");
            if (diario == null) {
                throw new ClimaException("Open-Meteo no devolvio datos diarios");
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
        } catch (ClimaException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error al llamar a la API de Open-Meteo", ex);
            throw new ClimaException("No se pudo obtener el clima en este momento", ex);
        }
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
