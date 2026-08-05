package com.app.viajes.service;

import com.app.viajes.dto.AuthResponseDTO;
import com.app.viajes.dto.LoginRequestDTO;
import com.app.viajes.dto.RegisterRequestDTO;

/**
 * Servicio de autenticacion: registro de nuevos usuarios e inicio de sesion.
 */
public interface AuthService {

    AuthResponseDTO registrar(RegisterRequestDTO request);

    AuthResponseDTO login(LoginRequestDTO request);
}
