package com.app.viajes.service;

import com.app.viajes.dto.DiccionarioResponseDTO;

import java.util.List;

/**
 * Servicio de diccionario de vocabulario (global, solo usuarios autenticados).
 */
public interface DiccionarioService {

    List<DiccionarioResponseDTO> obtenerTodo();

    List<DiccionarioResponseDTO> obtenerPorCategoria(String categoria);

    List<DiccionarioResponseDTO> obtenerPorIdioma(String idioma);

    List<DiccionarioResponseDTO> obtenerPorIdiomaYCategoria(String idioma, String categoria);
}