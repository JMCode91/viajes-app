package com.app.viajes.repository;

import com.app.viajes.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Gasto}.
 */
public interface GastoRepository extends JpaRepository<Gasto, UUID> {

    List<Gasto> findByViajeIdOrderByCantidadDesc(UUID viajeId);

    void deleteByViajeId(UUID viajeId);
}