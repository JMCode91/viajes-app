package com.app.viajes.controller;

import com.app.viajes.dto.ActividadRequestDTO;
import com.app.viajes.dto.ActividadResponseDTO;
import com.app.viajes.service.ActividadService;
import com.app.viajes.service.StorageService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controlador de actividades de un viaje. Todos los endpoints requieren JWT
 * y la pertenencia del usuario al viaje.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/actividades")
public class ActividadController {

    private final ActividadService actividadService;
    private final StorageService storageService;

    public ActividadController(ActividadService actividadService,
                               StorageService storageService) {
        this.actividadService = actividadService;
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<ActividadResponseDTO> crearActividad(
            @PathVariable UUID viajeId,
            @Valid @RequestBody ActividadRequestDTO request) {
        ActividadResponseDTO response = actividadService.crearActividad(viajeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ActividadResponseDTO>> obtenerActividades(
            @PathVariable UUID viajeId) {
        return ResponseEntity.ok(actividadService.obtenerActividadesPorViaje(viajeId));
    }

    @GetMapping("/timeline")
    public ResponseEntity<List<ActividadResponseDTO>> obtenerTimelinePorFecha(
            @PathVariable UUID viajeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(actividadService.obtenerTimelinePorFecha(viajeId, fecha));
    }

    @PutMapping("/{actividadId}")
    public ResponseEntity<ActividadResponseDTO> actualizarActividad(
            @PathVariable UUID viajeId,
            @PathVariable UUID actividadId,
            @Valid @RequestBody ActividadRequestDTO request) {
        ActividadResponseDTO response = actividadService.actualizarActividad(viajeId, actividadId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{actividadId}/documentos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ActividadResponseDTO> subirDocumento(
            @PathVariable UUID viajeId,
            @PathVariable UUID actividadId,
            @RequestParam("file") MultipartFile file) {
        String urlArchivo = storageService.subirDocumento(file);
        ActividadResponseDTO response = actividadService.guardarDocumento(
                viajeId, actividadId, file.getOriginalFilename(), urlArchivo);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{actividadId}/documentos/{documentoId}")
    public ResponseEntity<Void> eliminarDocumento(
            @PathVariable UUID viajeId,
            @PathVariable UUID actividadId,
            @PathVariable UUID documentoId) {
        actividadService.eliminarDocumento(viajeId, actividadId, documentoId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{actividadId}")
    public ResponseEntity<Void> eliminarActividad(
            @PathVariable UUID viajeId,
            @PathVariable UUID actividadId) {
        actividadService.eliminarActividad(viajeId, actividadId);
        return ResponseEntity.noContent().build();
    }
}