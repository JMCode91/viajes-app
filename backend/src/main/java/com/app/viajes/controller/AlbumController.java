package com.app.viajes.controller;

import com.app.viajes.dto.AlbumRequestDTO;
import com.app.viajes.dto.AlbumResponseDTO;
import com.app.viajes.service.AlbumService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de albumes de la galeria multimedia de un viaje.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/albumes")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @GetMapping
    public ResponseEntity<List<AlbumResponseDTO>> obtenerAlbumes(@PathVariable UUID viajeId) {
        return ResponseEntity.ok(albumService.obtenerAlbumes(viajeId));
    }

    @PostMapping
    public ResponseEntity<AlbumResponseDTO> crearAlbum(
            @PathVariable UUID viajeId,
            @Valid @RequestBody AlbumRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(albumService.crearAlbum(viajeId, request));
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> eliminarAlbum(
            @PathVariable UUID viajeId,
            @PathVariable UUID albumId) {
        albumService.eliminarAlbum(viajeId, albumId);
        return ResponseEntity.noContent().build();
    }
}