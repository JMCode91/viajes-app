package com.app.viajes.service.impl;

import com.app.viajes.dto.response.DivisaResponseDTO;
import com.app.viajes.exception.DivisaException;
import com.app.viajes.service.DivisaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.Map;

/**
 * Implementacion del servicio de conversion de divisas usando la API publica
 * de Frankfurter (https://api.frankfurter.app), sin necesidad de API key.
 *
 * <p>Disena resiliente: fail-fast con un timeout estricto y corto para no
 * colgar el hilo de Spring Boot, y fallback con un catalogo estatico de
 * monedas principales si la API externa falla, se agota o devuelve un 5xx
 * (los desplegables del frontend nunca se quedan vacios).</p>
 */
@Service
public class DivisaServiceImpl implements DivisaService {

    private static final Logger log = LoggerFactory.getLogger(DivisaServiceImpl.class);
    private static final String FRANKFURTER_ENDPOINT = "https://api.frankfurter.app";
    private static final int TIMEOUT_MILISEGUNDOS = 3000;

    private final RestClient restClient;

    public DivisaServiceImpl() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MILISEGUNDOS);
        requestFactory.setReadTimeout(TIMEOUT_MILISEGUNDOS);

        this.restClient = RestClient.builder()
                .baseUrl(FRANKFURTER_ENDPOINT)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public DivisaResponseDTO convertir(Double monto, String from, String to) {
        if (monto == null || monto <= 0) {
            throw new DivisaException("El monto debe ser un numero positivo");
        }
        String monedaOrigen = from.toUpperCase();
        String monedaDestino = to.toUpperCase();

        if (monedaOrigen.equals(monedaDestino)) {
            return conversionMismaMoneda(monto, monedaOrigen);
        }

        try {
            Map<String, Object> respuesta = restClient.get()
                    .uri(uriBuilder -> construirUriConversion(
                            uriBuilder, monto, monedaOrigen, monedaDestino))
                    .retrieve()
                    .body(Map.class);

            return parsearRespuestaConversion(respuesta, monto, monedaOrigen, monedaDestino);
        } catch (DivisaException ex) {
            throw ex;
        } catch (Exception ex) {
            // Timeout, error de conexion o 5xx de la API externa: se traduce a
            // una excepcion controlada del dominio para que el manejador global
            // devuelva un error limpio al frontend.
            log.error("Error al llamar a la API de Frankfurter", ex);
            throw new DivisaException("Servicio de divisas temporalmente no disponible", ex);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> obtenerMonedas() {
        try {
            Map<String, String> monedas = restClient.get()
                    .uri("/currencies")
                    .retrieve()
                    .body(Map.class);

            if (monedas == null) {
                throw new DivisaException("La API de divisas no devolvio el catalogo de monedas");
            }
            return monedas;
        } catch (DivisaException ex) {
            log.warn("Catalogo de monedas no disponible, usando fallback estatico: {}",
                    ex.getMessage());
            return monedasFallback();
        } catch (Exception ex) {
            // Timeout, error de conexion o 5xx de la API externa: se degrada al
            // catalogo estatico para que los desplegables nunca queden vacios.
            log.error("Error al obtener el catalogo de monedas de Frankfurter, "
                    + "usando fallback estatico", ex);
            return monedasFallback();
        }
    }

    /**
     * Construye la URI de conversion ({@code /latest}) con los parametros
     * necesarios: monto, moneda origen y moneda destino.
     */
    private URI construirUriConversion(UriBuilder uriBuilder,
                                       Double monto,
                                       String monedaOrigen,
                                       String monedaDestino) {
        return uriBuilder.path("/latest")
                .queryParam("amount", monto)
                .queryParam("from", monedaOrigen)
                .queryParam("to", monedaDestino)
                .build();
    }

    /**
     * Construye una conversion identidad (misma moneda origen y destino)
     * sin necesidad de llamar a la API externa.
     */
    private DivisaResponseDTO conversionMismaMoneda(Double monto, String moneda) {
        return new DivisaResponseDTO(
                monto,
                moneda,
                monto,
                moneda,
                1.0,
                LocalDate.now().toString());
    }

    /**
     * Parsea la respuesta JSON de la conversion y valida que contenga el tipo
     * de cambio de la moneda destino.
     */
    @SuppressWarnings("unchecked")
    private DivisaResponseDTO parsearRespuestaConversion(Map<String, Object> respuesta,
                                                         Double monto,
                                                         String monedaOrigen,
                                                         String monedaDestino) {
        if (respuesta == null) {
            throw new DivisaException("La API de divisas no devolvio datos");
        }

        Map<String, Object> rates = extraerRates(respuesta, monedaDestino);
        String fechaActualizacion = String.valueOf(respuesta.get("date"));
        Double montoConvertido = ((Number) rates.get(monedaDestino)).doubleValue();

        return new DivisaResponseDTO(
                monto,
                monedaOrigen,
                montoConvertido,
                monedaDestino,
                montoConvertido / monto,
                fechaActualizacion);
    }

    /**
     * Extrae y valida el mapa {@code rates} de la respuesta de la API.
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extraerRates(Map<String, Object> respuesta, String monedaDestino) {
        Map<String, Object> rates = (Map<String, Object>) respuesta.get("rates");
        if (rates == null || !rates.containsKey(monedaDestino)) {
            throw new DivisaException(
                    "No se encontro el tipo de cambio para la moneda " + monedaDestino);
        }
        return rates;
    }

    /**
     * Catalogo estatico de salvavidas con las divisas principales. Se usa como
     * fallback cuando la API externa no responde a tiempo o falla, garantizando
     * que el frontend siempre tenga opciones en los desplegables.
     */
    private Map<String, String> monedasFallback() {
        return Map.ofEntries(
                Map.entry("EUR", "Euro"),
                Map.entry("JPY", "Yen Japones"),
                Map.entry("USD", "Dolar Estadounidense"),
                Map.entry("GBP", "Libra Esterlina"),
                Map.entry("CHF", "Franco Suizo"),
                Map.entry("CAD", "Dolar Canadiense"),
                Map.entry("AUD", "Dolar Australiano"),
                Map.entry("CNY", "Yuan Chino"),
                Map.entry("KRW", "Won Surcoreano"),
                Map.entry("MXN", "Peso Mexicano"));
    }
}
