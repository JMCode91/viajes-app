package com.app.viajes.service.impl;

import com.app.viajes.dto.AuthResponseDTO;
import com.app.viajes.dto.LoginRequestDTO;
import com.app.viajes.dto.MensajeResponseDTO;
import com.app.viajes.dto.RegisterRequestDTO;
import com.app.viajes.exception.CuentaNoActivadaException;
import com.app.viajes.exception.DatosInvalidosException;
import com.app.viajes.exception.EmailAlreadyExistsException;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.VerificationToken;
import com.app.viajes.model.enums.Rol;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.VerificationTokenRepository;
import com.app.viajes.security.JwtUtil;
import com.app.viajes.service.AuthService;
import com.app.viajes.service.EmailService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementacion del servicio de autenticacion con registro por email:
 * el usuario nace {@code activo = false} y solo puede iniciar sesion
 * despues de verificar su cuenta mediante el token de confirmacion.
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                           VerificationTokenRepository verificationTokenRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           AuthenticationManager authenticationManager,
                           EmailService emailService) {
        this.usuarioRepository = usuarioRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public MensajeResponseDTO registrar(RegisterRequestDTO request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Ya existe un usuario con este email");
        }

        if (!request.password().equals(request.confirmarPassword())) {
            throw new DatosInvalidosException("Las contrasenas no coinciden");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.nombre());
        usuario.setEmail(request.email());
        usuario.setPassword(passwordEncoder.encode(request.password()));
        usuario.setRol(Rol.USER);
        // El usuario se registra inactivo: debe confirmar su cuenta por email.

        usuarioRepository.save(usuario);

        VerificationToken verificationToken = new VerificationToken(
                UUID.randomUUID().toString(), usuario);
        verificationTokenRepository.save(verificationToken);

        emailService.enviarCorreoConfirmacion(usuario.getEmail(), verificationToken.getToken());

        return new MensajeResponseDTO(
                "Registro exitoso. Revisa tu correo para confirmar tu cuenta");
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        // Se comprueba la activacion antes de autenticar: un usuario no confirmado
        // no puede iniciar sesion aunque sus credenciales sean correctas.
        usuarioRepository.findByEmail(request.email())
                .filter(usuario -> !usuario.isActivo())
                .ifPresent(usuario -> {
                    throw new CuentaNoActivadaException(
                            "Cuenta no activada. Revisa tu email");
                });

        // Si las credenciales son invalidas, AuthenticationManager lanza
        // BadCredentialsException (mensaje generico, sin revelar cual fallo).
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales invalidas"));

        return construirRespuestaLogin(usuario);
    }

    @Override
    @Transactional
    public MensajeResponseDTO verificarCuenta(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new DatosInvalidosException(
                        "Token de verificacion no encontrado"));

        if (verificationToken.esExpirado()) {
            throw new DatosInvalidosException(
                    "El token de verificacion ha caducado. Solicita un nuevo enlace");
        }

        Usuario usuario = verificationToken.getUsuario();
        usuario.setActivo(true);
        usuarioRepository.save(usuario);

        // El token es de un solo uso: se elimina tras verificar la cuenta.
        verificationTokenRepository.delete(verificationToken);

        return new MensajeResponseDTO(
                "Cuenta verificada correctamente. Ya puedes iniciar sesion");
    }

    /**
     * Genera el token JWT y construye la respuesta de autenticacion con los
     * datos publicos del usuario. Se reutiliza en el login.
     */
    private AuthResponseDTO construirRespuestaLogin(Usuario usuario) {
        String token = jwtUtil.generateToken(usuario.getEmail());
        return new AuthResponseDTO(
                token, usuario.getEmail(), usuario.getNombre(), usuario.getAvatarUrl());
    }
}