package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando falla el servicio de clima (Open-Meteo).
 */
public class ClimaException extends RuntimeException {

    public ClimaException(String message) {
        super(message);
    }

    public ClimaException(String message, Throwable cause) {
        super(message, cause);
    }
}
