package com.app.viajes.service;

import com.app.viajes.dto.TraduccionRequestDTO;
import com.app.viajes.dto.TraduccionResponseDTO;

/**
 * Servicio de traduccion mediante la API de DeepL.
 */
public interface TraductorService {

    TraduccionResponseDTO traducir(TraduccionRequestDTO request);
}