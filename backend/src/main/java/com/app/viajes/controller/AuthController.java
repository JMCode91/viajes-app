package com.app.viajes.controller;

import com.app.viajes.dto.AuthResponseDTO;
import com.app.viajes.dto.LoginRequestDTO;
import com.app.viajes.dto.MensajeResponseDTO;
import com.app.viajes.dto.RegisterRequestDTO;
import com.app.viajes.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador publico de autenticacion (registro, verificacion de cuenta
 * por email e inicio de sesion).
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<MensajeResponseDTO> registrar(@Valid @RequestBody RegisterRequestDTO request) {
        MensajeResponseDTO response = authService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar")
    public ResponseEntity<AuthResponseDTO> verificar(@RequestParam String token) {
        AuthResponseDTO response = authService.verificarCuenta(token);
        return ResponseEntity.ok(response);
    }
}