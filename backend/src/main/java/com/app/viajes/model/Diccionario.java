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

import java.util.UUID;

/**
 * Entidad global del diccionario de vocabulario de supervivencia.
 * No esta vinculada a ningun viaje.
 */
@Entity
@Table(name = "diccionario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Diccionario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "frase_espanol", nullable = false, length = 300)
    private String fraseEspanol;

    @Column(name = "frase_japones", nullable = false, length = 300)
    private String fraseJapones;

    @Column(name = "pronunciacion", nullable = false, length = 300)
    private String pronunciacion;

    @Column(name = "categoria", length = 100)
    private String categoria;

    /**
     * Codigo ISO del idioma al que pertenece la traduccion (ej: "JA", "EN", "FR").
     */
    @Column(name = "idioma", nullable = false, length = 5)
    private String idioma;
}