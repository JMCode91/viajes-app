package com.app.viajes.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de almacenamiento de documentos en Cloudflare R2.
 */
public interface StorageService {

    /**
     * Sube un documento al bucket R2 y devuelve la URL publica final.
     * Genera un UUID como nombre de archivo preservando la extension original.
     *
     * @param archivo documento a subir (max 5MB configurado en multipart)
     * @return URL publica concatenando {@code cloudflare.r2.public-url} + nombre generado
     */
    String subirDocumento(MultipartFile archivo);

    /**
     * Elimina fisicamente un documento del bucket R2.
     *
     * @param urlArchivo URL publica completa del documento
     *                   ({@code public-url} + nombre generado). La clave exacta
     *                   del objeto se extrae cortando por el ultimo "/".
     */
    void eliminarDocumento(String urlArchivo);
}