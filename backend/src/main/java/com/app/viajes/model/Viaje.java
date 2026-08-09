package com.app.viajes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad que representa un viaje creado por un usuario.
 */
@Entity
@Table(name = "viajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Viaje {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "presupuesto_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal presupuestoBase;

    /**
     * Presupuesto global del viaje (techo economico total).
     * Por defecto 0.0 y puede agrupar los presupuestos individuales
     * de los participantes para el modulo de Finanzas y Gastos Compartidos.
     */
    @Column(name = "presupuesto_global", nullable = false, precision = 12, scale = 2,
            columnDefinition = "numeric(12,2) default 0.0")
    private BigDecimal presupuestoGlobal = BigDecimal.ZERO;

    /**
     * URL publica (Cloudflare R2) de la foto de portada del viaje.
     * Es opcional y se gestiona desde el Panel de Ajustes del Viaje.
     */
    @Column(name = "foto_portada", length = 500)
    private String fotoPortada;
}
