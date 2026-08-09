package com.app.viajes.service;

import com.app.viajes.dto.InvitacionRequestDTO;
import com.app.viajes.dto.ParticipanteResponseDTO;
import com.app.viajes.dto.ViajeRequestDTO;
import com.app.viajes.dto.ViajeResponseDTO;
import com.app.viajes.dto.ViajeUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de gestion de viajes e invitaciones.
 * Todas las operaciones validan que el usuario autenticado
 * pertenezca al viaje (seguridad por propiedad de recursos).
 */
public interface ViajeService {

    ViajeResponseDTO crearViaje(ViajeRequestDTO request);

    List<ViajeResponseDTO> obtenerMisViajes();

    void invitarUsuario(UUID viajeId, InvitacionRequestDTO request);

    void responderInvitacion(UUID viajeId, boolean aceptar);

    void abandonarViaje(UUID viajeId);

    void eliminarViaje(UUID viajeId);

    List<ParticipanteResponseDTO> obtenerParticipantes(UUID viajeId);

    ViajeResponseDTO actualizarViaje(UUID viajeId, ViajeUpdateDTO request);

    ViajeResponseDTO actualizarFotoPortada(UUID viajeId, MultipartFile archivo);

    void expulsarParticipante(UUID viajeId, UUID usuarioId);
}