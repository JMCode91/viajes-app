package com.app.viajes.exception;

/**
 * Excepcion lanzada cuando falla la subida/lectura de un documento
 * en el almacenamiento Cloudflare R2.
 */
public class StorageException extends RuntimeException {

    public StorageException(String message) {
        super(message);
    }
}