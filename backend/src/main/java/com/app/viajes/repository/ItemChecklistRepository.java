package com.app.viajes.repository;

import com.app.viajes.model.ItemChecklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad {@link ItemChecklist}.
 */
public interface ItemChecklistRepository extends JpaRepository<ItemChecklist, UUID> {

    List<ItemChecklist> findByViajeIdOrderByNombreAsc(UUID viajeId);
}