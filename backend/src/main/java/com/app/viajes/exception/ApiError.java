package com.app.viajes.exception;

import java.time.LocalDateTime;

/**
 * Cuerpo uniforme de las respuestas de error de la API.
 */
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String error,
        String message
) {
}
