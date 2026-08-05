package com.app.viajes.controller;

import com.app.viajes.dto.ItemChecklistRequestDTO;
import com.app.viajes.dto.ItemChecklistResponseDTO;
import com.app.viajes.service.ChecklistService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de checklist de un viaje.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/checklist")
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    @PostMapping
    public ResponseEntity<ItemChecklistResponseDTO> crearItem(
            @PathVariable UUID viajeId,
            @Valid @RequestBody ItemChecklistRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(checklistService.crearItem(viajeId, request));
    }

    @GetMapping
    public ResponseEntity<List<ItemChecklistResponseDTO>> obtenerItems(@PathVariable UUID viajeId) {
        return ResponseEntity.ok(checklistService.obtenerItems(viajeId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemChecklistResponseDTO> marcarCompletado(
            @PathVariable UUID viajeId,
            @PathVariable UUID itemId,
            @RequestParam boolean completado) {
        return ResponseEntity.ok(checklistService.marcarCompletado(viajeId, itemId, completado));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> eliminarItem(@PathVariable UUID viajeId, @PathVariable UUID itemId) {
        checklistService.eliminarItem(viajeId, itemId);
        return ResponseEntity.noContent().build();
    }
}