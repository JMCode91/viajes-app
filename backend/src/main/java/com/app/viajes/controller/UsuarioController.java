package com.app.viajes.controller;

import com.app.viajes.dto.UsuarioResponseDTO;
import com.app.viajes.service.UsuarioService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UsuarioResponseDTO> subirAvatar(
            @RequestParam("file") MultipartFile file) {
        UsuarioResponseDTO response = usuarioService.actualizarAvatar(file);
        return ResponseEntity.ok(response);
    }
}