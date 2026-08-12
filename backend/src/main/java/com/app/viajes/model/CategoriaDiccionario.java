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

/**
 * Categoria del diccionario de vocabulario de supervivencia (ej. Transporte,
 * Restaurantes, Compras, Emergencias). Agrupa las frases y aporta el icono
 * que el frontend usa en la UI (nombre de icono de Ionic/Tailwind).
 */
@Entity
@Table(name = "categoria_diccionario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDiccionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "icono", nullable = false, length = 100)
    private String icono;
}
