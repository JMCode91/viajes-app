import { Component, computed, effect, inject, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { ViajeService } from '../../core/services/viaje.service';
import { GastoService } from '../../core/services/gasto.service';
import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';
import {
  DeudaResumen,
  DetalleDivision,
  TipoGasto,
  TipoDivision,
  UUID,
  Viaje,
} from '../../shared/types';

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
  protected readonly balance = this.gastoService.balance;
  protected readonly participantes = this.viajeService.participantes;
  protected readonly usuario = this.authService.usuario;
  protected readonly theme = this.themeService.theme;

  protected readonly viajeSeleccionado = signal<UUID | null>(null);
  protected readonly vistaActiva = signal<'GRUPO' | 'PERSONAL'>('GRUPO');
  protected readonly modalAbierto = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly porcentajes = signal<Partial<Record<UUID, number>>>({});
  protected readonly deudorSeleccionado = signal<UUID | null>(null);

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

  protected readonly gastosVisibles = computed(() => {
    const tipo = this.vistaActiva() === 'GRUPO' ? 'COMPARTIDO' : 'PERSONAL';
    return this.gastos().filter((gasto) => gasto.tipoGasto === tipo);
  });

  protected readonly totalGastosFiltrados = computed(
    () => this.gastosVisibles().length
  );

  protected readonly totalGastadoVista = computed(() => {
    const balance = this.balance();
    if (this.vistaActiva() === 'GRUPO') {
      return balance?.totalGastadoGrupo ?? 0;
    }
    return balance?.totalGastadoUsuario ?? 0;
  });

  protected readonly presupuestoVista = computed(() => {
    const balance = this.balance();
    if (this.vistaActiva() === 'GRUPO') {
      return balance?.presupuestoGlobal ?? this.presupuestoBase();
    }
    return balance?.presupuestoIndividualUsuario ?? 0;
  });

  protected readonly porcentajeVista = computed(() => {
    const base = this.presupuestoVista();
    if (base <= 0) {
      return 0;
    }
    return Math.min(100, Math.round((this.totalGastadoVista() / base) * 100));
  });

  protected readonly deudasVisibles = computed<DeudaResumen[]>(() => {
    if (this.vistaActiva() !== 'GRUPO') {
      return [];
    }
    return this.balance()?.deudas ?? [];
  });

  protected readonly sumaPorcentajes = computed(() =>
    Object.values(this.porcentajes()).reduce<number>(
      (sum, valor) => sum + (valor ?? 0),
      0
    )
  );

  protected readonly porcentajesValidos = computed(() =>
    Math.abs(this.sumaPorcentajes() - 100) < 0.001
  );

  protected readonly formGasto = this.fb.nonNullable.group({
    concepto: ['', [Validators.required, Validators.maxLength(200)]],
    importe: [0, [Validators.required, Validators.min(0)]],
    tipoGasto: ['COMPARTIDO' as TipoGasto, Validators.required],
    tipoDivision: ['PARTES_IGUALES' as TipoDivision],
    pagadorId: ['' as string, Validators.required],
  });

  protected readonly puedeGuardar = computed(() => {
    if (this.guardando()) {
      return false;
    }
    const importe = this.formGasto.controls.importe.value;
    if (this.formGasto.invalid || !(importe > 0)) {
      return false;
    }
    const { tipoGasto, tipoDivision } = this.formGasto.getRawValue();
    if (tipoGasto !== 'COMPARTIDO') {
      return true;
    }
    if (tipoDivision === 'PORCENTAJE') {
      return this.porcentajesValidos();
    }
    if (
      tipoDivision === 'DEUDA_PAGADOR' ||
      tipoDivision === 'DEUDA_TERCERO'
    ) {
      return !!this.deudorSeleccionado();
    }
    return true;
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
        this.gastoService.cargarBalance(viajes[0].id);
        this.viajeService.cargarParticipantes(viajes[0].id);
      }
    });
  }

  protected cambiarVista(vista: 'GRUPO' | 'PERSONAL'): void {
    this.vistaActiva.set(vista);
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
    this.gastoService.cargarBalance(id);
    this.viajeService.cargarParticipantes(id);
  }

  protected toggleTheme(): void {
    this.themeService.toggle();
  }

  protected abrirModal(): void {
    const usuario = this.usuario();
    const participantes = this.participantes();
    const pagadorInicial =
      participantes.find((p) => p.email === usuario?.email)?.usuarioId ??
      participantes[0]?.usuarioId ??
      '';

    this.errorForm.set(null);
    this.deudorSeleccionado.set(null);
    this.formGasto.reset({
      concepto: '',
      importe: 0,
      tipoGasto: this.vistaActiva() === 'PERSONAL' ? 'PERSONAL' : 'COMPARTIDO',
      tipoDivision: 'PARTES_IGUALES',
      pagadorId: pagadorInicial,
    });
    this.repartirPorcentajesPorDefecto();
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  protected cambiarTipoGasto(tipo: TipoGasto): void {
    this.formGasto.patchValue({ tipoGasto: tipo });
    if (tipo === 'PERSONAL') {
      this.formGasto.patchValue({ tipoDivision: 'PARTES_IGUALES' });
      this.deudorSeleccionado.set(null);
    }
  }

  protected cambiarTipoDivision(division: TipoDivision): void {
    this.formGasto.patchValue({ tipoDivision: division });
    this.deudorSeleccionado.set(null);
  }

  protected cambiarPorcentaje(id: UUID, valor: number): void {
    const numero = Number(valor);
    const cantidad = Math.min(
      100,
      Math.max(0, Number.isFinite(numero) ? numero : 0)
    );
    this.porcentajes.update((map) => ({ ...map, [id]: cantidad }));
  }

  protected seleccionarDeudor(id: UUID): void {
    this.deudorSeleccionado.set(id);
  }

  protected repartirPorcentajesPorDefecto(): void {
    const participantes = this.participantes();
    if (participantes.length === 0) {
      this.porcentajes.set({});
      return;
    }
    const base = Math.floor((100 / participantes.length) * 100) / 100;
    const resto = Math.round((100 - base * participantes.length) * 100) / 100;
    const mapa: Record<UUID, number> = {};
    participantes.forEach((p, i) => {
      mapa[p.usuarioId] = i === participantes.length - 1
        ? Math.round((base + resto) * 100) / 100
        : base;
    });
    this.porcentajes.set(mapa);
  }

  protected hasError(
    field: 'concepto' | 'importe' | 'tipoGasto' | 'tipoDivision' | 'pagadorId'
  ): boolean {
    const control = this.formGasto.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected guardarGasto(): void {
    const viajeId = this.viajeSeleccionado();
    if (!viajeId || !this.puedeGuardar()) {
      this.formGasto.markAllAsTouched();
      return;
    }

    const { concepto, importe, tipoGasto, tipoDivision, pagadorId } =
      this.formGasto.getRawValue();
    this.guardando.set(true);
    this.errorForm.set(null);

    const detalles: DetalleDivision[] | undefined = this.construirDetalles(
      tipoGasto,
      tipoDivision
    );

    this.gastoService
      .crearGasto(viajeId, {
        concepto,
        importe,
        pagadorId,
        tipoGasto,
        tipoDivision: tipoGasto === 'PERSONAL' ? undefined : tipoDivision,
        detalles,
      })
      .subscribe({
        next: () => {
          this.gastoService.cargarGastos(viajeId);
          this.gastoService.cargarBalance(viajeId);
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

  private construirDetalles(
    tipoGasto: TipoGasto,
    tipoDivision: TipoDivision
  ): DetalleDivision[] | undefined {
    if (tipoGasto !== 'COMPARTIDO') {
      return undefined;
    }
    if (tipoDivision === 'PORCENTAJE') {
      return this.participantes()
        .filter((p) => (this.porcentajes()[p.usuarioId] ?? 0) > 0)
        .map((p) => ({
          usuarioId: p.usuarioId,
          valorAsignado: this.porcentajes()[p.usuarioId] ?? 0,
        }));
    }
    if (
      tipoDivision === 'DEUDA_PAGADOR' ||
      tipoDivision === 'DEUDA_TERCERO'
    ) {
      const deudorId = this.deudorSeleccionado();
      return deudorId ? [{ usuarioId: deudorId, valorAsignado: 100 }] : undefined;
    }
    return undefined;
  }
}