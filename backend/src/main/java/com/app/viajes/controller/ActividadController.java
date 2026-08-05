package com.app.viajes.controller;

import com.app.viajes.dto.ActividadRequestDTO;
import com.app.viajes.dto.ActividadResponseDTO;
import com.app.viajes.service.ActividadService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
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

    @DeleteMapping("/{actividadId}")
    public ResponseEntity<Void> eliminarActividad(
            @PathVariable UUID viajeId,
            @PathVariable UUID actividadId) {
        actividadService.eliminarActividad(viajeId, actividadId);
        return ResponseEntity.noContent().build();
    }
}