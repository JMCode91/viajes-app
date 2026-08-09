import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { BalanceResponse, Gasto, GastoRequest, UUID } from '../../shared/types';

const PAGADORES_KEY = 'finanzas_pagadores';

@Injectable({ providedIn: 'root' })
export class GastoService {
  private readonly http = inject(HttpClient);

  readonly gastos = signal<Gasto[]>([]);
  readonly balance = signal<BalanceResponse | null>(null);

  private readonly pagadores: Map<string, string> = new Map<string, string>();

  constructor() {
    this.cargarPagadoresPersistidos();
  }

  getGastosPorViaje(viajeId: UUID): Observable<Gasto[]> {
    return this.http.get<Gasto[]>(`/api/viajes/${viajeId}/gastos`);
  }

  cargarGastos(viajeId: UUID): void {
    this.getGastosPorViaje(viajeId).subscribe({
      next: (gastos) => this.gastos.set(gastos),
    });
  }

  getBalances(viajeId: UUID): Observable<BalanceResponse> {
    return this.http.get<BalanceResponse>(`/api/viajes/${viajeId}/balances`);
  }

  cargarBalance(viajeId: UUID): void {
    this.getBalances(viajeId).subscribe({
      next: (balance) => this.balance.set(balance),
      error: () => this.balance.set(null),
    });
  }

  crearGasto(viajeId: UUID, gasto: Partial<GastoRequest>): Observable<Gasto> {
    return this.http.post<Gasto>(`/api/viajes/${viajeId}/gastos`, gasto);
  }

  /**
   * El backend solo persiste `pagadorId` (UUID), sin nombre de la persona.
   * Este helper genera un UUID determinista por nombre y lo recuerda en
   * localStorage para poder pintar las iniciales del avatar tras recargar.
   */
  /**
   * Resuelve el `pagadorId` de un gasto desde el usuario autenticado.
   * Genera un UUID determinista a partir de su email (identificador real)
   * y guarda su nombre para pintar el avatar/etiqueta del pagador.
   */
  pagadorAutenticado(email: string, nombre: string): UUID {
    const id = this.uuidDesdeTexto(email.trim().toLowerCase());
    this.pagadores.set(id, nombre.trim() || 'Yo');
    this.persistir();
    return id;
  }

  nombrePagador(id: UUID | undefined): string {
    return [...this.pagadores].find(([k]) => k === id)?.[1] ?? 'Invitado';
  }

  private cargarPagadoresPersistidos(): void {
    try {
      const parsed = JSON.parse(
        localStorage.getItem(PAGADORES_KEY) ?? '{}'
      ) as Record<string, string>;
      for (const [id, n] of Object.entries(parsed)) {
        this.pagadores.set(id, n);
      }
    } catch {
      this.pagadores.clear();
    }
  }

  private persistir(): void {
    localStorage.setItem(
      PAGADORES_KEY,
      JSON.stringify(Object.fromEntries(this.pagadores))
    );
  }

  private uuidDesdeTexto(idUsuario: string): UUID {
    const hex = this.hash(idUsuario).toString(16).padStart(32, '0');
    return `${hex.slice(0, 8)}-${hex.slice(8, 12)}-${hex.slice(12, 16)}-${hex.slice(16, 20)}-${hex.slice(20, 32)}`;
  }

  private hash(texto: string): number {
    let h = 2166136261;
    for (let i = 0; i < texto.length; i++) {
      h ^= texto.charCodeAt(i);
      h = Math.imul(h, 16777619);
    }
    return h >>> 0;
  }
}