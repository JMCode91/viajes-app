package com.app.viajes.repository;

import com.app.viajes.model.Viaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Viaje}.
 */
public interface ViajeRepository extends JpaRepository<Viaje, UUID> {
}
