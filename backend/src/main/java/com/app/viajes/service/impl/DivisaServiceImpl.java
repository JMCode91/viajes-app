package com.app.viajes.service.impl;

import com.app.viajes.dto.response.DivisaResponseDTO;
import com.app.viajes.exception.DivisaException;
import com.app.viajes.service.DivisaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Implementacion del servicio de conversion de divisas usando la API publica
 * de Frankfurter (https://api.frankfurter.app), sin necesidad de API key.
 */
@Service
public class DivisaServiceImpl implements DivisaService {

    private static final Logger log = LoggerFactory.getLogger(DivisaServiceImpl.class);
    private static final String FRANKFURTER_ENDPOINT = "https://api.frankfurter.app";

    private final RestClient restClient;

    public DivisaServiceImpl() {
        this.restClient = RestClient.builder()
                .baseUrl(FRANKFURTER_ENDPOINT)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public DivisaResponseDTO convertir(Double monto, String from, String to) {
        if (monto == null || monto <= 0) {
            throw new DivisaException("El monto debe ser un numero positivo");
        }
        String monedaOrigen = from.toUpperCase();
        String monedaDestino = to.toUpperCase();

        try {
            Map<String, Object> respuesta = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/latest")
                            .queryParam("amount", monto)
                            .queryParam("from", monedaOrigen)
                            .queryParam("to", monedaDestino)
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (respuesta == null) {
                throw new DivisaException("La API de divisas no devolvio datos");
            }

            Map<String, Number> rates = (Map<String, Number>) respuesta.get("rates");
            if (rates == null || !rates.containsKey(monedaDestino)) {
                throw new DivisaException(
                        "No se encontro el tipo de cambio para la moneda " + monedaDestino);
            }

            Double montoConvertido = rates.get(monedaDestino).doubleValue();
            Double ratioCambio = montoConvertido / monto;

            return new DivisaResponseDTO(
                    monto,
                    monedaOrigen,
                    montoConvertido,
                    monedaDestino,
                    ratioCambio);
        } catch (DivisaException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error al llamar a la API de Frankfurter", ex);
            throw new DivisaException(
                    "No se pudo realizar la conversion de divisas en este momento", ex);
        }
    }
}
