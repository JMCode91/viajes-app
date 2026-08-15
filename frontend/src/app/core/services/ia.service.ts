import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ParadaViaje, Viaje } from '../../shared/types';

export interface IaPrompt {
  destino: string;
  fechaInicio: string;
  fechaFin: string;
  tematica: string;
  detallesAdicionales: string;
}

export interface IaItinerario {
  rawJson: string;
}

export interface ActividadIaDTO {
  hora: string;
  titulo: string;
  lugar: string;
  latitud?: number;
  longitud?: number;
  rating?: number;
  horario?: string;
  descripcion: string;
}

export interface IaRegenerarActividadRequestDTO {
  destino: string;
  tematica: string;
  hora: string;
  actividadActual: string;
}

export interface DiaIaDTO {
  dia: number;
  titulo: string;
  actividades: ActividadIaDTO[];
}

/** Parada (ciudad/destino) geolocalizada devuelta por la IA dentro del rawJson. */
export interface ParadaIa {
  nombreCiudad: string;
  latitud: number | null;
  longitud: number | null;
  fechaLlegada: string;
  fechaSalida: string;
}

/** Respuesta parseada del rawJson de /api/ia/generar (objeto, no array). */
export interface GenerarItinerarioResponse {
  paradas: ParadaIa[];
  itinerario: DiaIaDTO[];
}

export interface IaGuardarViajeRequest {
  destino: string;
  fechaInicio: string;
  fechaFin: string;
  itinerario: DiaIaDTO[];
  paradas: ParadaViaje[];
}

export interface TraduccionRequestDTO {
  texto: string;
  idiomaDestino: string;
}

export interface TraduccionResponseDTO {
  textoTraducido: string;
  pronunciacion: string;
}

@Injectable({ providedIn: 'root' })
export class IaService {
  private readonly http = inject(HttpClient);

  generarItinerario(prompt: IaPrompt): Observable<IaItinerario> {
    return this.http.post<IaItinerario>('/api/ia/generar', prompt);
  }

  guardarViaje(request: IaGuardarViajeRequest): Observable<Viaje> {
    return this.http.post<Viaje>('/api/ia/guardar', request);
  }

  regenerarActividad(
    request: IaRegenerarActividadRequestDTO
  ): Observable<ActividadIaDTO> {
    return this.http.post<ActividadIaDTO>(
      '/api/ia/regenerar-actividad',
      request
    );
  }

  traducirTexto(
    request: TraduccionRequestDTO
  ): Observable<TraduccionResponseDTO> {
    return this.http.post<TraduccionResponseDTO>('/api/ia/traducir', request);
  }
}
