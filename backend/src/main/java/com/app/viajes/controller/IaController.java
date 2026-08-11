package com.app.viajes.controller;

import com.app.viajes.dto.request.IaPromptRequestDTO;
import com.app.viajes.dto.response.IaItinerarioResponseDTO;
import com.app.viajes.service.IaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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

    public IaController(IaService iaService) {
        this.iaService = iaService;
    }

    @PostMapping("/generar")
    public ResponseEntity<IaItinerarioResponseDTO> generar(
            @Valid @RequestBody IaPromptRequestDTO request) {
        return ResponseEntity.ok(iaService.generarItinerario(request));
    }
}
