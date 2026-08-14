package com.app.viajes.security;

import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.model.Usuario;
import com.app.viajes.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Utilidad de seguridad que centraliza la extraccion del usuario autenticado
 * desde el {@link SecurityContextHolder} (JWT). Elimina la duplicacion (DRY)
 * del helper que antes se repetia en casi todos los servicios del proyecto.
 */
@Component
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public SecurityUtils(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Devuelve el email del usuario autenticado (el principal de la autenticacion).
     */
    public String getEmailAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * Devuelve la entidad {@link Usuario} del usuario autenticado, o lanza
     * {@link UsuarioNoEncontradoException} si no existe en la base de datos.
     */
    public Usuario getUsuarioAutenticado() {
        return usuarioRepository.findByEmail(getEmailAutenticado())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }
}