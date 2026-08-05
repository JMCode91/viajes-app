package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando se intenta registrar un email ya existente.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
