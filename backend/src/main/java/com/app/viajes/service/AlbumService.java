package com.app.viajes.service;

import com.app.viajes.dto.AlbumRequestDTO;
import com.app.viajes.dto.AlbumResponseDTO;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de albumes de fotos de la galeria multimedia de un viaje.
 * Cada operacion valida que el usuario autenticado pertenece al viaje.
 */
public interface AlbumService {

    List<AlbumResponseDTO> obtenerAlbumes(UUID viajeId);

    AlbumResponseDTO crearAlbum(UUID viajeId, AlbumRequestDTO dto);

    void eliminarAlbum(UUID viajeId, UUID albumId);
}