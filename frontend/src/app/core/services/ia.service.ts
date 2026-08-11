import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface IaPrompt {
  destino: string;
  dias: number;
  tematica: string;
}

export interface IaItinerario {
  rawJson: string;
}

@Injectable({ providedIn: 'root' })
export class IaService {
  private readonly http = inject(HttpClient);

  generarItinerario(prompt: IaPrompt): Observable<IaItinerario> {
    return this.http.post<IaItinerario>('/api/ia/generar', prompt);
  }
}
