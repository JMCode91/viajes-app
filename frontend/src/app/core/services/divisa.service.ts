import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { DivisaResponseDTO } from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class DivisaService {
  private readonly http = inject(HttpClient);

  convertir(
    amount: number,
    from: string,
    to: string
  ): Observable<DivisaResponseDTO> {
    const params = new HttpParams()
      .set('amount', amount)
      .set('from', from)
      .set('to', to);
    return this.http.get<DivisaResponseDTO>('/api/divisas/convertir', {
      params,
    });
  }

  obtenerMonedas(): Observable<Record<string, string>> {
    return this.http.get<Record<string, string>>('/api/divisas/monedas');
  }
}
