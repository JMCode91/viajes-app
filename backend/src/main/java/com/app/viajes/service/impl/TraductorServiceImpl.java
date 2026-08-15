package com.app.viajes.service.impl;

import com.app.viajes.dto.TraduccionRequestDTO;
import com.app.viajes.dto.TraduccionResponseDTO;
import com.app.viajes.exception.DatosInvalidosException;
import com.app.viajes.exception.TraduccionException;
import com.app.viajes.model.enums.IdiomaDestino;
import com.app.viajes.service.TraductorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

/**
 * Implementacion del servicio de traduccion usando la API de DeepL.
 * La API key se inyecta desde la propiedad 'deepl.api.key'
 * (debe sobrescribirse via variable de entorno en produccion).
 *
 * <p>Disena resiliente: fail-fast con un timeout estricto y corto, y cualquier
 * fallo de la API externa se traduce a una {@link TraduccionException} para que
 * el frontend muestre un mensaje de error amigable.</p>
 */
@Service
public class TraductorServiceImpl implements TraductorService {

    private static final Logger log = LoggerFactory.getLogger(TraductorServiceImpl.class);
    private static final String DEEPL_ENDPOINT = "https://api-free.deepl.com/v2/translate";
    private static final int TIMEOUT_MILISEGUNDOS = 3000;

    private final String apiKey;
    private final RestClient restClient;

    public TraductorServiceImpl(@Value("${deepl.api.key:}") String apiKey) {
        this.apiKey = apiKey;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MILISEGUNDOS);
        requestFactory.setReadTimeout(TIMEOUT_MILISEGUNDOS);

        this.restClient = RestClient.builder()
                .baseUrl(DEEPL_ENDPOINT)
                .defaultHeader("Authorization", "DeepL-Auth-Key " + apiKey)
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public TraduccionResponseDTO traducir(TraduccionRequestDTO request) {
        if (!StringUtils.hasText(apiKey)) {
            throw new TraduccionException(
                    "El servicio de traduccion no esta configurado (falta deepl.api.key)");
        }

        // Valida y normaliza el idioma destino (incluye Español/es). El idioma de
        // origen NO se envia en la peticion: DeepL lo autodetecta, lo que permite
        // la traduccion bidireccional (ej. Japones -> Español).
        IdiomaDestino idiomaDestino = resolverIdiomaDestino(request.idiomaDestino());

        try {
            Map<String, Object> response = restClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(construirCuerpoTraduccion(request, idiomaDestino))
                    .retrieve()
                    .body(Map.class);

            return parsearRespuestaTraduccion(response);
        } catch (TraduccionException ex) {
            throw ex;
        } catch (Exception ex) {
            // Timeout, error de conexion o 5xx de la API externa: se traduce a
            // una excepcion controlada del dominio para un mensaje de error
            // amigable en el frontend.
            log.error("Fallo critico en la API externa de Traduccion: {}", ex.getMessage(), ex);
            throw new TraduccionException("No se pudo traducir el texto en este momento", ex);
        }
    }

    /**
     * Resuelve y valida el idioma destino contra los soportados (Español, Inglés,
     * Japonés). Un codigo no soportado se traduce a un error 400.
     */
    private IdiomaDestino resolverIdiomaDestino(String idiomaDestino) {
        try {
            return IdiomaDestino.desdeCodigo(idiomaDestino);
        } catch (IllegalArgumentException ex) {
            throw new DatosInvalidosException(ex.getMessage());
        }
    }

    /**
     * Construye el cuerpo de la peticion (form-urlencoded, con codificacion
     * automatica de Spring) con el texto a traducir y el idioma destino. El
     * idioma de origen se deja fuera para que la API lo autodetecte.
     */
    private MultiValueMap<String, String> construirCuerpoTraduccion(
            TraduccionRequestDTO request, IdiomaDestino idiomaDestino) {
        MultiValueMap<String, String> cuerpo = new LinkedMultiValueMap<>();
        cuerpo.add("text", request.texto());
        cuerpo.add("target_lang", idiomaDestino.codigoDeepL());
        return cuerpo;
    }

    /**
     * Parsea la respuesta JSON de DeepL extrayendo la primera traduccion
     * ({@code translations[0].text}).
     */
    @SuppressWarnings("unchecked")
    private TraduccionResponseDTO parsearRespuestaTraduccion(Map<String, Object> response) {
        List<Map<String, String>> translations =
                (List<Map<String, String>>) response.get("translations");
        if (translations == null || translations.isEmpty()) {
            throw new TraduccionException("DeepL no devolvio traduccion");
        }

        String textoTraducido = translations.get(0).get("text");
        return new TraduccionResponseDTO(textoTraducido, null);
    }
}