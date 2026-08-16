package com.app.viajes.service.impl;

import com.app.viajes.exception.DatosInvalidosException;
import com.app.viajes.exception.StorageException;
import com.app.viajes.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
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
@Slf4j
@Service
public class StorageServiceImpl implements StorageService {

    /** Extensiones permitidas para documentos (whitelist estricta). */
    private static final Set<String> EXTENSIONES_DOCUMENTOS = Set.of(
            "pdf", "jpg", "jpeg", "png");

    /** Extensiones permitidas para imagenes de avatar, galeria y portada. */
    private static final Set<String> EXTENSIONES_IMAGENES = Set.of(
            "jpg", "jpeg", "png", "gif", "webp");

    /** Prefijos MIME aceptados para documentos (imagenes + PDF). */
    private static final Set<String> PREFIJOS_MIME_DOCUMENTOS = Set.of(
            "image/", "application/pdf");

    /** Prefijos MIME aceptados para imagenes. */
    private static final Set<String> PREFIJOS_MIME_IMAGENES = Set.of(
            "image/");

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
        return subir(archivo, EXTENSIONES_DOCUMENTOS, PREFIJOS_MIME_DOCUMENTOS,
                "Formato de archivo no permitido. Formatos validos: PDF, JPG, JPEG, PNG");
    }

    @Override
    public String subirImagen(MultipartFile archivo) {
        return subir(archivo, EXTENSIONES_IMAGENES, PREFIJOS_MIME_IMAGENES,
                "Formato de imagen no permitido. Formatos validos: JPG, JPEG, PNG, GIF, WEBP");
    }

    /**
     * Valida el archivo contra una lista blanca (extension + prefijo MIME) y lo
     * sube a Cloudflare R2. Si no cumple la validacion lanza un 400.
     */
    private String subir(MultipartFile archivo, Set<String> extensionesPermitidas,
                         Set<String> prefijosMime, String mensajeError) {
        String contentType = validarArchivo(archivo, extensionesPermitidas, prefijosMime, mensajeError);

        String nombreGenerado = generarNombreUnico(obtenerExtension(archivo.getOriginalFilename()));
        subirAlBucket(nombreGenerado, contentType, archivo);

        return publicUrlPrefix + "/" + nombreGenerado;
    }

    /**
     * Valida el archivo contra la whitelist: extension permitida Y prefijo MIME
     * declarado acorde a esa familia. Devuelve el contentType normalizado.
     */
    private String validarArchivo(MultipartFile archivo,
                                  Set<String> extensionesPermitidas,
                                  Set<String> prefijosMime,
                                  String mensajeError) {
        if (archivo == null || archivo.isEmpty()) {
            throw new DatosInvalidosException("El archivo esta vacio");
        }

        String extension = obtenerExtension(archivo.getOriginalFilename());
        String contentType = obtenerContentType(archivo.getContentType());

        // Validacion relajada: la extension debe estar en la lista blanca Y el
        // MIME declarado debe empezar por uno de los prefijos permitidos. No se
        // compara el MIME exacto, de modo que variaciones como image/jpg (que
        // algunos navegadores/SO envian en lugar de image/jpeg) no rechazan una
        // subida valida.
        if (!extensionesPermitidas.contains(extension)
                || prefijosMime.stream().noneMatch(contentType::startsWith)) {
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
            // Restaurado a fromInputStream para solucionar el Timeout de red
            s3Client.putObject(request, RequestBody.fromInputStream(
                    archivo.getInputStream(), archivo.getSize()));
        } catch (IOException ex) {
            throw new StorageException("No se pudo leer el documento para subir a Cloudflare R2");
        } catch (Exception ex) {
            log.error("Fallo critico al subir a Cloudflare R2: ", ex);
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
            log.error("Fallo critico al eliminar de Cloudflare R2: ", ex);
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