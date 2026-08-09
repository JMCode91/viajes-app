package com.app.viajes.controller;

import com.app.viajes.dto.FotoResponseDTO;
import com.app.viajes.service.FotoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de fotos de la galeria multimedia de un viaje.
 * La subida guarda el archivo en Cloudflare R2 y asigna el usuario
 * autenticado (extraido del JWT) como autor de la foto.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/fotos")
public class FotoController {

    private final FotoService fotoService;

    public FotoController(FotoService fotoService) {
        this.fotoService = fotoService;
    }

    @GetMapping
    public ResponseEntity<List<FotoResponseDTO>> obtenerFotos(
            @PathVariable UUID viajeId,
            @RequestParam(required = false) UUID albumId) {
        return ResponseEntity.ok(fotoService.obtenerFotos(viajeId, albumId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FotoResponseDTO> subirFoto(
            @PathVariable UUID viajeId,
            @RequestParam(value = "albumId", required = false) UUID albumId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(fotoService.subirFoto(viajeId, albumId, file));
    }

    @DeleteMapping("/{fotoId}")
    public ResponseEntity<Void> eliminarFoto(
            @PathVariable UUID viajeId,
            @PathVariable UUID fotoId) {
        fotoService.eliminarFoto(viajeId, fotoId);
        return ResponseEntity.noContent().build();
    }
}