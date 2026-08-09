package com.app.viajes.controller;

import com.app.viajes.dto.BalanceResponseDTO;
import com.app.viajes.service.GastoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Controlador de balances financieros de un viaje.
 * Devuelve el resumen financiero para el usuario autenticado.
 */
@RestController
@RequestMapping("/api/viajes/{viajeId}/balances")
public class BalanceController {

    private final GastoService gastoService;

    public BalanceController(GastoService gastoService) {
        this.gastoService = gastoService;
    }

    @GetMapping
    public ResponseEntity<BalanceResponseDTO> obtenerBalance(@PathVariable UUID viajeId) {
        return ResponseEntity.ok(gastoService.obtenerBalance(viajeId));
    }
}