package com.app.viajes.repository;

import com.app.viajes.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link VerificationToken}.
 */
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    /**
     * Busca un token de verificacion por su valor (columna unica).
     *
     * @param token valor del token de confirmacion
     * @return el token encontrado o vacio si no existe
     */
    Optional<VerificationToken> findByToken(String token);
}