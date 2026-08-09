package com.app.viajes.service;

import com.app.viajes.dto.FotoResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de fotos de la galeria multimedia de un viaje.
 * Las fotos se guardan en Cloudflare R2 y se persiste su URL junto al
 * usuario autenticado que las subio (autor).
 */
public interface FotoService {

    List<FotoResponseDTO> obtenerFotos(UUID viajeId, UUID albumId);

    FotoResponseDTO subirFoto(UUID viajeId, UUID albumId, MultipartFile archivo);

    void eliminarFoto(UUID viajeId, UUID fotoId);
}