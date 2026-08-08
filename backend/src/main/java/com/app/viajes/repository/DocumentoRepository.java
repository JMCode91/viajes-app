package com.app.viajes.repository;

import com.app.viajes.model.Documento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Documento}.
 */
public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    /**
     * Obtiene todos los documentos de las actividades de un viaje.
     * Usado para purgar fisicamente los archivos de Cloudflare R2 antes
     * del borrado en cadena del viaje.
     */
    List<Documento> findByActividadViajeId(UUID viajeId);

    /**
     * Elimina todos los documentos de las actividades de un viaje.
     * Usado en el borrado en cascada manual de {@code ViajeService}.
     */
    @Modifying
    @Query("DELETE FROM Documento d WHERE d.actividad.viaje.id = :viajeId")
    void deleteByViajeId(@Param("viajeId") UUID viajeId);
}