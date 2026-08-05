package com.app.viajes.service.impl;

import com.app.viajes.dto.DiccionarioResponseDTO;
import com.app.viajes.model.Diccionario;
import com.app.viajes.repository.DiccionarioRepository;
import com.app.viajes.service.DiccionarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementacion del servicio de diccionario. Es un recurso global,
 * por lo que no requiere verificacion de pertenencia a un viaje.
 */
@Service
public class DiccionarioServiceImpl implements DiccionarioService {

    private final DiccionarioRepository diccionarioRepository;

    public DiccionarioServiceImpl(DiccionarioRepository diccionarioRepository) {
        this.diccionarioRepository = diccionarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiccionarioResponseDTO> obtenerTodo() {
        return diccionarioRepository.findAllByOrderByFraseEspanolAsc()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiccionarioResponseDTO> obtenerPorCategoria(String categoria) {
        return diccionarioRepository.findByCategoriaIgnoreCaseOrderByFraseEspanolAsc(categoria)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiccionarioResponseDTO> obtenerPorIdioma(String idioma) {
        return diccionarioRepository.findByIdiomaOrderByFraseEspanolAsc(idioma.toUpperCase())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiccionarioResponseDTO> obtenerPorIdiomaYCategoria(String idioma, String categoria) {
        return diccionarioRepository
                .findByIdiomaAndCategoriaIgnoreCaseOrderByFraseEspanolAsc(idioma.toUpperCase(), categoria)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private DiccionarioResponseDTO toResponseDTO(Diccionario entrada) {
        return new DiccionarioResponseDTO(
                entrada.getId(),
                entrada.getFraseEspanol(),
                entrada.getFraseJapones(),
                entrada.getPronunciacion(),
                entrada.getCategoria(),
                entrada.getIdioma());
    }
}