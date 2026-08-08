package com.app.viajes.service.impl;

import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.StorageException;
import com.app.viajes.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
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
 */
@Service
public class StorageServiceImpl implements StorageService {

    /** Extensiones permitidas (whitelist) de documentos de una actividad. */
    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of(
            "pdf", "jpg", "jpeg", "png", "gif", "webp",
            "doc", "docx", "xls", "xlsx", "csv", "txt");

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
        if (archivo == null || archivo.isEmpty()) {
            throw new AccesoDenegadoException("El archivo esta vacio");
        }

        String extension = obtenerExtension(archivo.getOriginalFilename());
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new AccesoDenegadoException(
                    "Tipo de archivo no permitido. Extensiones validas: " + EXTENSIONES_PERMITIDAS);
        }

        String nombreGenerado = UUID.randomUUID() + "." + extension;

        try {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(nombreGenerado)
                    .contentType(archivo.getContentType())
                    .build();
            s3Client.putObject(request, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                    archivo.getInputStream(), archivo.getSize()));
} catch (IOException ex) {
            throw new StorageException("No se pudo leer el documento para subir a Cloudflare R2");
        } catch (Exception ex) {
            throw new StorageException("No se pudo subir el documento a Cloudflare R2");
        }

        return publicUrlPrefix + "/" + nombreGenerado;
    }

    @Override
    public void eliminarDocumento(String urlArchivo) {
        if (!StringUtils.hasText(urlArchivo)) {
            return;
        }

        // La clave del objeto R2 es el nombre generado (idioma UUID.ext),
        // que es el ultimo segmento de la URL publica.
        String clave = urlArchivo.substring(urlArchivo.lastIndexOf('/') + 1);
        if (clave.isEmpty()) {
            throw new StorageException("No se pudo determinar el archivo a eliminar de Cloudflare R2");
        }

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
}