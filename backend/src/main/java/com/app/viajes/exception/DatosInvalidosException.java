package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando los datos de una solicitud no cumplen las
 * reglas de negocio (ej. porcentajes de division que no suman 100).
 * Se traduce a un HTTP 400 Bad Request.
 */
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String message) {
        super(message);
    }
}