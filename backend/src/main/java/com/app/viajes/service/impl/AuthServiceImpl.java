package com.app.viajes.service.impl;

import com.app.viajes.dto.AuthResponseDTO;
import com.app.viajes.dto.LoginRequestDTO;
import com.app.viajes.dto.RegisterRequestDTO;
import com.app.viajes.exception.EmailAlreadyExistsException;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.enums.Rol;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.security.JwtUtil;
import com.app.viajes.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacion del servicio de autenticacion.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationManager authenticationManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthResponseDTO registrar(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con este email");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(Rol.USER);

        usuarioRepository.save(usuario);

        String token = jwtUtil.generateToken(usuario.getEmail());
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNombre());
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Si las credenciales son invalidas, AuthenticationManager lanza
        // BadCredentialsException (mensaje generico, sin revelar cual fallo).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        String token = jwtUtil.generateToken(usuario.getEmail());
        return new AuthResponseDTO(token, usuario.getEmail(), usuario.getNombre());
    }
}
