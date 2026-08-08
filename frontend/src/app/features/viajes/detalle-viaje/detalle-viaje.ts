import { Component, computed, effect, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';

import { ViajeService } from '../../../core/services/viaje.service';
import { ActividadService } from '../../../core/services/actividad.service';
import { ActividadRequest, UUID } from '../../../shared/types';

export type Tab = 'itinerario' | 'mapa' | 'checklist';

export interface TabItem {
  key: Tab;
  label: string;
  icon: string;
}

export interface Dia {
  valor: string;
  numero: number;
  etiqueta: string;
}

export interface ChecklistItem {
  id: number;
  texto: string;
  hecha: boolean;
  asignado: string | null;
}

export type ListaChecklist = 'comun' | 'privada';

@Component({
  selector: 'app-detalle-viaje',
  imports: [DatePipe, ReactiveFormsModule, FormsModule],
  templateUrl: './detalle-viaje.html',
  styleUrl: './detalle-viaje.css',
})
export class DetalleViaje {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly viajeService = inject(ViajeService);
  private readonly actividadService = inject(ActividadService);

  protected readonly viaje = this.viajeService.viajeDetalle;
  protected readonly actividades = this.actividadService.actividades;
  protected readonly tabActiva = signal<Tab>('itinerario');
  protected readonly diaSeleccionado = signal<string | null>(null);
  protected readonly modalAbierto = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly listaChecklist = signal<ListaChecklist>('comun');
  protected readonly listaComun = signal<ChecklistItem[]>([]);
  protected readonly listaPrivada = signal<ChecklistItem[]>([]);
  protected readonly nuevaTarea = signal('');
  protected readonly asignadoTarea = signal<string>('');
  protected readonly participantes = signal<string[]>([
    'Yo',
    'Lucía',
    'Marco',
  ]);
  protected readonly archivo = signal<{ nombre: string; url: string } | null>(
    null
  );
  protected readonly subiendoArchivo = signal(false);
  protected readonly modalCompartirAbierto = signal(false);

  protected readonly uuidInvitacion = computed<UUID | null>(
    () => this.viaje()?.id ?? null
  );

  private readonly viajeId: UUID | null = this.route.snapshot.paramMap.get(
    'id'
  ) as UUID | null;

  protected readonly dias = computed<Dia[]>(() => {
    const viaje = this.viaje();
    if (!viaje) {
      return [];
    }
    return this.generarDias(viaje.fechaInicio, viaje.fechaFin);
  });

  protected readonly diaActivo = computed<string | null>(
    () => this.diaSeleccionado() ?? this.dias()[0]?.valor ?? null
  );

  protected readonly actividadesDelDia = computed(() => {
    const dia = this.diaActivo();
    if (!dia) {
      return [];
    }
    return this.actividades().filter((a) =>
      a.fechaHoraInicio.startsWith(dia)
    );
  });

  protected readonly listaActiva = computed<ChecklistItem[]>(() =>
    this.listaChecklist() === 'comun'
      ? this.listaComun()
      : this.listaPrivada()
  );

  protected readonly urlDocumento = computed(() => this.archivo()?.url);

  protected readonly formActividad = this.fb.nonNullable.group({
    dia: ['', Validators.required],
    titulo: ['', [Validators.required, Validators.maxLength(200)]],
    hora: ['', Validators.required],
    ubicacion: [''],
  });

  protected readonly tabs: TabItem[] = [
    {
      key: 'itinerario',
      label: 'Itinerario',
      icon: 'M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-7.5A2.25 2.25 0 0 1 5.25 9h13.5A2.25 2.25 0 0 1 21 11.25v7.5',
    },
    {
      key: 'mapa',
      label: 'Mapa',
      icon: 'M12 21a9 9 0 1 1 0-18 9 9 0 0 1 0 18Zm-3.5-5.5 1.75-4.25 4.25-1.75-1.75 4.25-4.25 1.75Zm2-2.75 1.25-1.25',
    },
    {
      key: 'checklist',
      label: 'Checklist',
      icon: 'M9 12.75 11.25 15 15 9.75M21 12c0 1.268-.63 2.39-1.593 3.068a3.745 3.745 0 0 1-1.043 3.296 3.745 3.745 0 0 1-3.296 1.043A3.745 3.745 0 0 1 12 21c-1.268 0-2.39-.63-3.068-1.593a3.746 3.746 0 0 1-3.296-1.043 3.745 3.745 0 0 1-1.043-3.296A3.745 3.745 0 0 1 3 12c0-1.268.63-2.39 1.593-3.068a3.745 3.745 0 0 1 1.043-3.296 3.746 3.746 0 0 1 3.296-1.043A3.746 3.746 0 0 1 12 3c1.268 0 2.39.63 3.068 1.593a3.746 3.746 0 0 1 3.296 1.043 3.746 3.746 0 0 1 1.043 3.296A3.745 3.745 0 0 1 21 12Z',
    },
  ];

  constructor() {
    if (this.viajeId) {
      this.viajeService.cargarViaje(this.viajeId);
      this.actividadService.cargarActividades(this.viajeId);
    }

    effect(() => {
      const primerDia = this.dias()[0]?.valor;
      if (primerDia && !this.diaSeleccionado()) {
        this.diaSeleccionado.set(primerDia);
      }
    });
  }

  protected goBack(): void {
    void this.router.navigate(['/dashboard']);
  }

  protected setTab(tab: Tab): void {
    this.tabActiva.set(tab);
  }

  protected seleccionarDia(valor: string): void {
    this.diaSeleccionado.set(valor);
  }

  protected abrirModal(): void {
    this.errorForm.set(null);
    this.archivo.set(null);
    this.formActividad.reset();
    this.formActividad.patchValue({ dia: this.diaActivo() ?? '' });
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    this.modalAbierto.set(false);
  }

  protected abrirModalCompartir(): void {
    this.modalCompartirAbierto.set(true);
  }

  protected cerrarModalCompartir(): void {
    this.modalCompartirAbierto.set(false);
  }

  protected hasError(field: 'dia' | 'titulo' | 'hora' | 'ubicacion'): boolean {
    const control = this.formActividad.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected guardarActividad(): void {
    if (!this.viajeId || this.formActividad.invalid) {
      this.formActividad.markAllAsTouched();
      return;
    }

    const { dia, titulo, hora, ubicacion } = this.formActividad.getRawValue();
    const fechaHoraInicio = `${dia}T${hora}`;

    const request: Partial<ActividadRequest> = {
      titulo,
      tipoActividad: 'OTRO',
      fechaHoraInicio,
      fechaHoraFin: fechaHoraInicio,
      descripcion: ubicacion || undefined,
      urlDocumento: this.urlDocumento() ?? undefined,
    };

    this.guardando.set(true);
    this.errorForm.set(null);

    this.actividadService
      .crearActividad(this.viajeId, request)
      .subscribe({
        next: () => {
          this.actividadService.cargarActividades(this.viajeId!);
          this.guardando.set(false);
          this.cerrarModal();
        },
        error: () => {
          this.guardando.set(false);
          this.errorForm.set('No se pudo guardar el plan. Inténtalo de nuevo.');
        },
      });
  }

  protected onArchivoSeleccionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    if (!file) {
      return;
    }
    this.subiendoArchivo.set(true);
    this.actividadService.subirDocumento(file).subscribe({
      next: (url) => {
        this.archivo.set({ nombre: file.name, url });
        this.subiendoArchivo.set(false);
      },
      error: () => {
        this.subiendoArchivo.set(false);
      },
    });
    input.value = '';
  }

  protected quitarArchivo(): void {
    this.archivo.set(null);
  }

  protected setListaChecklist(lista: ListaChecklist): void {
    this.listaChecklist.set(lista);
  }

  protected anadirTarea(): void {
    const texto = this.nuevaTarea().trim();
    if (!texto) {
      return;
    }
    const asignado = this.listaChecklist() === 'comun'
      ? this.asignadoTarea() || 'Yo'
      : null;
    const item: ChecklistItem = { id: Date.now(), texto, hecha: false, asignado };
    if (this.listaChecklist() === 'comun') {
      this.listaComun.update((l) => [...l, item]);
    } else {
      this.listaPrivada.update((l) => [...l, item]);
    }
    this.nuevaTarea.set('');
    this.asignadoTarea.set('');
  }

  protected toggleTarea(id: number): void {
    const alternar = (item: ChecklistItem): ChecklistItem =>
      item.id === id ? { ...item, hecha: !item.hecha } : item;
    if (this.listaChecklist() === 'comun') {
      this.listaComun.update((l) => l.map(alternar));
    } else {
      this.listaPrivada.update((l) => l.map(alternar));
    }
  }

  private generarDias(inicio: string, fin: string): Dia[] {
    const start = this.fechaLocal(inicio);
    const end = this.fechaLocal(fin);

    if (!start || !end || start.getTime() > end.getTime()) {
      return [];
    }

    const dias: Dia[] = [];
    let numero = 1;

    for (let d = new Date(start.getTime()); d <= end; d.setUTCDate(d.getUTCDate() + 1)) {
      dias.push({
        valor: this.isoLocal(d),
        numero,
        etiqueta: d.toLocaleDateString('es-ES', {
          day: 'numeric',
          month: 'short',
          timeZone: 'UTC',
        }),
      });
      numero++;
    }
    return dias;
  }

  private fechaLocal(fecha: string): Date | null {
    const [anio, mes, dia] = fecha.split('-').map(Number);
    if (!anio || !mes || !dia) {
      return null;
    }
    return new Date(Date.UTC(anio, mes - 1, dia));
  }

  private isoLocal(fecha: Date): string {
    const mes = String(fecha.getUTCMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getUTCDate()).padStart(2, '0');
    return `${fecha.getUTCFullYear()}-${mes}-${dia}`;
  }
}