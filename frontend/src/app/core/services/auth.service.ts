import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import {
  AuthResponse,
  LoginRequest,
  MensajeResponse,
  RegisterRequest,
} from '../../shared/types';

const TOKEN_KEY = 'auth_token';
const USUARIO_KEY = 'auth_usuario';

export interface UsuarioSesion {
  email: string;
  nombre: string;
  avatarUrl: string | null;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly _isAuthenticated = signal<boolean>(
    !!localStorage.getItem(TOKEN_KEY)
  );

  private readonly _usuario = signal<UsuarioSesion | null>(
    this.cargarUsuarioSesion()
  );

  readonly isAuthenticated = computed(() => this._isAuthenticated());

  readonly usuario = computed(() => this._usuario());

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`/api/auth/login`, credentials).pipe(
      tap((response) => this.saveAuth(response))
    );
  }

  register(userData: RegisterRequest): Observable<MensajeResponse> {
    return this.http.post<MensajeResponse>(`/api/auth/register`, userData);
  }

  verificarCuenta(token: string): Observable<MensajeResponse> {
    return this.http.get<MensajeResponse>(`/api/auth/verificar`, {
      params: { token },
    });
  }

  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USUARIO_KEY);
    this._isAuthenticated.set(false);
    this._usuario.set(null);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  uploadAvatar(file: File): Observable<{ avatarUrl: string | null }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<{ avatarUrl: string | null }>(`/api/usuarios/me/avatar`, formData)
      .pipe(tap((response) => this.actualizarAvatar(response.avatarUrl)));
  }

  private actualizarAvatar(avatarUrl: string | null | undefined): void {
    const usuario = this._usuario();
    if (!usuario) {
      return;
    }
    const actualizado: UsuarioSesion = {
      ...usuario,
      avatarUrl: avatarUrl ?? null,
    };
    this._usuario.set(actualizado);
    localStorage.setItem(USUARIO_KEY, JSON.stringify(actualizado));
  }

  private saveAuth(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    const usuario: UsuarioSesion = {
      email: response.email,
      nombre: response.nombre,
      avatarUrl: response.avatarUrl ?? null,
    };
    localStorage.setItem(USUARIO_KEY, JSON.stringify(usuario));
    this._isAuthenticated.set(true);
    this._usuario.set(usuario);
  }

  private cargarUsuarioSesion(): UsuarioSesion | null {
    try {
      const sesion = JSON.parse(localStorage.getItem(USUARIO_KEY) ?? 'null') as
        | UsuarioSesion
        | null;
      return sesion
        ? { email: sesion.email, nombre: sesion.nombre, avatarUrl: sesion.avatarUrl ?? null }
        : null;
    } catch {
      return null;
    }
  }
}