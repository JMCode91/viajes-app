package com.app.viajes.repository;

import com.app.viajes.model.CategoriaDiccionario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad {@link CategoriaDiccionario}.
 */
public interface CategoriaDiccionarioRepository extends JpaRepository<CategoriaDiccionario, Long> {
}
