package com.app.viajes.repository;

import com.app.viajes.model.DeudaGasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link DeudaGasto}.
 * Las deudas se vinculan siempre a un gasto y a un participante deudor.
 */
public interface DeudaGastoRepository extends JpaRepository<DeudaGasto, UUID> {

    /**
     * Obtiene las deudas de un gasto concreto.
     */
    List<DeudaGasto> findByGastoId(UUID gastoId);

    /**
     * Obtiene todas las deudas de los gastos de un viaje.
     */
    List<DeudaGasto> findByGastoViajeId(UUID viajeId);

    /**
     * Elimina las deudas de un gasto concreto.
     * Usado al eliminar un gasto para no violar la clave foranea.
     */
    void deleteByGastoId(UUID gastoId);

    /**
     * Elimina todas las deudas de los gastos de un viaje.
     * Usado en el borrado en cascada manual de {@code ViajeService},
     * que purga las deudas antes que los gastos.
     */
    void deleteByGastoViajeId(UUID viajeId);
}