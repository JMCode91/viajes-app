package com.app.viajes.repository;

import com.app.viajes.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Album}.
 */
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    List<Album> findByViajeIdOrderByFechaCreacionDesc(UUID viajeId);

    /**
     * Elimina todos los albumes de un viaje.
     * Usado en el borrado en cascada manual de {@code ViajeService}.
     */
    @Modifying
    @Query("DELETE FROM Album a WHERE a.viaje.id = :viajeId")
    void deleteByViajeId(@Param("viajeId") UUID viajeId);
}