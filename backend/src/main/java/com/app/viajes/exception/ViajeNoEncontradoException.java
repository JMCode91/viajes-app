package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando no se encuentra un viaje por su id.
 */
public class ViajeNoEncontradoException extends RuntimeException {

    public ViajeNoEncontradoException(String message) {
        super(message);
    }
}