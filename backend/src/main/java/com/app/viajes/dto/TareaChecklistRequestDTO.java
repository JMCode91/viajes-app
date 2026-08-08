package com.app.viajes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * DTO de entrada para crear o actualizar una tarea de la checklist.
 * El creador de la tarea siempre es el usuario autenticado (se obtiene
 * del token JWT). La asignacion es opcional: si {@code asignadoId} es null
 * la tarea no tiene ningun participante asignado.
 */
public record TareaChecklistRequestDTO(
        @NotBlank(message = "El contenido es obligatorio")
        @Size(max = 200, message = "El contenido no puede superar los 200 caracteres")
        String contenido,

        boolean esPrivada,

        UUID asignadoId
) {
}