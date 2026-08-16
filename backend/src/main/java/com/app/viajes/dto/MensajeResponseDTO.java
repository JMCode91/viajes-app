package com.app.viajes.dto;

/**
 * DTO de respuesta con un mensaje informativo para operaciones que no
 * devuelven datos estructurados (ej. registro pendiente de confirmacion,
 * verificacion de cuenta por email).
 */
public record MensajeResponseDTO(String mensaje) {
}