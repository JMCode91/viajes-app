import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { ActividadIaDTO, DiaIaDTO, IaService } from '../../core/services/ia.service';
import { CustomDatePicker } from '../../shared/components/custom-date-picker/custom-date-picker';

@Component({
  selector: 'app-ia',
  imports: [FormsModule, CustomDatePicker],
  templateUrl: './ia.html',
})
export class Ia {
  private readonly iaService = inject(IaService);
  private readonly router = inject(Router);

  protected readonly destino = signal('');
  protected readonly fechaInicio = signal('');
  protected readonly fechaFin = signal('');
  protected readonly tematica = signal('');
  protected readonly detallesAdicionales = signal('');
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly regenerando = signal<string | null>(null);
  protected readonly itinerarioGenerado = signal<DiaIaDTO[]>([]);
  protected readonly error = signal<string | null>(null);

  protected generarItinerario(): void {
    this.error.set(null);
    this.cargando.set(true);

    this.iaService
      .generarItinerario({
        destino: this.destino().trim(),
        fechaInicio: this.fechaInicio(),
        fechaFin: this.fechaFin(),
        tematica: this.tematica().trim(),
        detallesAdicionales: this.detallesAdicionales().trim(),
      })
      .subscribe({
        next: (res) => {
          this.cargando.set(false);
          try {
            this.itinerarioGenerado.set(JSON.parse(res.rawJson) as DiaIaDTO[]);
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

  protected regenerarPlan(
    diaIndex: number,
    actividadIndex: number,
    actividad: ActividadIaDTO
  ): void {
    const itinerario = this.itinerarioGenerado();
    const dia = itinerario[diaIndex];
    if (!dia || !dia.actividades[actividadIndex]) {
      return;
    }

    const key = `${diaIndex}-${actividadIndex}`;
    this.regenerando.set(key);
    this.error.set(null);

    this.iaService
      .regenerarActividad({
        destino: this.destino().trim(),
        tematica: this.tematica().trim(),
        hora: actividad.hora,
        actividadActual: actividad.titulo,
      })
      .subscribe({
        next: (nuevaActividad) => {
          this.regenerando.set(null);
          const actualizado = itinerario.map((d, di) =>
            di !== diaIndex
              ? d
              : {
                  ...d,
                  actividades: d.actividades.map((item, ai) =>
                    ai === actividadIndex ? nuevaActividad : item
                  ),
                }
          );
          this.itinerarioGenerado.set(actualizado);
        },
        error: () => {
          this.regenerando.set(null);
          this.error.set('No se pudo regenerar la actividad. Inténtalo de nuevo.');
        },
      });
  }

  protected guardarItinerario(): void {
    const itinerario = this.itinerarioGenerado();
    if (itinerario.length === 0) {
      return;
    }

    this.guardando.set(true);
    this.error.set(null);

    this.iaService
      .guardarViaje({
        destino: this.destino().trim(),
        fechaInicio: this.fechaInicio(),
        fechaFin: this.fechaFin(),
        itinerario,
      })
      .subscribe({
        next: (viaje) => {
          this.guardando.set(false);
          this.router.navigate(['/viajes', viaje.id]);
        },
        error: () => {
          this.guardando.set(false);
          this.error.set('No se pudo guardar el viaje. Inténtalo de nuevo.');
        },
      });
  }
}
