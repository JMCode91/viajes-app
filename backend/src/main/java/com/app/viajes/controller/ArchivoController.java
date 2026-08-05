package com.app.viajes.controller;

import com.app.viajes.dto.ArchivoResponseDTO;
import com.app.viajes.service.ArchivoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.Objects;

/**
 * Controlador de archivos seguros de un viaje.
 * Los archivos se descargan como flujo de bytes previa validacion
 * del JWT y de la pertenencia del usuario al viaje.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/archivos")
public class ArchivoController {

    private final ArchivoService archivoService;

    public ArchivoController(ArchivoService archivoService) {
        this.archivoService = archivoService;
    }

    @PostMapping
    public ResponseEntity<ArchivoResponseDTO> subirArchivo(
            @PathVariable UUID viajeId,
            @RequestParam("archivo") MultipartFile archivo) {
        String nombreArchivo = archivoService.guardarArchivo(viajeId, archivo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ArchivoResponseDTO(nombreArchivo));
    }

    @GetMapping("/{nombreArchivo}")
    public ResponseEntity<Resource> descargarArchivo(
            @PathVariable UUID viajeId,
            @PathVariable String nombreArchivo) {
        Resource resource = archivoService.cargarArchivo(viajeId, nombreArchivo);
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        try {
            MediaType detectado = MediaType.asMediaType(
                    MimeType.valueOf(Objects.requireNonNull(resource.getFilename())));
            if (detectado != null) {
                mediaType = detectado;
            }
        } catch (Exception ignored) {
            // Se mantiene el Content-Type por defecto si no se puede detectar.
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(mediaType)
                .body(resource);
    }
}