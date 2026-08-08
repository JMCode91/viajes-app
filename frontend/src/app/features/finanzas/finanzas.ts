import { Component, computed, effect, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { ViajeService } from '../../core/services/viaje.service';
import { GastoService } from '../../core/services/gasto.service';
import { UUID } from '../../shared/types';

@Component({
  selector: 'app-finanzas',
  imports: [CurrencyPipe, ReactiveFormsModule],
  templateUrl: './finanzas.html',
  styleUrl: './finanzas.css',
})
export class Finanzas {
  private readonly fb = inject(FormBuilder);
  private readonly viajeService = inject(ViajeService);
  private readonly gastoService = inject(GastoService);

  protected readonly viajes = this.viajeService.viajes;
  protected readonly gastos = this.gastoService.gastos;

  protected readonly viajeSeleccionado = signal<UUID | null>(null);
  protected readonly modalAbierto = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly totalGastado = computed(() =>
    this.gastos().reduce((sum, gasto) => sum + (gasto.cantidad ?? 0), 0)
  );

  protected readonly formGasto = this.fb.nonNullable.group({
    concepto: ['', [Validators.required, Validators.maxLength(200)]],
    cantidad: [0, [Validators.required, Validators.min(0)]],
    pagadoPor: ['Yo', Validators.required],
  });

  constructor() {
    this.viajeService.cargarMisViajes();

    effect(() => {
      const viajes = this.viajes();
      const seleccion = this.viajeSeleccionado();
      const valido = viajes.some((viaje) => viaje.id === seleccion);
      if (viajes.length > 0 && !valido) {
        this.viajeSeleccionado.set(viajes[0].id);
        this.gastoService.cargarGastos(viajes[0].id);
      }
    });
  }

  protected nombrePagador(id: UUID): string {
    return this.gastoService.nombrePagador(id);
  }

  protected inicialPagador(id: UUID): string {
    return (this.nombrePagador(id) || '?').charAt(0).toUpperCase();
  }

  protected seleccionarViaje(id: string): void {
    this.viajeSeleccionado.set(id);
    this.gastoService.cargarGastos(id);
  }

  protected abrirModal(): void {
    this.errorForm.set(null);
    this.formGasto.reset({ concepto: '', cantidad: 0, pagadoPor: 'Yo' });
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  protected hasError(field: 'concepto' | 'cantidad' | 'pagadoPor'): boolean {
    const control = this.formGasto.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected guardarGasto(): void {
    const viajeId = this.viajeSeleccionado();
    if (!viajeId || this.formGasto.invalid) {
      this.formGasto.markAllAsTouched();
      return;
    }

    const { concepto, cantidad, pagadoPor } = this.formGasto.getRawValue();
    this.guardando.set(true);
    this.errorForm.set(null);

    this.gastoService
      .crearGasto(viajeId, {
        concepto,
        cantidad,
        pagadorId: this.gastoService.resolverPagador(pagadoPor),
      })
      .subscribe({
        next: () => {
          this.gastoService.cargarGastos(viajeId);
          this.guardando.set(false);
          this.cerrarModal();
        },
        error: () => {
          this.guardando.set(false);
          this.errorForm.set(
            'No se pudo registrar el gasto. Inténtalo de nuevo.'
          );
        },
      });
  }
}