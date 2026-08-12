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
 * Frase de supervivencia del diccionario. Agrupa la misma frase en espanol,
 * ingles y japones (con su romanizacion romaji) y pertenece a una
 * {@link CategoriaDiccionario}.
 *
 * <p>La relacion {@code @ManyToOne} con la categoria se mantiene en modo
 * EAGER (al contrario que el resto de relaciones LAZY del proyecto) porque los
 * endpoints exponen la entidad completa directamente, para que JSON la
 * serialice sin {@code LazyInitializationException} (open-in-view=false).
 */
@Entity
@Table(name = "frase_diccionario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FraseDiccionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "texto_espanol", nullable = false, length = 300)
    private String textoEspanol;

    @Column(name = "texto_ingles", nullable = false, length = 300)
    private String textoIngles;

    @Column(name = "texto_japones", nullable = false, length = 300)
    private String textoJapones;

    @Column(name = "texto_japones_romaji", nullable = false, length = 300)
    private String textoJaponesRomaji;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "categoria_diccionario_id", nullable = false)
    private CategoriaDiccionario categoria;
}
