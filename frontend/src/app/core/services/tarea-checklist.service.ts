import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  TareaChecklistRequest,
  TareaChecklistResponse,
  UUID,
} from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class TareaChecklistService {
  private readonly http = inject(HttpClient);

  readonly tareas = signal<TareaChecklistResponse[]>([]);

  obtenerTareas(viajeId: UUID): Observable<TareaChecklistResponse[]> {
    return this.http.get<TareaChecklistResponse[]>(
      `/api/viajes/${viajeId}/checklist`
    );
  }

  cargarTareas(viajeId: UUID): void {
    this.obtenerTareas(viajeId).subscribe({
      next: (tareas) => this.tareas.set(tareas),
    });
  }

  crearTarea(
    viajeId: UUID,
    request: TareaChecklistRequest
  ): Observable<TareaChecklistResponse> {
    return this.http.post<TareaChecklistResponse>(
      `/api/viajes/${viajeId}/checklist`,
      request
    );
  }

  cambiarEstado(
    viajeId: UUID,
    tareaId: number,
    completada: boolean
  ): Observable<TareaChecklistResponse> {
    return this.http.put<TareaChecklistResponse>(
      `/api/viajes/${viajeId}/checklist/${tareaId}/estado`,
      null,
      { params: { completada: String(completada) } }
    );
  }

  asignarUsuario(
    viajeId: UUID,
    tareaId: number,
    usuarioId: UUID | null
  ): Observable<TareaChecklistResponse> {
    const params = usuarioId
      ? { params: { asignadoId: usuarioId } }
      : undefined;
    return this.http.put<TareaChecklistResponse>(
      `/api/viajes/${viajeId}/checklist/${tareaId}/asignar`,
      null,
      params
    );
  }

  eliminarTarea(viajeId: UUID, tareaId: number): Observable<void> {
    return this.http.delete<void>(
      `/api/viajes/${viajeId}/checklist/${tareaId}`
    );
  }
}