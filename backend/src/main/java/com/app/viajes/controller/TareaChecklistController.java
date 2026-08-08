package com.app.viajes.controller;

import com.app.viajes.dto.TareaChecklistRequestDTO;
import com.app.viajes.dto.TareaChecklistResponseDTO;
import com.app.viajes.service.TareaChecklistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

import java.util.List;
import java.util.UUID;

/**
 * Controlador del checklist (tareas) de un viaje. Todos los endpoints
 * requieren JWT valido y verifican la pertenencia al viaje en el servicio.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/checklist")
public class TareaChecklistController {

    private final TareaChecklistService tareaChecklistService;

    public TareaChecklistController(TareaChecklistService tareaChecklistService) {
        this.tareaChecklistService = tareaChecklistService;
    }

    @PostMapping
    public ResponseEntity<TareaChecklistResponseDTO> crearTarea(
            @PathVariable UUID viajeId,
            @Valid @RequestBody TareaChecklistRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(tareaChecklistService.crearTarea(viajeId, request));
    }

    @GetMapping
    public ResponseEntity<List<TareaChecklistResponseDTO>> obtenerTareas(
            @PathVariable UUID viajeId) {
        return ResponseEntity.ok(tareaChecklistService.obtenerTareas(viajeId));
    }

    @PutMapping("/{tareaId}/estado")
    public ResponseEntity<TareaChecklistResponseDTO> cambiarEstado(
            @PathVariable UUID viajeId,
            @PathVariable Long tareaId,
            @RequestParam boolean completada) {
        return ResponseEntity.ok(
                tareaChecklistService.cambiarEstado(viajeId, tareaId, completada));
    }

    @PutMapping("/{tareaId}/asignar")
    public ResponseEntity<TareaChecklistResponseDTO> asignarUsuario(
            @PathVariable UUID viajeId,
            @PathVariable Long tareaId,
            @RequestParam(required = false) UUID asignadoId) {
        return ResponseEntity.ok(
                tareaChecklistService.asignarUsuario(viajeId, tareaId, asignadoId));
    }

    @DeleteMapping("/{tareaId}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable UUID viajeId,
                                              @PathVariable Long tareaId) {
        tareaChecklistService.eliminarTarea(viajeId, tareaId);
        return ResponseEntity.noContent().build();
    }
}