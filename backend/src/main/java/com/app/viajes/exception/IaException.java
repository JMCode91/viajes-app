package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando falla el servicio de generacion de itinerarios con IA (Gemini).
 */
public class IaException extends RuntimeException {

    public IaException(String message) {
        super(message);
    }

    public IaException(String message, Throwable cause) {
        super(message, cause);
    }
}
