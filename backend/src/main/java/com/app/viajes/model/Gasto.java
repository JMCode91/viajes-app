package com.app.viajes.model;

import com.app.viajes.model.enums.TipoDivision;
import com.app.viajes.model.enums.TipoGasto;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad principal del modulo de Finanzas y Gastos Compartidos.
 * Representa un gasto del viaje, quien lo abono (pagador) y como se
 * divide entre los participantes. La division de un gasto PERSONAL queda
 * sin {@code tipoDivision} (null) y no genera deudas.
 */
@Entity
@Table(name = "gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Gasto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    /**
     * Usuario que abono el gasto. La pertenencia del pagador al viaje se
     * valida en la capa de servicio antes de persistir.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pagador_id", nullable = false)
    private Usuario pagador;

    @Column(name = "concepto", nullable = false, length = 200)
    private String concepto;

    @Column(name = "importe", nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_gasto", nullable = false, length = 20)
    private TipoGasto tipoGasto;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_division", length = 30)
    private TipoDivision tipoDivision;
}