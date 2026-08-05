package com.app.viajes.model;

import com.app.viajes.model.enums.EstadoInvitacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Tabla intermedia explicita que vincula a un usuario con un viaje.
 * Modela la pertenencia de un usuario a un viaje y el estado de su invitacion.
 */
@Entity
@Table(name = "usuarios_viajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioViaje {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_invitacion", nullable = false, length = 20)
    private EstadoInvitacion estadoInvitacion = EstadoInvitacion.PENDIENTE;

    @Column(name = "rol_en_viaje", nullable = false, length = 30)
    private String rolEnViaje;
}
