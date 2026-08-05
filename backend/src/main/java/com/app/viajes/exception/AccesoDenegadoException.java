package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando un usuario intenta operar sobre un viaje
 * al que no pertenece o sin los permisos necesarios.
 */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String message) {
        super(message);
    }
}