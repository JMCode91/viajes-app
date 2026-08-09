import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import {
  Participante,
  UUID,
  Viaje,
  ViajeRequest,
  ViajeUpdateRequest,
} from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class ViajeService {
  private readonly http = inject(HttpClient);

  readonly viajes = signal<Viaje[]>([]);

  readonly viajeDetalle = signal<Viaje | null>(null);

  readonly participantes = signal<Participante[]>([]);

  getMisViajes(): Observable<Viaje[]> {
    return this.http.get<Viaje[]>(`/api/viajes`);
  }

  cargarMisViajes(): void {
    this.getMisViajes().subscribe({
      next: (viajes) => this.viajes.set(viajes),
    });
  }

  getViajeById(id: string): Observable<Viaje | undefined> {
    return this.getMisViajes().pipe(
      map((viajes) => viajes.find((viaje) => viaje.id === id))
    );
  }

  cargarViaje(id: string): void {
    this.getViajeById(id).subscribe({
      next: (viaje) => this.viajeDetalle.set(viaje ?? null),
    });
  }

  crearViaje(viaje: Partial<ViajeRequest>): Observable<Viaje> {
    return this.http.post<Viaje>(`/api/viajes`, viaje);
  }

  updateViaje(id: UUID, viaje: ViajeUpdateRequest): Observable<Viaje> {
    return this.http.put<Viaje>(`/api/viajes/${id}`, viaje);
  }

  uploadPortada(id: UUID, file: File): Observable<Viaje> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Viaje>(`/api/viajes/${id}/portada`, formData);
  }

  unirseViaje(uuid: string): Observable<void> {
    return this.http.put<void>(`/api/viajes/${uuid}/responder-invitacion`, null, {
      params: { aceptar: 'true' },
    });
  }

  eliminarViaje(id: string): Observable<void> {
    return this.http.delete<void>(`/api/viajes/${id}`);
  }

  abandonarViaje(id: string): Observable<void> {
    return this.http.delete<void>(`/api/viajes/${id}/participantes/me`);
  }

  getParticipantes(viajeId: UUID): Observable<Participante[]> {
    return this.http.get<Participante[]>(
      `/api/viajes/${viajeId}/participantes`
    );
  }

  cargarParticipantes(viajeId: UUID): void {
    this.getParticipantes(viajeId).subscribe({
      next: (participantes) => this.participantes.set(participantes),
    });
  }

  kickParticipante(viajeId: UUID, usuarioId: UUID): Observable<void> {
    return this.http.delete<void>(
      `/api/viajes/${viajeId}/participantes/${usuarioId}`
    );
  }
}