import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Viaje, ViajeRequest } from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class ViajeService {
  private readonly http = inject(HttpClient);

  readonly viajes = signal<Viaje[]>([]);

  getMisViajes(): Observable<Viaje[]> {
    return this.http.get<Viaje[]>(`/api/viajes`);
  }

  cargarMisViajes(): void {
    this.getMisViajes().subscribe({
      next: (viajes) => this.viajes.set(viajes),
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