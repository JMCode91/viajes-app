package com.app.viajes.service;

import com.app.viajes.dto.AuthResponseDTO;
import com.app.viajes.dto.LoginRequestDTO;
import com.app.viajes.dto.MensajeResponseDTO;
import com.app.viajes.dto.RegisterRequestDTO;

/**
 * Servicio de autenticacion: registro con confirmacion por email,
 * verificacion de cuenta e inicio de sesion.
 */
public interface AuthService {

    MensajeResponseDTO registrar(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);

    MensajeResponseDTO verificarCuenta(String token);
}
