package com.app.viajes.service.impl;

import com.app.viajes.dto.ItemChecklistRequestDTO;
import com.app.viajes.dto.ItemChecklistResponseDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.ItemChecklist;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.Viaje;
import com.app.viajes.repository.ItemChecklistRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.ChecklistService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementacion del servicio de checklist. Valida la pertenencia
 * del usuario autenticado al viaje en cada operacion.
 */
@Service
public class ChecklistServiceImpl implements ChecklistService {

    private final ItemChecklistRepository itemRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;

    public ChecklistServiceImpl(ItemChecklistRepository itemRepository,
                                ViajeRepository viajeRepository,
                                UsuarioRepository usuarioRepository,
                                UsuarioViajeRepository usuarioViajeRepository) {
        this.itemRepository = itemRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
    }

    @Override
    @Transactional
    public ItemChecklistResponseDTO crearItem(UUID viajeId, ItemChecklistRequestDTO dto) {
        verificarPertenencia(viajeId);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        ItemChecklist item = new ItemChecklist();
        item.setViaje(viaje);
        item.setNombre(dto.nombre());
        item.setCompletado(Boolean.FALSE);
        itemRepository.save(item);

        return toResponseDTO(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemChecklistResponseDTO> obtenerItems(UUID viajeId) {
        verificarPertenencia(viajeId);
        return itemRepository.findByViajeIdOrderByNombreAsc(viajeId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public ItemChecklistResponseDTO marcarCompletado(UUID viajeId, UUID itemId, boolean completado) {
        verificarPertenencia(viajeId);

        ItemChecklist item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AccesoDenegadoException("Item no encontrado"));

        if (!item.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El item no pertenece a este viaje");
        }

        item.setCompletado(completado);
        itemRepository.save(item);

        return toResponseDTO(item);
    }

    @Override
    @Transactional
    public void eliminarItem(UUID viajeId, UUID itemId) {
        verificarPertenencia(viajeId);

        ItemChecklist item = itemRepository.findById(itemId)
                .orElseThrow(() -> new AccesoDenegadoException("Item no encontrado"));

        if (!item.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El item no pertenece a este viaje");
        }

        itemRepository.delete(item);
    }

    private void verificarPertenencia(UUID viajeId) {
        Usuario usuario = obtenerUsuarioAutenticado();
        if (!usuarioViajeRepository.existsByUsuarioIdAndViajeId(usuario.getId(), viajeId)) {
            throw new AccesoDenegadoException("No tiene acceso a este viaje");
        }
    }

    private Usuario obtenerUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado"));
    }

    private ItemChecklistResponseDTO toResponseDTO(ItemChecklist item) {
        return new ItemChecklistResponseDTO(
                item.getId(),
                item.getViaje().getId(),
                item.getNombre(),
                Boolean.TRUE.equals(item.getCompletado()));
    }
}