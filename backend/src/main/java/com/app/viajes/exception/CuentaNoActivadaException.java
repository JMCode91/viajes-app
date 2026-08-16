package com.app.viajes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepcion lanzada cuando un usuario intenta iniciar sesion sin haber
 * confirmado su registro por email. Se traduce a un HTTP 403 Forbidden.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class CuentaNoActivadaException extends RuntimeException {

    public CuentaNoActivadaException(String message) {
        super(message);
    }
}