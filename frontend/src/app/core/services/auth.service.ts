import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/types';

const TOKEN_KEY = 'auth_token';
const USUARIO_KEY = 'auth_usuario';

export interface UsuarioSesion {
  email: string;
  nombre: string;
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

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`/api/auth/register`, userData).pipe(
      tap((response) => this.saveAuth(response))
    );
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

  private saveAuth(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    localStorage.setItem(
      USUARIO_KEY,
      JSON.stringify({ email: response.email, nombre: response.nombre })
    );
    this._isAuthenticated.set(true);
    this._usuario.set({ email: response.email, nombre: response.nombre });
  }

  private cargarUsuarioSesion(): UsuarioSesion | null {
    try {
      return JSON.parse(localStorage.getItem(USUARIO_KEY) ?? 'null') as
        | UsuarioSesion
        | null;
    } catch {
      return null;
    }
  }
}