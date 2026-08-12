package com.app.viajes.repository;

import com.app.viajes.model.FraseDiccionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link FraseDiccionario}.
 */
public interface FraseDiccionarioRepository extends JpaRepository<FraseDiccionario, Long> {

    /**
     * Devuelve las frases pertenecientes a la categoria con el id indicado.
     *
     * @param categoriaId id de la {@code CategoriaDiccionario}
     * @return frases de esa categoria
     */
    List<FraseDiccionario> findByCategoriaId(Long categoriaId);
}
