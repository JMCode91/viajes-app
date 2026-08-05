package com.app.viajes.repository;

import com.app.viajes.model.Diccionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link Diccionario}.
 */
public interface DiccionarioRepository extends JpaRepository<Diccionario, UUID> {

    List<Diccionario> findAllByOrderByFraseEspanolAsc();

    List<Diccionario> findByCategoriaIgnoreCaseOrderByFraseEspanolAsc(String categoria);

    List<Diccionario> findByIdiomaOrderByFraseEspanolAsc(String idioma);

    List<Diccionario> findByIdiomaAndCategoriaIgnoreCaseOrderByFraseEspanolAsc(String idioma, String categoria);
}