package com.app.viajes.service.impl;

import com.app.viajes.dto.GastoRequestDTO;
import com.app.viajes.dto.GastoResponseDTO;
import com.app.viajes.exception.AccesoDenegadoException;
import com.app.viajes.exception.UsuarioNoEncontradoException;
import com.app.viajes.exception.ViajeNoEncontradoException;
import com.app.viajes.model.Gasto;
import com.app.viajes.model.Usuario;
import com.app.viajes.model.Viaje;
import com.app.viajes.repository.GastoRepository;
import com.app.viajes.repository.UsuarioRepository;
import com.app.viajes.repository.UsuarioViajeRepository;
import com.app.viajes.repository.ViajeRepository;
import com.app.viajes.service.GastoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementacion del servicio de gastos. Valida la pertenencia del
 * usuario autenticado al viaje en cada operacion.
 */
@Service
public class GastoServiceImpl implements GastoService {

    private final GastoRepository gastoRepository;
    private final ViajeRepository viajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioViajeRepository usuarioViajeRepository;

    public GastoServiceImpl(GastoRepository gastoRepository,
                            ViajeRepository viajeRepository,
                            UsuarioRepository usuarioRepository,
                            UsuarioViajeRepository usuarioViajeRepository) {
        this.gastoRepository = gastoRepository;
        this.viajeRepository = viajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.usuarioViajeRepository = usuarioViajeRepository;
    }

    @Override
    @Transactional
    public GastoResponseDTO crearGasto(UUID viajeId, GastoRequestDTO dto) {
        verificarPertenencia(viajeId);

        Viaje viaje = viajeRepository.findById(viajeId)
                .orElseThrow(() -> new ViajeNoEncontradoException("Viaje no encontrado"));

        Gasto gasto = new Gasto();
        gasto.setViaje(viaje);
        gasto.setConcepto(dto.concepto());
        gasto.setCantidad(dto.cantidad());
        gasto.setPagadorId(dto.pagadorId());
        gastoRepository.save(gasto);

        return toResponseDTO(gasto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoResponseDTO> obtenerGastos(UUID viajeId) {
        verificarPertenencia(viajeId);
        return gastoRepository.findByViajeIdOrderByCantidadDesc(viajeId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void eliminarGasto(UUID viajeId, UUID gastoId) {
        verificarPertenencia(viajeId);

        Gasto gasto = gastoRepository.findById(gastoId)
                .orElseThrow(() -> new AccesoDenegadoException("Gasto no encontrado"));

        if (!gasto.getViaje().getId().equals(viajeId)) {
            throw new AccesoDenegadoException("El gasto no pertenece a este viaje");
        }

        gastoRepository.delete(gasto);
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

    private GastoResponseDTO toResponseDTO(Gasto gasto) {
        return new GastoResponseDTO(
                gasto.getId(),
                gasto.getViaje().getId(),
                gasto.getConcepto(),
                gasto.getCantidad(),
                gasto.getPagadorId());
    }
}