import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { ActividadIaDTO, DiaIaDTO, GenerarItinerarioResponse, IaService, ParadaIa } from '../../core/services/ia.service';
import { ParadaViaje } from '../../shared/types';
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
  protected readonly paradasGeneradas = signal<ParadaIa[]>([]);
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
          try {
            const generado = JSON.parse(res.rawJson) as GenerarItinerarioResponse | DiaIaDTO[];
            if (Array.isArray(generado)) {
              this.itinerarioGenerado.set(generado);
              this.paradasGeneradas.set([]);
            } else if (Array.isArray(generado.itinerario)) {
              this.itinerarioGenerado.set(generado.itinerario);
              this.paradasGeneradas.set(
                Array.isArray(generado.paradas) ? generado.paradas : []
              );
            } else {
              throw new Error('Formato de respuesta inesperado');
            }
          } catch (ex) {
            console.error('Error al parsear el itinerario de Gemini', ex);
            this.error.set('No se pudo interpretar la respuesta de la IA. Inténtalo de nuevo.');
            this.itinerarioGenerado.set([]);
            this.paradasGeneradas.set([]);
          } finally {
            this.cargando.set(false);
          }
        },
        error: () => {
          this.cargando.set(false);
          this.itinerarioGenerado.set([]);
          this.paradasGeneradas.set([]);
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

    // Las paradas que devolvió la IA se envían con el orden de ruta (1..n)
    // que exige el ParadaViajeDTO del backend antes de persistir.
    const paradas: ParadaViaje[] = this.paradasGeneradas().map((parada, index) => ({
      nombreCiudad: parada.nombreCiudad,
      latitud: parada.latitud ?? null,
      longitud: parada.longitud ?? null,
      fechaLlegada: parada.fechaLlegada,
      fechaSalida: parada.fechaSalida,
      orden: index + 1,
    }));

    this.guardando.set(true);
    this.error.set(null);

    this.iaService
      .guardarViaje({
        destino: this.destino().trim(),
        fechaInicio: this.fechaInicio(),
        fechaFin: this.fechaFin(),
        itinerario,
        paradas,
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
