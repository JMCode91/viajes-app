package com.app.viajes.repository;

import com.app.viajes.model.Foto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Foto}.
 */
public interface FotoRepository extends JpaRepository<Foto, UUID> {

    List<Foto> findByViajeIdOrderByFechaSubidaDesc(UUID viajeId);

    List<Foto> findByViajeIdAndAlbumIdOrderByFechaSubidaDesc(UUID viajeId, UUID albumId);

    List<Foto> findByAlbumId(UUID albumId);

    List<Foto> findByAlbumIdOrderByFechaSubidaDesc(UUID albumId);

    /**
     * Obtiene todas las fotos de un viaje. Usado para purgar fisicamente
     * los archivos de Cloudflare R2 antes del borrado en cascada del viaje.
     */
    List<Foto> findByViajeId(UUID viajeId);

    /**
     * Elimina todas las fotos de un viaje.
     * Usado en el borrado en cascada manual de {@code ViajeService}.
     */
    @Modifying
    @Query("DELETE FROM Foto f WHERE f.viaje.id = :viajeId")
    void deleteByViajeId(@Param("viajeId") UUID viajeId);
}