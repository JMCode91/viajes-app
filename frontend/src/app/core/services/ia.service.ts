import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Viaje } from '../../shared/types';

export interface IaPrompt {
  destino: string;
  dias: number;
  tematica: string;
}

export interface IaItinerario {
  rawJson: string;
}

export interface DiaIaDTO {
  dia: number;
  titulo: string;
  actividades: string[];
}

export interface IaGuardarViajeRequest {
  destino: string;
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
