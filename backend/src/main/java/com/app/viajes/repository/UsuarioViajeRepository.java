package com.app.viajes.repository;

import com.app.viajes.model.UsuarioViaje;
import com.app.viajes.model.enums.EstadoInvitacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la tabla intermedia {@link UsuarioViaje}.
 * Es la pieza clave de la seguridad: permite verificar que un usuario
 * pertenece a un viaje antes de autorizar cualquier operacion.
 */
public interface UsuarioViajeRepository extends JpaRepository<UsuarioViaje, UUID> {

    boolean existsByUsuarioIdAndViajeId(UUID usuarioId, UUID viajeId);

    /**
     * Verifica que el usuario es un participante ACTIVO y ACEPTADO del viaje.
     * Es el chequeo estricto anti-IDOR: excluye pertenencias PENDIENTES,
     * RECHAZADAS o abandonadas (activo=false).
     */
    boolean existsByUsuarioIdAndViajeIdAndEstadoInvitacionAndActivoTrue(
            UUID usuarioId, UUID viajeId, EstadoInvitacion estadoInvitacion);

    Optional<UsuarioViaje> findByUsuarioIdAndViajeId(UUID usuarioId, UUID viajeId);

    List<UsuarioViaje> findByViajeId(UUID viajeId);

    List<UsuarioViaje> findByUsuarioIdAndEstadoInvitacion(UUID usuarioId, EstadoInvitacion estadoInvitacion);

    /**
     * Participantes ACEPTADOS y activos de un viaje (excluye abandonos).
     * Usado para repartir los gastos compartidos a partes iguales.
     */
    List<UsuarioViaje> findByViajeIdAndEstadoInvitacionAndActivoTrue(UUID viajeId, EstadoInvitacion estadoInvitacion);

    void deleteByViajeId(UUID viajeId);
}
