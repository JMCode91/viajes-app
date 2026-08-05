package com.app.viajes.repository;

import com.app.viajes.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Actividad}.
 */
public interface ActividadRepository extends JpaRepository<Actividad, UUID> {

    List<Actividad> findByViajeIdOrderByFechaHoraInicioAsc(UUID viajeId);

    @Query("SELECT a FROM Actividad a WHERE a.viaje.id = :viajeId " +
            "AND a.fechaHoraInicio BETWEEN :inicio AND :fin " +
            "ORDER BY a.fechaHoraInicio ASC")
    List<Actividad> findByViajeIdAndFecha(
            @Param("viajeId") UUID viajeId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}