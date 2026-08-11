import { Component, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { startWith } from 'rxjs';

import { ViajeService } from '../../core/services/viaje.service';
import { GastoService } from '../../core/services/gasto.service';
import { AuthService } from '../../core/services/auth.service';
import {
  DeudaResumen,
  DetalleDivision,
  Gasto,
  GastoRequest,
  TipoGasto,
  TipoDivision,
  UUID,
} from '../../shared/types';

@Component({
  selector: 'app-finanzas',
  imports: [CurrencyPipe, ReactiveFormsModule],
  templateUrl: './finanzas.html',
  styleUrl: './finanzas.css',
})
export class Finanzas {
  private readonly fb = inject(FormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly viajeService = inject(ViajeService);
  private readonly gastoService = inject(GastoService);
  private readonly authService = inject(AuthService);

  protected readonly viajeId: UUID | null = this.route.snapshot.paramMap.get(
    'id'
  ) as UUID | null;

  protected readonly gastos = this.gastoService.gastos;
  protected readonly balance = this.gastoService.balance;
  protected readonly participantes = this.viajeService.participantes;
  protected readonly usuario = this.authService.usuario;

  protected readonly vistaActiva = signal<'GRUPO' | 'PERSONAL'>('GRUPO');
  protected readonly modalAbierto = signal(false);
  protected readonly gastoEnEdicion = signal<Gasto | null>(null);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly errorBorrar = signal<string | null>(null);
  protected readonly porcentajes = signal<Partial<Record<UUID, number>>>({});
  protected readonly deudorSeleccionado = signal<UUID | null>(null);

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
      return balance?.presupuestoGlobal ?? 0;
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

  protected readonly miUsuarioId = computed<UUID | null>(() => {
    const email = this.usuario()?.email;
    if (!email) {
      return null;
    }
    return (
      this.participantes().find((p) => p.email === email)?.usuarioId ?? null
    );
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

  /**
   * Señal reactiva derivada de los cambios del formulario. Permite que las
   * previsualizaciones (computed) se recalculen en tiempo real mientras el
   * usuario edita concepto, importe, tipo de gasto o pagador.
   */
  private readonly estadoFormulario = toSignal(
    this.formGasto.valueChanges.pipe(startWith(this.formGasto.getRawValue()))
  );

  /**
   * Previsualizacion en tiempo real del reparto de un gasto COMPARTIDO:
   * aplica la misma logica de division que el backend (partes iguales,
   * porcentajes o deuda completa) y devuelve un objeto estructurado con el
   * pagador (que abona el importe total) y la lista de deudores con su
   * parte calculada. Devuelve {@code null} si no hay datos suficientes.
   */
  protected readonly previewDeudas = computed<{
    pagador: { nombre: string; importe: number };
    deudores: { nombre: string; importe: number }[];
  } | null>(() => {
    const raw = this.estadoFormulario();
    if (!raw || raw.tipoGasto !== 'COMPARTIDO') {
      return null;
    }
    const importe = Number(raw.importe);
    if (!(importe > 0)) {
      return null;
    }
    const pagadorId = raw.pagadorId;
    const participantes = this.participantes();
    const pagador = participantes.find((p) => p.usuarioId === pagadorId);
    if (!pagador) {
      return null;
    }

    // REGLA DE ORO: el pagador nunca es deudor de su propio gasto.
    const deudoresBase = participantes.filter((p) => p.usuarioId !== pagadorId);
    let deudores: { nombre: string; importe: number }[] = [];

    if (raw.tipoDivision === 'PARTES_IGUALES') {
      const n = participantes.length;
      if (n === 0) {
        return null;
      }
      const base = Math.floor((importe / n) * 100) / 100;
      const remanente = Math.round((importe - base * n) * 100) / 100;
      deudores = deudoresBase.map((p, i) => ({
        nombre: p.nombre,
        importe:
          i === deudoresBase.length - 1 && remanente > 0
            ? Math.round((base + remanente) * 100) / 100
            : base,
      }));
    } else if (raw.tipoDivision === 'PORCENTAJE') {
      // Formula estricta: (importeTotal * porcentajeAsignado) / 100.
      deudores = deudoresBase
        .filter((p) => (this.porcentajes()[p.usuarioId] ?? 0) > 0)
        .map((p) => ({
          nombre: p.nombre,
          importe: (importe * (this.porcentajes()[p.usuarioId] ?? 0)) / 100,
        }));
    } else if (raw.tipoDivision === 'DEUDA_TERCERO') {
      const deudor = participantes.find(
        (p) => p.usuarioId === this.deudorSeleccionado()
      );
      if (!deudor || deudor.usuarioId === pagadorId) {
        return null;
      }
      deudores = [{ nombre: deudor.nombre, importe }];
    }

    return {
      pagador: { nombre: pagador.nombre, importe },
      deudores,
    };
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
    if (tipoDivision === 'DEUDA_TERCERO') {
      return !!this.deudorSeleccionado();
    }
    return true;
  });

  constructor() {
    if (this.viajeId) {
      this.gastoService.cargarGastos(this.viajeId);
      this.gastoService.cargarBalance(this.viajeId);
      this.viajeService.cargarParticipantes(this.viajeId);
    }
  }

  protected goBack(): void {
    if (this.viajeId) {
      void this.router.navigate(['/viajes', this.viajeId]);
      return;
    }
    void this.router.navigate(['/dashboard']);
  }

  protected cambiarVista(vista: 'GRUPO' | 'PERSONAL'): void {
    this.vistaActiva.set(vista);
  }

  protected nombrePagador(id: UUID | undefined): string {
    return this.gastoService.nombrePagador(id);
  }

  protected inicialPagador(id: UUID | undefined): string {
    return (this.nombrePagador(id) || '?').charAt(0).toUpperCase();
  }

  protected getResumenGasto(gasto: Gasto): {
    texto: string;
    importe: number;
    color: string;
  } {
    const id = this.miUsuarioId();
    const soyPagador = id !== null && gasto.pagadorId === id;

    if (soyPagador) {
      const totalPrestado = (gasto.deudas ?? []).reduce(
        (suma, deuda) => suma + deuda.importe,
        0
      );
      if (totalPrestado > 0) {
        return { texto: 'prestaste', importe: totalPrestado, color: 'text-emerald-500' };
      }
      return { texto: 'pagaste', importe: gasto.importe, color: 'text-gray-400' };
    }

    const miDeuda = (gasto.deudas ?? []).find((d) => d.deudorId === id);
    if (miDeuda) {
      return { texto: 'pediste', importe: miDeuda.importe, color: 'text-orange-500' };
    }
    return { texto: 'no participas', importe: 0, color: 'text-gray-500' };
  }

  protected abrirModal(): void {
    this.gastoEnEdicion.set(null);
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

  protected abrirEdicion(gasto: Gasto): void {
    this.gastoEnEdicion.set(gasto);
    this.errorForm.set(null);
    this.deudorSeleccionado.set(null);

    this.formGasto.reset({
      concepto: gasto.concepto,
      importe: gasto.importe,
      tipoGasto: gasto.tipoGasto,
      tipoDivision: gasto.tipoDivision ?? 'PARTES_IGUALES',
      pagadorId: gasto.pagadorId ?? '',
    });

    // PORCENTAJE: se reconstruyen los porcentajes desde las deudas actuales
    // del gasto (el pagador queda a 0); si no hay datos, reparto equitativo.
    if (
      gasto.tipoGasto === 'COMPARTIDO' &&
      gasto.tipoDivision === 'PORCENTAJE'
    ) {
      const deudas = gasto.deudas ?? [];
      if (deudas.length > 0 && gasto.importe > 0) {
        const asignados: Record<UUID, number> = {};
        for (const deuda of deudas) {
          asignados[deuda.deudorId] =
            Math.round((deuda.importe / gasto.importe) * 10000) / 100;
        }
        const suma = Object.values(asignados).reduce((s, v) => s + v, 0);
        const factor = suma > 0 ? 100 / suma : 1;
        const final: Record<UUID, number> = {};
        for (const p of this.participantes()) {
          final[p.usuarioId] =
            Math.round((asignados[p.usuarioId] ?? 0) * factor * 100) / 100;
        }
        // Ajustar el ultimo para que la suma sea exactamente 100.
        const claves = Object.keys(final);
        const sumaRounded = Object.values(final).reduce((s, v) => s + v, 0);
        const ajuste = Math.round((100 - sumaRounded) * 100) / 100;
        if (claves.length > 0) {
          const ultimaClave = claves[claves.length - 1] as UUID;
          final[ultimaClave] =
            Math.round(((final[ultimaClave] ?? 0) + ajuste) * 100) / 100;
        }
        this.porcentajes.set(final);
      } else {
        this.repartirPorcentajesPorDefecto();
      }
    } else {
      this.repartirPorcentajesPorDefecto();
    }

    // DEUDA_TERCERO: se preselecciona el deudor actual.
    if (
      gasto.tipoGasto === 'COMPARTIDO' &&
      gasto.tipoDivision === 'DEUDA_TERCERO'
    ) {
      this.deudorSeleccionado.set(gasto.deudas?.[0]?.deudorId ?? null);
    }

    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    this.modalAbierto.set(false);
    this.gastoEnEdicion.set(null);
  }

  /**
   * Prepara el modal de nuevo gasto como "Liquidación de deuda". El que
   * debía (deudor) paga ahora un gasto del importe adeudado y el 100% se
   * asigna al acreedor (DEUDA_TERCERO), de modo que el backend registra el
   * gasto inverso y el balance compensa la deuda cruzada.
   */
  protected prepararLiquidacion(deuda: DeudaResumen): void {
    this.gastoEnEdicion.set(null);
    this.formGasto.patchValue({
      concepto: 'Liquidación / Bizum',
      importe: deuda.importe,
      pagadorId: deuda.deudorId,
      tipoGasto: 'COMPARTIDO',
      tipoDivision: 'DEUDA_TERCERO',
    });
    this.deudorSeleccionado.set(deuda.acreedorId);
    this.modalAbierto.set(true);
  }

  protected eliminarGasto(gasto: Gasto, $event: Event): void {
    $event.stopPropagation();
    const viajeId = this.viajeId;
    if (!viajeId) {
      return;
    }
    const confirmado = window.confirm(
      '¿Seguro que quieres borrar este gasto? Esta acción recalculará las deudas.'
    );
    if (!confirmado) {
      return;
    }
    this.errorBorrar.set(null);
    this.gastoService.eliminarGasto(viajeId, gasto.id).subscribe({
      next: () => {
        this.gastoService.cargarGastos(viajeId);
        this.gastoService.cargarBalance(viajeId);
      },
      error: () => {
        this.errorBorrar.set(
          'No se pudo eliminar el gasto. Inténtalo de nuevo.'
        );
      },
    });
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
    const viajeId = this.viajeId;
    if (!viajeId || !this.puedeGuardar()) {
      this.formGasto.markAllAsTouched();
      return;
    }

    const { concepto, importe, tipoGasto, tipoDivision, pagadorId } =
      this.formGasto.getRawValue();
    const enEdicion = this.gastoEnEdicion();
    this.guardando.set(true);
    this.errorForm.set(null);

    const detalles: DetalleDivision[] | undefined = this.construirDetalles(
      tipoGasto,
      tipoDivision
    );

    const payload: Partial<GastoRequest> = {
      concepto,
      importe,
      pagadorId,
      tipoGasto,
      tipoDivision: tipoGasto === 'PERSONAL' ? undefined : tipoDivision,
      detalles,
    };

    const peticion = enEdicion
      ? this.gastoService.actualizarGasto(viajeId, enEdicion.id, payload)
      : this.gastoService.crearGasto(viajeId, payload);

    peticion.subscribe({
      next: () => {
        this.gastoService.cargarGastos(viajeId);
        this.gastoService.cargarBalance(viajeId);
        this.guardando.set(false);
        this.cerrarModal();
      },
      error: () => {
        this.guardando.set(false);
        this.errorForm.set(
          enEdicion
            ? 'No se pudo actualizar el gasto. Inténtalo de nuevo.'
            : 'No se pudo registrar el gasto. Inténtalo de nuevo.'
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
    if (tipoDivision === 'DEUDA_TERCERO') {
      const deudorId = this.deudorSeleccionado();
      return deudorId ? [{ usuarioId: deudorId, valorAsignado: 100 }] : undefined;
    }
    return undefined;
  }
}