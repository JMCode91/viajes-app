package com.app.viajes.repository;

import com.app.viajes.model.TareaChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link TareaChecklist}.
 */
public interface TareaChecklistRepository extends JpaRepository<TareaChecklist, Long> {

    /**
     * Obtiene las tareas de un viaje visibles para el usuario dado:
     * las tareas publicas (esPrivada = false) o aquellas creadas por el
     * propio usuario (para no exponer las listas privadas de otros).
     */
    @Query("SELECT t FROM TareaChecklist t " +
            "WHERE t.viaje.id = :viajeId " +
            "AND (t.esPrivada = false OR t.creador.id = :usuarioId) " +
            "ORDER BY t.id ASC")
    List<TareaChecklist> findVisiblesPorViajeYUsuario(
            @Param("viajeId") UUID viajeId,
            @Param("usuarioId") UUID usuarioId);

    void deleteByViajeId(UUID viajeId);
}