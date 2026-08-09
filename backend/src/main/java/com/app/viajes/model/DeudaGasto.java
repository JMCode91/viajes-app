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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Entidad intermedia que materializa la division de un gasto compartido:
 * representa la parte concreta que debe (deuda) un participante por un
 * gasto. Un gasto COMPARTIDO genera una {@code DeudaGasto} por cada
 * participante que participa en el reparto.
 */
@Entity
@Table(name = "deudas_gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeudaGasto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gasto_id", nullable = false)
    private Gasto gasto;

    /**
     * Usuario deudor: el participante que debe esta parte del gasto.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "deudor_id", nullable = false)
    private Usuario deudor;

    /**
     * Cantidad exacta que esta persona debe de este gasto.
     */
    @Column(name = "importe_deuda", nullable = false, precision = 12, scale = 2)
    private BigDecimal importeDeuda;

    /**
     * Porcentaje asignado a este participante en la division del gasto
     * (nullable). Solo tiene sentido cuando el gasto se dividio por
     * porcentajes (TipoDivision.PORCENTAJE).
     */
    @Column(name = "porcentaje_asignado", precision = 5, scale = 2)
    private BigDecimal porcentajeAsignado;
}