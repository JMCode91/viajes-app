import {
  Component,
  computed,
  effect,
  inject,
  OnDestroy,
  signal,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import * as L from 'leaflet';

import { CustomTimePicker } from '../../../shared/components/custom-time-picker/custom-time-picker';
import { ActividadService } from '../../../core/services/actividad.service';
import { TareaChecklistService } from '../../../core/services/tarea-checklist.service';
import { ViajeService } from '../../../core/services/viaje.service';
import {
  Actividad,
  ActividadRequest,
  Documento,
  NominatimResult,
  TareaChecklistResponse,
  UUID,
} from '../../../shared/types';

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

export type ListaChecklist = 'comun' | 'privada';

@Component({
  selector: 'app-detalle-viaje',
  imports: [DatePipe, ReactiveFormsModule, FormsModule, CustomTimePicker],
  templateUrl: './detalle-viaje.html',
  styleUrl: './detalle-viaje.css',
})
export class DetalleViaje implements OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly http = inject(HttpClient);
  private readonly viajeService = inject(ViajeService);
  private readonly actividadService = inject(ActividadService);
  private readonly tareaChecklistService = inject(TareaChecklistService);

  protected readonly viaje = this.viajeService.viajeDetalle;
  protected readonly actividades = this.actividadService.actividades;
  protected readonly tareas = this.tareaChecklistService.tareas;
  protected readonly participantes = this.viajeService.participantes;
  protected readonly tabActiva = signal<Tab>('itinerario');
  protected readonly diaSeleccionado = signal<string | null>(null);
  protected readonly modalAbierto = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);

  protected readonly listaChecklist = signal<ListaChecklist>('comun');
  protected readonly nuevaTarea = signal('');
  protected readonly subiendoArchivo = signal(false);
  protected readonly borrandoDocumento = signal<UUID | null>(null);
  protected readonly modalCompartirAbierto = signal(false);
  protected readonly modalDetalleAbierto = signal(false);
  protected readonly actividadSeleccionada = signal<Actividad | null>(null);
  protected readonly copiado = signal(false);
  protected readonly modalEliminarAbierto = signal(false);
  protected readonly eliminando = signal(false);
  protected readonly errorEliminar = signal<string | null>(null);

  protected readonly buscandoLugar = signal(false);
  protected readonly resultadosBusqueda = signal<NominatimResult[]>([]);
  protected readonly errorBusqueda = signal<string | null>(null);

  protected readonly actividadesConCoordenadas = computed(() =>
    this.actividades().filter(
      (a) => a.latitud != null && a.longitud != null
    )
  );

  private mapa: L.Map | null = null;

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

  protected readonly listaComun = computed<TareaChecklistResponse[]>(() =>
    this.tareas().filter((t) => !t.esPrivada)
  );

  protected readonly listaPrivada = computed<TareaChecklistResponse[]>(() =>
    this.tareas().filter((t) => t.esPrivada)
  );

  protected readonly listaActiva = computed<TareaChecklistResponse[]>(() =>
    this.listaChecklist() === 'comun'
      ? this.listaComun()
      : this.listaPrivada()
  );

  protected readonly formActividad = this.fb.group({
    dia: ['', Validators.required],
    titulo: ['', [Validators.required, Validators.maxLength(200)]],
    hora: ['', Validators.required],
    ubicacion: [''],
    direccion: [''],
    latitud: [null as number | null],
    longitud: [null as number | null],
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
      this.tareaChecklistService.cargarTareas(this.viajeId);
      this.viajeService.cargarParticipantes(this.viajeId);
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
    if (tab === 'mapa') {
      // Al entrar en la pestana del mapa se (re)construye el mapa Leaflet
      // sobre el contenedor recien renderizado por el @switch.
      this.destruirMapa();
      setTimeout(() => this.montarMapa(), 0);
    }
  }

  ngOnDestroy(): void {
    this.destruirMapa();
  }

  protected seleccionarDia(valor: string): void {
    this.diaSeleccionado.set(valor);
  }

  protected abrirModal(): void {
    this.errorForm.set(null);
    this.errorBusqueda.set(null);
    this.resultadosBusqueda.set([]);
    this.formActividad.reset();
    this.formActividad.patchValue({ dia: this.diaActivo() ?? '' });
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    this.errorBusqueda.set(null);
    this.resultadosBusqueda.set([]);
    this.modalAbierto.set(false);
  }

  protected buscarLugar(): void {
    const consulta = this.formActividad.get('direccion')?.value?.trim();
    if (!consulta || this.buscandoLugar()) {
      return;
    }

    this.buscandoLugar.set(true);
    this.errorBusqueda.set(null);

    const url =
      `https://nominatim.openstreetmap.org/search?format=json&limit=5&q=` +
      encodeURIComponent(consulta);

    this.http.get<NominatimResult[]>(url).subscribe({
      next: (resultados) => {
        this.resultadosBusqueda.set(resultados);
        this.buscandoLugar.set(false);
      },
      error: () => {
        this.resultadosBusqueda.set([]);
        this.buscandoLugar.set(false);
        this.errorBusqueda.set(
          'No se pudieron buscar lugares. Inténtalo de nuevo.'
        );
      },
    });
  }

  protected seleccionarLugar(resultado: NominatimResult): void {
    const direccion =
      resultado.display_name.length > 250
        ? `${resultado.display_name.slice(0, 249)}…`
        : resultado.display_name;

    this.formActividad.patchValue({
      direccion,
      latitud: Number(resultado.lat),
      longitud: Number(resultado.lon),
    });
    this.resultadosBusqueda.set([]);
  }

  protected abrirDetalleActividad(actividad: Actividad): void {
    this.actividadSeleccionada.set(actividad);
    this.modalDetalleAbierto.set(true);
  }

  protected cerrarDetalleActividad(): void {
    this.modalDetalleAbierto.set(false);
  }

  protected abrirDocumento(url: string): void {
    window.open(url, '_blank', 'noopener,noreferrer');
  }

  protected abrirModalCompartir(): void {
    this.modalCompartirAbierto.set(true);
  }

  protected cerrarModalCompartir(): void {
    this.modalCompartirAbierto.set(false);
  }

  protected copiarUuid(): void {
    const uuid = this.uuidInvitacion();
    if (!uuid) {
      return;
    }
    navigator.clipboard.writeText(uuid).then(() => {
      this.copiado.set(true);
      setTimeout(() => this.copiado.set(false), 2000);
    });
  }

  protected abrirModalEliminar(): void {
    this.errorEliminar.set(null);
    this.modalEliminarAbierto.set(true);
  }

  protected cerrarModalEliminar(): void {
    this.modalEliminarAbierto.set(false);
  }

  protected eliminarViaje(): void {
    if (!this.viajeId || this.eliminando()) {
      return;
    }
    this.eliminando.set(true);
    this.errorEliminar.set(null);

    this.viajeService.eliminarViaje(this.viajeId).subscribe({
      next: () => {
        this.viajeService.viajes.update((viajes) =>
          viajes.filter((viaje) => viaje.id !== this.viajeId)
        );
        void this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.eliminando.set(false);
        this.errorEliminar.set(
          'No se pudo eliminar el viaje. Inténtalo de nuevo.'
        );
      },
    });
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

    const { dia, titulo, hora, ubicacion, direccion, latitud, longitud } =
      this.formActividad.getRawValue();
    const fechaHoraInicio = `${dia ?? ''}T${hora ?? ''}`;

    const request: Partial<ActividadRequest> = {
      titulo: titulo ?? '',
      tipoActividad: 'OTRO',
      fechaHoraInicio,
      fechaHoraFin: fechaHoraInicio,
      descripcion: ubicacion || undefined,
      direccion: direccion || undefined,
      latitud: latitud ?? undefined,
      longitud: longitud ?? undefined,
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
    const actividad = this.actividadSeleccionada();
    if (!file || !actividad || !this.viajeId) {
      input.value = '';
      return;
    }

    this.subiendoArchivo.set(true);
    this.actividadService
      .subirDocumento(this.viajeId, actividad.id, file)
      .subscribe({
        next: (actualizada) => {
          this.subiendoArchivo.set(false);
          this.actividadSeleccionada.set(actualizada);
          this.actividadService.actividades.update((lista) =>
            lista.map((a) => (a.id === actualizada.id ? actualizada : a))
          );
        },
        error: () => {
          this.subiendoArchivo.set(false);
        },
      });
    input.value = '';
  }

  protected eliminarDocumentoDeActividad(documentoId: UUID): void {
    const actividad = this.actividadSeleccionada();
    if (!this.viajeId || !actividad || this.borrandoDocumento()) {
      return;
    }

    this.borrandoDocumento.set(documentoId);
    this.actividadService
      .eliminarDocumento(this.viajeId, actividad.id, documentoId)
      .subscribe({
        next: () => {
          this.borrandoDocumento.set(null);
          const sinDocumento = (lista: Documento[]) =>
            lista.filter((d) => d.id !== documentoId);
          this.actividadSeleccionada.update((act) =>
            act ? { ...act, documentos: sinDocumento(act.documentos) } : act
          );
          this.actividadService.actividades.update((lista) =>
            lista.map((a) =>
              a.id === actividad.id
                ? { ...a, documentos: sinDocumento(a.documentos) }
                : a
            )
          );
        },
        error: () => {
          this.borrandoDocumento.set(null);
        },
      });
  }

  protected setListaChecklist(lista: ListaChecklist): void {
    this.listaChecklist.set(lista);
  }

  protected anadirTarea(): void {
    if (!this.viajeId) {
      return;
    }
    const contenido = this.nuevaTarea().trim();
    if (!contenido) {
      return;
    }

    this.tareaChecklistService
      .crearTarea(this.viajeId, {
        contenido,
        esPrivada: this.listaChecklist() === 'privada',
      })
      .subscribe({
        next: () => {
          this.nuevaTarea.set('');
          this.tareaChecklistService.cargarTareas(this.viajeId!);
        },
      });
  }

  protected toggleTarea(tarea: TareaChecklistResponse): void {
    if (!this.viajeId) {
      return;
    }
    this.tareaChecklistService
      .cambiarEstado(this.viajeId, tarea.id, !tarea.completada)
      .subscribe({
        next: () => this.tareaChecklistService.cargarTareas(this.viajeId!),
      });
  }

  protected eliminarTarea(tareaId: number): void {
    if (!this.viajeId) {
      return;
    }
    this.tareaChecklistService.eliminarTarea(this.viajeId, tareaId).subscribe({
      next: () => this.tareaChecklistService.cargarTareas(this.viajeId!),
    });
  }

  protected idAsignadoDe(tarea: TareaChecklistResponse): string {
    if (!tarea.nombreAsignado) {
      return '';
    }
    return (
      this.participantes().find((p) => p.nombre === tarea.nombreAsignado)
        ?.id ?? ''
    );
  }

  protected asignarTarea(tarea: TareaChecklistResponse, evento: Event): void {
    if (!this.viajeId) {
      return;
    }
    const valor = (evento.target as HTMLSelectElement).value;
    const usuarioId = valor ? (valor as UUID) : null;
    this.tareaChecklistService
      .asignarUsuario(this.viajeId, tarea.id, usuarioId)
      .subscribe({
        next: () => this.tareaChecklistService.cargarTareas(this.viajeId!),
      });
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

  private destruirMapa(): void {
    if (this.mapa) {
      this.mapa.remove();
      this.mapa = null;
    }
  }

  private montarMapa(): void {
    const contenedor = document.getElementById('map-itinerario');
    if (!contenedor || this.mapa) {
      return;
    }

    // Fix del icono por defecto de Leaflet: los assets no se resuelven
    // con el bundler de Angular, se apuntan a copias locales en /public.
    L.Icon.Default.mergeOptions({
      iconRetinaUrl: 'markers/marker-icon-2x.png',
      iconUrl: 'markers/marker-icon.png',
      shadowUrl: 'markers/marker-shadow.png',
    });

    const puntos = this.actividadesConCoordenadas();

    const centro: [number, number] =
      puntos.length > 0
        ? [puntos[0].latitud ?? 0, puntos[0].longitud ?? 0]
        : [40.4168, -3.7038];
    const zoom = puntos.length > 0 ? 12 : 6;

    this.mapa = L.map(contenedor, { scrollWheelZoom: false }).setView(
      centro,
      zoom
    );

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    }).addTo(this.mapa);

    const marcadores = puntos.map((actividad) =>
      L.marker([actividad.latitud ?? 0, actividad.longitud ?? 0])
        .addTo(this.mapa!)
        .bindPopup(
          `<strong>${this.escapeHtml(actividad.titulo)}</strong><br/>` +
            `${this.horaCorta(actividad.fechaHoraInicio)}`
        )
    );

    if (marcadores.length > 0) {
      this.mapa.fitBounds(
        L.featureGroup(marcadores).getBounds().pad(0.25)
      );
    }

    // El contenedor se monta dentro de un @switch: se recalcula el tamaño
    // para que los tiles no queden desalineados.
    setTimeout(() => this.mapa?.invalidateSize(), 0);
  }

  private horaCorta(iso: string): string {
    return new Date(iso).toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  private escapeHtml(texto: string): string {
    return texto
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;');
  }
}