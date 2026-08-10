package com.app.viajes.controller;

import com.app.viajes.dto.GastoRequestDTO;
import com.app.viajes.dto.GastoResponseDTO;
import com.app.viajes.service.GastoService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de gastos de un viaje.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/gastos")
public class GastoController {

    private final GastoService gastoService;

    public GastoController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @PostMapping
    public ResponseEntity<GastoResponseDTO> crearGasto(
            @PathVariable UUID viajeId,
            @Valid @RequestBody GastoRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(gastoService.crearGasto(viajeId, request));
    }

    @GetMapping
    public ResponseEntity<List<GastoResponseDTO>> obtenerGastos(@PathVariable UUID viajeId) {
        return ResponseEntity.ok(gastoService.obtenerGastos(viajeId));
    }

    @DeleteMapping("/{gastoId}")
    public ResponseEntity<Void> eliminarGasto(@PathVariable UUID viajeId, @PathVariable UUID gastoId) {
        gastoService.eliminarGasto(viajeId, gastoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{gastoId}")
    public ResponseEntity<GastoResponseDTO> actualizarGasto(
            @PathVariable UUID viajeId,
            @PathVariable UUID gastoId,
            @Valid @RequestBody GastoRequestDTO request) {
        return ResponseEntity.ok(gastoService.actualizarGasto(viajeId, gastoId, request));
    }
}