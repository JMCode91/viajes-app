import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  CambiarPasswordRequest,
  PreferenciasNotificacionRequest,
  UsuarioPerfil,
} from '../../shared/types';

/**
 * Servicio de perfil del usuario autenticado.
 * Consume los endpoints /api/usuarios/me del backend (Spring Boot).
 */
@Injectable({ providedIn: 'root' })
export class UsuarioService {
  private readonly http = inject(HttpClient);

  /** GET /api/usuarios/me -> datos del perfil del usuario autenticado. */
  obtenerPerfil(): Observable<UsuarioPerfil> {
    return this.http.get<UsuarioPerfil>('/api/usuarios/me');
  }

  /** PUT /api/usuarios/me/password -> cambia la contrasena (BCrypt en el backend). */
  cambiarPassword(datos: CambiarPasswordRequest): Observable<void> {
    return this.http.put<void>('/api/usuarios/me/password', datos);
  }

  /** PUT /api/usuarios/me/preferencias -> guarda las preferencias de notificaciones. */
  actualizarPreferencias(
    preferencias: PreferenciasNotificacionRequest
  ): Observable<UsuarioPerfil> {
    return this.http.put<UsuarioPerfil>(
      '/api/usuarios/me/preferencias',
      preferencias
    );
  }
}