package com.app.viajes.service.impl;

import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.model.Usuario;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.service.ArchivoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Implementacion del servicio de archivos seguros.
 * Guarda archivos de forma aislada por viaje y valida la pertenencia
 * del usuario autenticado antes de subir o descargar.
 */
@Service
public class ArchivoServiceImpl implements ArchivoService {

    /** Extensiones permitidas (whitelist) para evitar ejecucion de codigo malicioso. */
    private static final Set<String> EXTENSIONES_PERMITIDAS = Set.of(
            "pdf", "jpg", "jpeg", "png", "gif", "webp",
            "doc", "docx", "xls", "xlsx", "csv", "txt");

    private final Path directorioRaiz;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;

    public ArchivoServiceImpl(@Value("${app.upload.dir}") String directorioRaiz,
                              UsuarioRepository usuarioRepository,
                              UsuarioViajeRepository usuarioViajeRepository) {
        this.directorioRaiz = Paths.get(directorioRaiz).toAbsolutePath().normalize();
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
    }

    @Override
    public String guardarArchivo(UUID viajeId, MultipartFile archivo) {
        verificarPertenencia(viajeId);

        if (archivo == null || archivo.isEmpty()) {
            throw new AccesoDenegadoException("El archivo esta vacio");
        }

        String extension = obtenerExtension(archivo.getOriginalFilename());
        if (!EXTENSIONES_PERMITIDAS.contains(extension)) {
            throw new AccesoDenegadoException(
                    "Tipo de archivo no permitido. Extensiones validas: " + EXTENSIONES_PERMITIDAS);
        }

        String nombreGenerado = UUID.randomUUID() + "." + extension;
        Path directorioViaje = directorioRaiz.resolve(viajeId.toString()).normalize();

        try {
            Files.createDirectories(directorioViaje);
            Path destino = directorioViaje.resolve(nombreGenerado).normalize();
            // Evita que el destino escape del directorio aislado del viaje.
            if (!destino.startsWith(directorioViaje)) {
                throw new AccesoDenegadoException("Nombre de archivo invalido");
            }
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new AccesoDenegadoException("No se pudo guardar el archivo");
        }
        return nombreGenerado;
    }

    @Override
    public Resource cargarArchivo(UUID viajeId, String nombreArchivo) {
        verificarPertenencia(viajeId);

        if (!StringUtils.hasText(nombreArchivo)) {
            throw new AccesoDenegadoException("Nombre de archivo invalido");
        }

        Path directorioViaje = directorioRaiz.resolve(viajeId.toString()).normalize();
        Path ruta = directorioViaje.resolve(nombreArchivo).normalize();

        // Evita la descarga de archivos fuera del directorio del viaje (path traversal).
        if (!ruta.startsWith(directorioViaje)) {
            throw new AccesoDenegadoException("Nombre de archivo invalido");
        }

        try {
            Resource resource = new UrlResource(ruta.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new AccesoDenegadoException("Archivo no encontrado");
        } catch (MalformedURLException ex) {
            throw new AccesoDenegadoException("Nombre de archivo invalido");
        }
    }

    private String obtenerExtension(String nombreOriginal) {
        if (!StringUtils.hasText(nombreOriginal)) {
            return "";
        }
        String extension = StringUtils.getFilenameExtension(nombreOriginal);
        return extension == null ? "" : extension.toLowerCase();
    }

    private void verificarPertenencia(UUID viajeId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
        if (!usuarioViajeRepository.existsByUsuarioIdAndViajeId(usuario.getId(), viajeId)) {
            throw new AccesoDenegadoException("No tiene acceso a este viaje");
        }
    }
}