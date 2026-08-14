package com.app.viajes.service.impl;

import com.app.viajes.exception.DatosInvalidosException;
import com.app.viajes.exception.StorageException;
import com.app.viajes.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implementacion del servicio de almacenamiento de documentos en Cloudflare R2.
 * Genera un UUID como nombre del archivo y lo sube al bucket usando el
 * cliente S3 configurado con {@link com.app.viajes.config.CloudflareR2Config}.
 * <p>
 * Seguridad anti web shell: antes de guardar se valida por Lista Blanca la
 * extension del archivo Y su tipo MIME declarado (defensa en profundidad).
 */
@Service
public class StorageServiceImpl implements StorageService {

    /** Extensiones permitidas para documentos (whitelist estricta). */
    private static final Set<String> EXTENSIONES_DOCUMENTOS = Set.of(
            "pdf", "jpg", "jpeg", "png");

    /** Extensiones permitidas para imagenes de avatar, galeria y portada. */
    private static final Set<String> EXTENSIONES_IMAGENES = Set.of(
            "jpg", "jpeg", "png", "gif", "webp");

    /** MIME oficial que debe declarar cada extension. */
    private static final Map<String, String> MIME_POR_EXTENSION = Map.of(
            "pdf", "application/pdf",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "png", "image/png",
            "gif", "image/gif",
            "webp", "image/webp");

    private final S3Client s3Client;
    private final String bucket;
    private final String publicUrlPrefix;

    public StorageServiceImpl(S3Client s3Client,
                              @Value("${cloudflare.r2.bucket}") String bucket,
                              @Value("${cloudflare.r2.public-url}") String publicUrlPrefix) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.publicUrlPrefix = publicUrlPrefix.endsWith("/")
                ? publicUrlPrefix.substring(0, publicUrlPrefix.length() - 1)
                : publicUrlPrefix;
    }

    @Override
    public String subirDocumento(MultipartFile archivo) {
        return subir(archivo, EXTENSIONES_DOCUMENTOS,
                "Formato de archivo no permitido. Formatos validos: PDF, JPG, JPEG, PNG");
    }

    @Override
    public String subirImagen(MultipartFile archivo) {
        return subir(archivo, EXTENSIONES_IMAGENES,
                "Formato de imagen no permitido. Formatos validos: JPG, JPEG, PNG, GIF, WEBP");
    }

    /**
     * Valida el archivo contra una lista blanca (extension + MIME) y lo sube
     * a Cloudflare R2. Si no cumple la validacion estricta lanza un 400.
     */
    private String subir(MultipartFile archivo, Set<String> extensionesPermitidas, String mensajeError) {
        String contentType = validarArchivo(archivo, extensionesPermitidas, mensajeError);

        String nombreGenerado = generarNombreUnico(obtenerExtension(archivo.getOriginalFilename()));
        subirAlBucket(nombreGenerado, contentType, archivo);

        return publicUrlPrefix + "/" + nombreGenerado;
    }

    /**
     * Valida el archivo contra la whitelist: extension permitida Y MIME
     * declarado acorde a esa extension. Devuelve el contentType normalizado.
     */
    private String validarArchivo(MultipartFile archivo,
                                  Set<String> extensionesPermitidas,
                                  String mensajeError) {
        if (archivo == null || archivo.isEmpty()) {
            throw new DatosInvalidosException("El archivo esta vacio");
        }

        String extension = obtenerExtension(archivo.getOriginalFilename());
        String contentType = obtenerContentType(archivo.getContentType());

        // Validacion estricta: la extension debe estar en la lista blanca Y
        // el MIME declarado debe corresponderse con esa extension.
        if (!extensionesPermitidas.contains(extension)
                || !MIME_POR_EXTENSION.getOrDefault(extension, "").equals(contentType)) {
            throw new DatosInvalidosException(mensajeError);
        }

        return contentType;
    }

    /**
     * Genera el nombre unico del objeto R2 a partir de un UUID y la extension
     * ya normalizada en minusculas.
     */
    private String generarNombreUnico(String extension) {
        return UUID.randomUUID() + "." + extension;
    }

    /**
     * Sube el archivo al bucket de Cloudflare R2 con el nombre y MIME dados.
     */
    private void subirAlBucket(String nombreGenerado, String contentType, MultipartFile archivo) {
        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(nombreGenerado)
                    .contentType(contentType)
                    .build();
            s3Client.putObject(request, RequestBody.fromInputStream(
                    archivo.getInputStream(), archivo.getSize()));
        } catch (IOException ex) {
            throw new StorageException("No se pudo leer el documento para subir a Cloudflare R2");
        } catch (Exception ex) {
            throw new StorageException("No se pudo subir el documento a Cloudflare R2");
        }
    }

    @Override
    public void eliminarDocumento(String urlArchivo) {
        if (!StringUtils.hasText(urlArchivo)) {
            return;
        }

        eliminarDelBucket(extraerClaveUrl(urlArchivo));
    }

    /**
     * Extrae la clave del objeto R2 desde la URL publica: es el ultimo
     * segmento (el nombre generado, idioma UUID.ext).
     */
    private String extraerClaveUrl(String urlArchivo) {
        String clave = urlArchivo.substring(urlArchivo.lastIndexOf('/') + 1);
        if (clave.isEmpty()) {
            throw new StorageException("No se pudo determinar el archivo a eliminar de Cloudflare R2");
        }
        return clave;
    }

    /**
     * Borra fisicamente el objeto de Cloudflare R2.
     */
    private void eliminarDelBucket(String clave) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(clave)
                    .build();
            s3Client.deleteObject(request);
        } catch (Exception ex) {
            throw new StorageException("No se pudo eliminar el documento de Cloudflare R2");
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (!StringUtils.hasText(nombreOriginal)) {
            return "";
        }
        String extension = StringUtils.getFilenameExtension(nombreOriginal);
        return extension == null ? "" : extension.toLowerCase();
    }

    private String obtenerContentType(String contentType) {
        return contentType == null ? "" : contentType.trim().toLowerCase();
    }
}