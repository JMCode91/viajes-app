package com.app.viajes.dto.request;

import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para actualizar las preferencias de notificaciones
 * del usuario autenticado.
 */
public record PreferenciasNotificacionRequest(
        @NotNull(message = "notificacionesGastos es obligatorio")
        Boolean notificacionesGastos,

        @NotNull(message = "notificacionesPlanes es obligatorio")
        Boolean notificacionesPlanes
) {
}