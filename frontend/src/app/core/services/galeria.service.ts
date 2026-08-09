import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Album, AlbumRequest, Foto, UUID } from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class GaleriaService {
  private readonly http = inject(HttpClient);

  readonly albumes = signal<Album[]>([]);
  readonly fotos = signal<Foto[]>([]);

  getAlbumes(viajeId: UUID): Observable<Album[]> {
    return this.http.get<Album[]>(`/api/viajes/${viajeId}/albumes`);
  }

  cargarAlbumes(viajeId: UUID): void {
    this.getAlbumes(viajeId).subscribe({
      next: (albumes) => this.albumes.set(albumes),
    });
  }

  crearAlbum(viajeId: UUID, album: AlbumRequest): Observable<Album> {
    return this.http.post<Album>(`/api/viajes/${viajeId}/albumes`, album);
  }

  eliminarAlbum(viajeId: UUID, albumId: UUID): Observable<void> {
    return this.http.delete<void>(
      `/api/viajes/${viajeId}/albumes/${albumId}`
    );
  }

  getFotos(viajeId: UUID, albumId?: UUID | null): Observable<Foto[]> {
    return this.http.get<Foto[]>(`/api/viajes/${viajeId}/fotos`, {
      params: albumId ? { albumId } : undefined,
    });
  }

  cargarFotos(viajeId: UUID, albumId?: UUID | null): void {
    this.getFotos(viajeId, albumId).subscribe({
      next: (fotos) => this.fotos.set(fotos),
    });
  }

  subirFoto(
    viajeId: UUID,
    file: File,
    albumId?: UUID | null
  ): Observable<Foto> {
    const formData = new FormData();
    formData.append('file', file);
    if (albumId) {
      formData.append('albumId', albumId);
    }
    return this.http.post<Foto>(`/api/viajes/${viajeId}/fotos`, formData);
  }

  eliminarFoto(viajeId: UUID, fotoId: UUID): Observable<void> {
    return this.http.delete<void>(
      `/api/viajes/${viajeId}/fotos/${fotoId}`
    );
  }
}