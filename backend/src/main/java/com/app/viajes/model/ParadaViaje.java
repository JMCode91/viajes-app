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

import java.time.LocalDate;

/**
 * Parada (ciudad/destino) de un viaje multidestino. Cada viaje contiene una
 * lista ordenada de paradas que definen su ruta geografica (ej. Tokio ->
 * Kioto -> Osaka), con las coordenadas GPS necesarias para geolocalizar el
 * clima y los mapas, y las fechas de llegada y salida de cada destino.
 *
 * <p>La relacion {@code @ManyToOne} con {@link Viaje} es LAZY como el resto
 * de relaciones del proyecto; la coleccion del lado propietario vive en
 * {@code Viaje.paradas} (cascade ALL + orphanRemoval).
 */
@Entity
@Table(name = "paradas_viaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParadaViaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre_ciudad", nullable = false, length = 200)
    private String nombreCiudad;

    @Column(name = "latitud")
    private Double latitud;

    @Column(name = "longitud")
    private Double longitud;

    @Column(name = "fecha_llegada", nullable = false)
    private LocalDate fechaLlegada;

    @Column(name = "fecha_salida", nullable = false)
    private LocalDate fechaSalida;

    @Column(name = "orden", nullable = false)
    private Integer orden;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viaje_id", nullable = false)
    private Viaje viaje;
}
