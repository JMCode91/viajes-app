import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { IaService } from '../../core/services/ia.service';

interface DiaItinerario {
  dia: number;
  titulo: string;
  actividades: string[];
}

@Component({
  selector: 'app-ia',
  imports: [FormsModule],
  templateUrl: './ia.html',
})
export class Ia {
  private readonly iaService = inject(IaService);

  protected readonly destino = signal('');
  protected readonly dias = signal(3);
  protected readonly tematica = signal('');
  protected readonly cargando = signal(false);
  protected readonly itinerarioGenerado = signal<DiaItinerario[]>([]);
  protected readonly error = signal<string | null>(null);

  protected generarItinerario(): void {
    this.error.set(null);
    this.cargando.set(true);

    this.iaService
      .generarItinerario({
        destino: this.destino().trim(),
        dias: this.dias(),
        tematica: this.tematica().trim(),
      })
      .subscribe({
        next: (res) => {
          this.cargando.set(false);
          try {
            this.itinerarioGenerado.set(JSON.parse(res.rawJson) as DiaItinerario[]);
          } catch (ex) {
            console.error('Error al parsear el itinerario de Gemini', ex);
            alert('No se pudo interpretar la respuesta de la IA. Inténtalo de nuevo.');
            this.itinerarioGenerado.set([]);
          }
        },
        error: () => {
          this.cargando.set(false);
          this.itinerarioGenerado.set([]);
          this.error.set('No se pudo generar el itinerario. Inténtalo de nuevo.');
        },
      });
  }
}
