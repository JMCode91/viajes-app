package com.app.viajes.controller;

import com.app.viajes.dto.InvitacionRequestDTO;
import com.app.viajes.dto.ViajeRequestDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.service.ViajeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
 * Controlador de viajes e invitaciones. Todos los endpoints requieren JWT valido.
 */
@RestController
@RequestMapping("/api/viajes")
public class ViajeController {

    private final ViajeService viajeService;

    public ViajeController(ViajeService viajeService) {
        this.viajeService = viajeService;
    }

    @PostMapping
    public ResponseEntity<ViajeResponseDTO> crearViaje(@Valid @RequestBody ViajeRequestDTO request) {
        ViajeResponseDTO response = viajeService.crearViaje(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ViajeResponseDTO>> obtenerMisViajes() {
        return ResponseEntity.ok(viajeService.obtenerMisViajes());
    }

    @PostMapping("/{id}/invitar")
    public ResponseEntity<Void> invitarUsuario(@PathVariable UUID id,
                                               @Valid @RequestBody InvitacionRequestDTO request) {
        viajeService.invitarUsuario(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}/responder-invitacion")
    public ResponseEntity<Void> responderInvitacion(@PathVariable UUID id,
                                                    @RequestParam boolean aceptar) {
        viajeService.responderInvitacion(id, aceptar);
        return ResponseEntity.ok().build();
    }
}