package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando no se encuentra un usuario en el sistema.
 */
public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}