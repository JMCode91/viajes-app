package com.app.viajes.model.enums;

import java.util.Locale;

/**
 * Idiomas destino soportados por el modulo de traduccion. Permite la traduccion
 * bidireccional: el texto de entrada se deja sin fijar (las APIs externas
 * autodetectan el idioma de origen), de modo que cualquier idioma de entrada
 * puede traducirse hacia estos idiomas destino (ej. Japones -> Español).
 */
public enum IdiomaDestino {

    ES("es", "Español"),
    EN("en", "Inglés"),
    JA("ja", "Japonés");

    private final String codigo;
    private final String descripcion;

    IdiomaDestino(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Codigo en mayusculas que espera la API de DeepL (ej. ES, EN, JA). */
    public String codigoDeepL() {
        return codigo.toUpperCase(Locale.ROOT);
    }

    /**
     * Resuelve el idioma destino a partir de un codigo escrito con libertad:
     * mayusculas/minusculas o con variante regional (ej. "es", "ES", "es-ES",
     * "ja-JP"). El idioma de origen NUNCA se fija: queda para la autodeteccion
     * de la API externa.
     *
     * @throws IllegalArgumentException si el codigo no corresponde a un idioma
     *         destino soportado.
     */
    public static IdiomaDestino desdeCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El idioma destino es obligatorio");
        }
        String normalizado = codigo.trim().toLowerCase(Locale.ROOT);
        if (normalizado.length() > 2) {
            normalizado = normalizado.substring(0, 2);
        }
        for (IdiomaDestino idioma : values()) {
            if (idioma.codigo.equals(normalizado)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Idioma destino no soportado: " + codigo);
    }
}
