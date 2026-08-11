package com.app.viajes.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO de entrada para persistir en la base de datos un itinerario
 * generado por IA. Contiene el destino del viaje, las fechas reales
 * del viaje y el itinerario dia a dia con sus actividades.
 */
public record IaGuardarViajeRequestDTO(
        @NotBlank(message = "El destino es obligatorio")
        @Size(max = 255, message = "El destino no puede superar 255 caracteres")
        String destino,

        @NotNull(message = "La fecha de inicio es obligatoria")
        LocalDate fechaInicio,

        @NotNull(message = "La fecha de fin es obligatoria")
        LocalDate fechaFin,

        @NotNull(message = "El itinerario es obligatorio")
        @NotEmpty(message = "El itinerario no puede estar vacio")
        @Valid
        List<DiaIaDTO> itinerario
) {
}
