package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando falla el servicio de conversion de divisas (Frankfurter).
 */
public class DivisaException extends RuntimeException {

    public DivisaException(String message) {
        super(message);
    }

    public DivisaException(String message, Throwable cause) {
        super(message, cause);
    }
}
