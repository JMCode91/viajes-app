import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

import { Viaje, ViajeRequest } from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class ViajeService {
  private readonly http = inject(HttpClient);

  readonly viajes = signal<Viaje[]>([]);

  readonly viajeDetalle = signal<Viaje | null>(null);

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

  unirseViaje(uuid: string): Observable<void> {
    return this.http.put<void>(`/api/viajes/${uuid}/responder-invitacion`, null, {
      params: { aceptar: 'true' },
    });
  }
}