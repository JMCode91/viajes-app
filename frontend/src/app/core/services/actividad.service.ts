import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { delay, Observable, of } from 'rxjs';

import { Actividad, ActividadRequest, UUID } from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class ActividadService {
  private readonly http = inject(HttpClient);

  readonly actividades = signal<Actividad[]>([]);

  getActividadesPorViaje(viajeId: UUID): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(`/api/viajes/${viajeId}/actividades`);
  }

  cargarActividades(viajeId: UUID): void {
    this.getActividadesPorViaje(viajeId).subscribe({
      next: (actividades) => this.actividades.set(actividades),
    });
  }

  crearActividad(
    viajeId: UUID,
    actividad: Partial<ActividadRequest>
  ): Observable<Actividad> {
    return this.http.post<Actividad>(
      `/api/viajes/${viajeId}/actividades`,
      actividad
    );
  }

  subirDocumento(file: File): Observable<string> {
    const nombreLimpio = file.name.replace(/[^a-zA-Z0-9._-]/g, '_');
    const urlTemporal = `https://r2.placeholder.viajes.app/reservas/${nombreLimpio}`;
    return of(urlTemporal).pipe(delay(600));
  }
}