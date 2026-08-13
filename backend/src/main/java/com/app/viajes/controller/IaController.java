package com.app.viajes.controller;

import com.app.viajes.dto.TraduccionRequestDTO;
import com.app.viajes.dto.TraduccionResponseDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.dto.request.ActividadIaDTO;
import com.app.viajes.dto.request.IaGuardarViajeRequestDTO;
import com.app.viajes.dto.request.IaPromptRequestDTO;
import com.app.viajes.dto.request.IaRegenerarActividadRequestDTO;
import com.app.viajes.dto.response.IaItinerarioResponseDTO;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.model.Usuario;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.service.IaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de generacion de itinerarios con IA (Google Gemini).
 * Requiere JWT (cae bajo {@code anyRequest().authenticated()} de SecurityConfig).
 */
@RestController
@RequestMapping("/api/ia")
public class IaController {

    private final IaService iaService;
    private final UsuarioRepository usuarioRepository;

    public IaController(IaService iaService, UsuarioRepository usuarioRepository) {
        this.iaService = iaService;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/generar")
    public ResponseEntity<IaItinerarioResponseDTO> generar(
            @Valid @RequestBody IaPromptRequestDTO request) {
        return ResponseEntity.ok(iaService.generarItinerario(request));
    }

    @PostMapping("/guardar")
    public ResponseEntity<ViajeResponseDTO> guardar(
            @Valid @RequestBody IaGuardarViajeRequestDTO request) {
        Usuario usuario = obtenerUsuarioAutenticado();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(iaService.guardarViajeGenerado(request, usuario.getId()));
    }

    @PostMapping("/regenerar-actividad")
    public ResponseEntity<ActividadIaDTO> regenerarActividad(
            @Valid @RequestBody IaRegenerarActividadRequestDTO request) {
        return ResponseEntity.ok(iaService.regenerarActividad(request));
    }

    @PostMapping("/traducir")
    public ResponseEntity<TraduccionResponseDTO> traducir(
            @Valid @RequestBody TraduccionRequestDTO request) {
        return ResponseEntity.ok(iaService.traducirTexto(request));
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }
}
