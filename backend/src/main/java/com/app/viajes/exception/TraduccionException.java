package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando falla el servicio de traduccion (DeepL).
 */
public class TraduccionException extends RuntimeException {

    public TraduccionException(String message) {
        super(message);
    }

    public TraduccionException(String message, Throwable cause) {
        super(message, cause);
    }
}