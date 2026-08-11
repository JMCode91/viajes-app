import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Clima } from '../../shared/types';

@Injectable({ providedIn: 'root' })
export class ClimaService {
  private readonly http = inject(HttpClient);

  getClima(lat: number, lon: number, fecha: string): Observable<Clima> {
    return this.http.get<Clima>('/api/clima', {
      params: { lat, lon, fecha },
    });
  }
}
