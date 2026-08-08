package com.app.viajes.dto;

/**
 * DTO de respuesta de una tarea de la checklist.
 * Se expone el nombre de usuario (creador y asignado) en vez de sus IDs
 * para que el frontend pueda renderizar avatares/iniciales sin llamadas extra.
 */
public record TareaChecklistResponseDTO(
        Long id,
        String contenido,
        boolean completada,
        boolean esPrivada,
        String nombreCreador,
        String nombreAsignado
) {
}