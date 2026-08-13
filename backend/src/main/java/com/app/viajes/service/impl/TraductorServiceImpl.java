package com.app.viajes.service.impl;

import com.app.viajes.dto.TraduccionRequestDTO;
import com.app.viajes.dto.TraduccionResponseDTO;
import com.app.viajes.exception.TraduccionException;
import com.app.viajes.service.TraductorService;
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
 * Implementacion del servicio de traduccion usando la API de DeepL.
 * La API key se inyecta desde la propiedad 'deepl.api.key'
 * (debe sobrescribirse via variable de entorno en produccion).
 */
@Service
public class TraductorServiceImpl implements TraductorService {

    private static final Logger log = LoggerFactory.getLogger(TraductorServiceImpl.class);
    private static final String DEEPL_ENDPOINT = "https://api-free.deepl.com/v2/translate";

    private final String apiKey;
    private final RestClient restClient;

    public TraductorServiceImpl(@Value("${deepl.api.key:}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(DEEPL_ENDPOINT)
                .defaultHeader("Authorization", "DeepL-Auth-Key " + apiKey)
                .build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public TraduccionResponseDTO traducir(TraduccionRequestDTO request) {
        if (!StringUtils.hasText(apiKey)) {
            throw new TraduccionException(
                    "El servicio de traduccion no esta configurado (falta deepl.api.key)");
        }

        try {
            Map<String, Object> response = restClient.post()
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body("text=" + request.texto() + "&target_lang=" + request.idiomaDestino())
                    .retrieve()
                    .body(Map.class);

            List<Map<String, String>> translations =
                    (List<Map<String, String>>) response.get("translations");
            if (translations == null || translations.isEmpty()) {
                throw new TraduccionException("DeepL no devolvio traduccion");
            }

            String textoTraducido = translations.get(0).get("text");
            return new TraduccionResponseDTO(textoTraducido, null);
        } catch (TraduccionException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error al llamar a la API de DeepL", ex);
            throw new TraduccionException("No se pudo traducir el texto en este momento", ex);
        }
    }
}