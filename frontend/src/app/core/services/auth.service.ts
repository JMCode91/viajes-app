import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { AuthResponse, LoginRequest, RegisterRequest } from '../../shared/types';

const TOKEN_KEY = 'auth_token';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly _isAuthenticated = signal<boolean>(
    !!localStorage.getItem(TOKEN_KEY)
  );

  readonly isAuthenticated = computed(() => this._isAuthenticated());

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
    this._isAuthenticated.set(false);
  }

  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  private saveAuth(response: AuthResponse): void {
    localStorage.setItem(TOKEN_KEY, response.token);
    this._isAuthenticated.set(true);
  }
}