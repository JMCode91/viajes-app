package com.app.viajes.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * Servicio de almacenamiento seguro de archivos de un viaje.
 * Los archivos se guardan aislados por viaje y solo pueden
 * subirse/descargarse validando la pertenencia del usuario.
 */
public interface ArchivoService {

    String guardarArchivo(UUID viajeId, MultipartFile archivo);

    Resource cargarArchivo(UUID viajeId, String nombreArchivo);
}