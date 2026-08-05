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

import java.util.UUID;

/**
 * Entidad que representa un item de la checklist de un viaje.
 */
@Entity
@Table(name = "items_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Column(name = "completado", nullable = false)
    private Boolean completado = Boolean.FALSE;
}