import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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

  subirDocumento(
    viajeId: UUID,
    actividadId: UUID,
    file: File
  ): Observable<Actividad> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Actividad>(
      `/api/viajes/${viajeId}/actividades/${actividadId}/documentos`,
      formData
    );
  }

  eliminarDocumento(
    viajeId: UUID,
    actividadId: UUID,
    documentoId: UUID
  ): Observable<void> {
    return this.http.delete<void>(
      `/api/viajes/${viajeId}/actividades/${actividadId}/documentos/${documentoId}`
    );
  }
}