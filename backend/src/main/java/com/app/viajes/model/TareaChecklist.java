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

/**
 * Entidad que representa una tarea de la checklist de un viaje.
 * Puede ser publica (visible para todos los miembros del viaje) o privada
 * (visible unicamente para su creador). Adicionalmente puede estar asignada
 * a un participante concreto del viaje.
 */
@Entity
@Table(name = "tareas_checklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TareaChecklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignado_id")
    private Usuario asignado;

    @Column(name = "contenido", nullable = false, length = 200)
    private String contenido;

    @Column(name = "completada", nullable = false)
    private Boolean completada = Boolean.FALSE;

    @Column(name = "es_privada", nullable = false)
    private Boolean esPrivada = Boolean.FALSE;
}