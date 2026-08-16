package com.app.viajes.model;

import com.app.viajes.model.enums.Rol;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad que representa a un usuario registrado en la aplicacion.
 */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "fondo_pantalla_url", length = 500)
    private String fondoPantallaUrl;

    @Column(name = "notificaciones_gastos", nullable = false, columnDefinition = "boolean default true")
    private boolean notificacionesGastos = true;

    @Column(name = "notificaciones_planes", nullable = false, columnDefinition = "boolean default true")
    private boolean notificacionesPlanes = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private Rol rol = Rol.USER;

    /**
     * Indica si el usuario ha confirmado su registro por email.
     * Un usuario recien registrado nace con {@code activo = false} y pasa a
     * {@code true} cuando verifica su cuenta mediante el token de confirmacion.
     */
    @Column(name = "activo", nullable = false, columnDefinition = "boolean default false")
    private boolean activo = false;
}
