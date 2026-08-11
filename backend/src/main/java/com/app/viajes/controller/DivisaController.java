package com.app.viajes.controller;

import com.app.viajes.dto.response.DivisaResponseDTO;
import com.app.viajes.service.DivisaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador de conversion de divisas (API publica de Frankfurter).
 * Requiere JWT (cae bajo {@code anyRequest().authenticated()} de SecurityConfig).
 */
@RestController
@RequestMapping("/api/divisas")
public class DivisaController {

    private final DivisaService divisaService;

    public DivisaController(DivisaService divisaService) {
        this.divisaService = divisaService;
    }

    @GetMapping("/convertir")
    public ResponseEntity<DivisaResponseDTO> convertir(
            @RequestParam Double amount,
            @RequestParam(defaultValue = "EUR") String from,
            @RequestParam(defaultValue = "JPY") String to) {
        return ResponseEntity.ok(divisaService.convertir(amount, from, to));
    }

    @GetMapping("/monedas")
    public ResponseEntity<Map<String, String>> monedas() {
        return ResponseEntity.ok(divisaService.obtenerMonedas());
    }
}
