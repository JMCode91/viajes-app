package com.app.viajes.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de almacenamiento de documentos en Cloudflare R2.
 */
public interface StorageService {

    /**
     * Sube un documento al bucket R2 y devuelve la URL publica final.
     * Whitelist estricta anti web shell: solo PDF, JPG, JPEG y PNG, con
     * validacion del MIME declarado y de la extension del archivo.
     *
     * @param archivo documento a subir (max 5MB configurado en multipart)
     * @return URL publica concatenando {@code cloudflare.r2.public-url} + nombre generado
     * @throws com.app.viajes.exception.DatosInvalidosException si el formato no esta permitido
     */
    String subirDocumento(MultipartFile archivo);

    /**
     * Sube una imagen (avatar, galeria o portada) al bucket R2 y devuelve la
     * URL publica final. Whitelist de imagenes: JPG, JPEG, PNG, GIF y WEBP,
     * con validacion del MIME declarado y de la extension del archivo.
     *
     * @param archivo imagen a subir (max 5MB configurado en multipart)
     * @return URL publica concatenando {@code cloudflare.r2.public-url} + nombre generado
     * @throws com.app.viajes.exception.DatosInvalidosException si el formato no esta permitido
     */
    String subirImagen(MultipartFile archivo);

    /**
     * Elimina fisicamente un documento del bucket R2.
     *
     * @param urlArchivo URL publica completa del documento
     *                   ({@code public-url} + nombre generado). La clave exacta
     *                   del objeto se extrae cortando por el ultimo "/".
     */
    void eliminarDocumento(String urlArchivo);
}