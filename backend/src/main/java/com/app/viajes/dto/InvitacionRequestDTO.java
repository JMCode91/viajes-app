package com.app.viajes.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de entrada para invitar a un usuario a un viaje.
 */
public record InvitacionRequestDTO(
        @NotBlank(message = "El email del invitado es obligatorio")
        @Email(message = "El email no tiene un formato valido")
        String emailInvitado
) {
}