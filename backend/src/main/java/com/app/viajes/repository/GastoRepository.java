package com.app.viajes.repository;

import com.app.viajes.model.Gasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Gasto}.
 */
public interface GastoRepository extends JpaRepository<Gasto, UUID> {

    /**
     * Obtiene los gastos de un viaje ordenados por fecha (mas recientes primero).
     */
    List<Gasto> findByViajeIdOrderByFechaDesc(UUID viajeId);

    /**
     * Elimina todos los gastos de un viaje.
     * Usado en el borrado en cascada manual de {@code ViajeService}.
     */
    void deleteByViajeId(UUID viajeId);
}