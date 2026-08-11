import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Viaje } from '../../shared/types';

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
  rating?: number;
  descripcion: string;
}

export interface DiaIaDTO {
  dia: number;
  titulo: string;
  actividades: ActividadIaDTO[];
}

export interface IaGuardarViajeRequest {
  destino: string;
  fechaInicio: string;
  fechaFin: string;
  itinerario: DiaIaDTO[];
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
}
