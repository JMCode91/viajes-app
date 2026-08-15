package com.app.viajes.controller;

import com.app.viajes.dto.UsuarioResponseDTO;
import com.app.viajes.dto.request.CambiarPasswordRequest;
import com.app.viajes.dto.request.PreferenciasNotificacionRequest;
import com.app.viajes.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador del perfil del usuario autenticado (exige JWT).
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> obtenerPerfil() {
        return ResponseEntity.ok(usuarioService.obtenerPerfil());
    }

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponseDTO> subirAvatar(
            @RequestParam("file") MultipartFile file) {
        UsuarioResponseDTO response = usuarioService.actualizarAvatar(file);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/me/fondo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponseDTO> subirFondoPantalla(
            @RequestParam("file") MultipartFile file) {
        UsuarioResponseDTO response = usuarioService.actualizarFondoPantalla(file);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/me/preferencias")
    public ResponseEntity<UsuarioResponseDTO> actualizarPreferencias(
            @Valid @RequestBody PreferenciasNotificacionRequest request) {
        return ResponseEntity.ok(usuarioService.actualizarPreferencias(request));
    }
}