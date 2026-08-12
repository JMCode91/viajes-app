package com.app.viajes.controller;

import com.app.viajes.dto.DiccionarioResponseDTO;
import com.app.viajes.dto.TraduccionRequestDTO;
import com.app.viajes.dto.TraduccionResponseDTO;
import com.app.viajes.model.CategoriaDiccionario;
import com.app.viajes.model.FraseDiccionario;
import com.app.viajes.repository.CategoriaDiccionarioRepository;
import com.app.viajes.repository.FraseDiccionarioRepository;
import com.app.viajes.service.DiccionarioService;
import com.app.viajes.service.TraductorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador del diccionario de vocabulario.
 * Accesible para cualquier usuario autenticado con JWT.
 */
@RestController
@RequestMapping("/api/diccionario")
public class DiccionarioController {

    private final DiccionarioService diccionarioService;
    private final TraductorService traductorService;
    private final CategoriaDiccionarioRepository categoriaDiccionarioRepository;
    private final FraseDiccionarioRepository fraseDiccionarioRepository;

    public DiccionarioController(DiccionarioService diccionarioService,
                                 TraductorService traductorService,
                                 CategoriaDiccionarioRepository categoriaDiccionarioRepository,
                                 FraseDiccionarioRepository fraseDiccionarioRepository) {
        this.diccionarioService = diccionarioService;
        this.traductorService = traductorService;
        this.categoriaDiccionarioRepository = categoriaDiccionarioRepository;
        this.fraseDiccionarioRepository = fraseDiccionarioRepository;
    }

    @GetMapping("/categorias")
    public ResponseEntity<List<CategoriaDiccionario>> obtenerCategorias() {
        return ResponseEntity.ok(categoriaDiccionarioRepository.findAll());
    }

    @GetMapping("/frases/{categoriaId}")
    public ResponseEntity<List<FraseDiccionario>> obtenerFrasesPorCategoria(
            @PathVariable Long categoriaId) {
        return ResponseEntity.ok(fraseDiccionarioRepository.findByCategoriaId(categoriaId));
    }

    @GetMapping
    public ResponseEntity<List<DiccionarioResponseDTO>> obtenerEntradas(
            @RequestParam(required = false) String idioma,
            @RequestParam(required = false) String categoria) {
        if (StringUtils.hasText(idioma) && StringUtils.hasText(categoria)) {
            return ResponseEntity.ok(
                    diccionarioService.obtenerPorIdiomaYCategoria(idioma, categoria));
        }
        if (StringUtils.hasText(idioma)) {
            return ResponseEntity.ok(diccionarioService.obtenerPorIdioma(idioma));
        }
        if (StringUtils.hasText(categoria)) {
            return ResponseEntity.ok(diccionarioService.obtenerPorCategoria(categoria));
        }
        return ResponseEntity.ok(diccionarioService.obtenerTodo());
    }

    @PostMapping("/traducir")
    public ResponseEntity<TraduccionResponseDTO> traducir(
            @Valid @RequestBody TraduccionRequestDTO request) {
        return ResponseEntity.ok(traductorService.traducir(request));
    }
}