import { Component, computed, effect, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { ViajeService } from '../../core/services/viaje.service';
import { GastoService } from '../../core/services/gasto.service';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import { UUID, Viaje } from '../../shared/types';

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
  private readonly authService = inject(AuthService);
  private readonly themeService = inject(ThemeService);

  protected readonly viajes = this.viajeService.viajes;
  protected readonly gastos = this.gastoService.gastos;
  protected readonly usuario = this.authService.usuario;
  protected readonly theme = this.themeService.theme;

  protected readonly viajeSeleccionado = signal<UUID | null>(null);
  protected readonly modalAbierto = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly viajeActual = computed<Viaje | null>(() => {
    const viajes = this.viajes();
    const seleccion = this.viajeSeleccionado();
    return (
      viajes.find((viaje) => viaje.id === seleccion) ?? viajes[0] ?? null
    );
  });

  protected readonly presupuestoBase = computed<number>(
    () => this.viajeActual()?.presupuestoBase ?? 0
  );

  protected readonly totalGastado = computed(() =>
    this.gastos().reduce((sum, gasto) => sum + (gasto.cantidad ?? 0), 0)
  );

  protected readonly porcentajePresupuesto = computed(() => {
    const base = this.presupuestoBase();
    if (base <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((this.totalGastado() / base) * 100));
  });

  protected readonly pagadorActual = computed(() => {
    const usuario = this.usuario();
    return usuario ? `${usuario.nombre}` : 'Yo';
  });

  protected readonly formGasto = this.fb.nonNullable.group({
    concepto: ['', [Validators.required, Validators.maxLength(200)]],
    cantidad: [0, [Validators.required, Validators.min(0)]],
    pagadorId: ['', Validators.required],
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

  protected toggleTheme(): void {
    this.themeService.toggle();
  }

  protected abrirModal(): void {
    const usuario = this.usuario();
    this.errorForm.set(null);
    this.formGasto.reset({
      concepto: '',
      cantidad: 0,
      pagadorId: usuario?.email ?? '',
    });
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  protected hasError(field: 'concepto' | 'cantidad' | 'pagadorId'): boolean {
    const control = this.formGasto.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected guardarGasto(): void {
    const viajeId = this.viajeSeleccionado();
    if (!viajeId || this.formGasto.invalid) {
      this.formGasto.markAllAsTouched();
      return;
    }

    const { concepto, cantidad, pagadorId } = this.formGasto.getRawValue();
    const usuario = this.usuario();
    this.guardando.set(true);
    this.errorForm.set(null);

    this.gastoService
      .crearGasto(viajeId, {
        concepto,
        cantidad,
        pagadorId: usuario?.email
          ? this.gastoService.pagadorAutenticado(
              usuario.email,
              usuario.nombre
            )
          : pagadorId,
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