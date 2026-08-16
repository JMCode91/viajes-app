package com.app.viajes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad que representa un token de verificacion de un solo uso
 * utilizado para confirmar el registro de un {@link Usuario} por email.
 *
 * <p>Un mismo usuario puede solicitar varios tokens (por ejemplo, para
 * reenviar el correo de confirmacion), por lo que la relacion con
 * {@link Usuario} es {@code @ManyToOne}: cada token apunta a su propietario
 * y un usuario puede acumular varios tokens emitidos.</p>
 */
@Entity
@Table(name = "tokens_verificacion")
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    /**
     * Validez del token en horas desde su creacion (24 horas).
     */
    public static final long VALIDEZ_HORAS = 24L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "token", unique = true, nullable = false, length = 64)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "fecha_expiracion", nullable = false)
    private LocalDateTime fechaExpiracion;

    /**
     * Crea un token de verificacion para el usuario indicado calculando
     * automaticamente su {@link #fechaExpiracion} como 24 horas a partir
     * del momento de la creacion.
     *
     * @param token   valor unico del token de confirmacion
     * @param usuario usuario al que pertenece el token
     */
    public VerificationToken(String token, Usuario usuario) {
        this.token = token;
        this.usuario = usuario;
        this.fechaExpiracion = LocalDateTime.now().plusHours(VALIDEZ_HORAS);
    }

    /**
     * Indica si el token ha superado su fecha de expiracion.
     *
     * @return {@code true} si el token esta expirado o si su fecha de
     *         expiracion es nula
     */
    public boolean esExpirado() {
        return fechaExpiracion == null || fechaExpiracion.isBefore(LocalDateTime.now());
    }
}