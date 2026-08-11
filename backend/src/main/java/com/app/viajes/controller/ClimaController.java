package com.app.viajes.controller;

import com.app.viajes.dto.response.ClimaResponseDTO;
import com.app.viajes.service.ClimaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controlador del clima: prevision meteorologica para una ubicacion y fecha.
 * Requiere JWT (cae bajo {@code anyRequest().authenticated()} de SecurityConfig).
 */
@RestController
@RequestMapping("/api/clima")
public class ClimaController {

    private final ClimaService climaService;

    public ClimaController(ClimaService climaService) {
        this.climaService = climaService;
    }

    @GetMapping
    public ResponseEntity<ClimaResponseDTO> obtenerClima(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(climaService.obtenerClima(lat, lon, fecha));
    }
}
