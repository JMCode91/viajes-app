import {
  Component,
  computed,
  effect,
  inject,
  OnDestroy,
  signal,
  ViewChild,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { GoogleMap, MapAdvancedMarker, MapInfoWindow } from '@angular/google-maps';
import {
  debounceTime,
  distinctUntilChanged,
  finalize,
  from,
  map,
  Observable,
  of,
  Subject,
  Subscription,
  switchMap,
} from 'rxjs';

import { CustomTimePicker } from '../../../shared/components/custom-time-picker/custom-time-picker';
import { GoogleMapsService } from '../../../core/services/google-maps.service';
import { ActividadService } from '../../../core/services/actividad.service';
import { TareaChecklistService } from '../../../core/services/tarea-checklist.service';
import { ViajeService } from '../../../core/services/viaje.service';
import { AuthService } from '../../../core/services/auth.service';
import { GaleriaService } from '../../../core/services/galeria.service';
import {
  Actividad,
  ActividadRequest,
  Album,
  Documento,
  Foto,
  TareaChecklistResponse,
  UUID,
} from '../../../shared/types';

export type Tab = 'itinerario' | 'mapa' | 'checklist' | 'galeria';

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

export type MapFilterType = 'ALL' | 'DAY' | 'ACTIVITY';

export interface ResultadoBusqueda {
  placeId: string;
  descripcion: string;
  /** Nueva API (Places API New): usada para resolver el detalle. */
  sugerencia?: google.maps.places.AutocompleteSuggestion;
  /** Fallback legado: predicción cruda de AutocompleteService. */
  prediccionLegacy?: google.maps.places.AutocompletePrediction;
}

@Component({
  selector: 'app-detalle-viaje',
  imports: [
    DatePipe,
    ReactiveFormsModule,
    FormsModule,
    CustomTimePicker,
    GoogleMap,
    MapAdvancedMarker,
    MapInfoWindow,
  ],
  templateUrl: './detalle-viaje.html',
  styleUrl: './detalle-viaje.css',
})
export class DetalleViaje implements OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly viajeService = inject(ViajeService);
  private readonly actividadService = inject(ActividadService);
  private readonly tareaChecklistService = inject(TareaChecklistService);
  private readonly googleMapsService = inject(GoogleMapsService);
  private readonly authService = inject(AuthService);
  private readonly galeriaService = inject(GaleriaService);

  protected readonly viaje = this.viajeService.viajeDetalle;
  protected readonly actividades = this.actividadService.actividades;
  protected readonly tareas = this.tareaChecklistService.tareas;
  protected readonly participantes = this.viajeService.participantes;
  protected readonly tabActiva = signal<Tab>('itinerario');
  protected readonly diaSeleccionado = signal<string | null>(null);
  protected readonly modalAbierto = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorForm = signal<string | null>(null);
  protected readonly editandoId = signal<UUID | null>(null);
  protected readonly modalBorrarActividadAbierto = signal(false);
  protected readonly actividadAEliminar = signal<Actividad | null>(null);
  protected readonly borrandoActividad = signal(false);
  protected readonly errorBorrarActividad = signal<string | null>(null);

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
  protected readonly resultadosBusqueda = signal<ResultadoBusqueda[]>([]);
  protected readonly errorBusqueda = signal<string | null>(null);

  protected readonly mapId = 'DEMO_MAP_ID';

  protected readonly userLocation = signal<google.maps.LatLngLiteral | null>(
    null
  );
  protected readonly localizando = signal(false);
  protected readonly errorGeo = signal<string | null>(null);
  protected readonly userMarkerContent = signal<HTMLElement | null>(null);

  protected readonly actividadesConCoordenadas = computed(() =>
    this.actividades().filter(
      (a) => a.latitud != null && a.longitud != null
    )
  );

  protected readonly mapFilterType = signal<MapFilterType>('ALL');
  protected readonly mapFilterValue = signal<string | null>(null);

  protected readonly diasMapa = computed<Dia[]>(() =>
    this.dias().filter((dia) =>
      this.actividades().some(
        (a) =>
          a.latitud != null &&
          a.longitud != null &&
          a.fechaHoraInicio.startsWith(dia.valor)
      )
    )
  );

  protected readonly actividadesMapa = computed<Actividad[]>(() => {
    const conCoordenadas = this.actividadesConCoordenadas();
    const tipo = this.mapFilterType();
    const valor = this.mapFilterValue();
    if (tipo === 'DAY' && valor) {
      return conCoordenadas.filter((a) =>
        a.fechaHoraInicio.startsWith(valor)
      );
    }
    if (tipo === 'ACTIVITY' && valor) {
      return conCoordenadas.filter((a) => a.id === valor);
    }
    return conCoordenadas;
  });

  protected readonly centroMapa = computed<google.maps.LatLngLiteral>(() => {
    const puntos = this.actividadesMapa();
    if (puntos.length > 0) {
      return {
        lat: puntos[0].latitud ?? 40.4168,
        lng: puntos[0].longitud ?? -3.7038,
      };
    }
    return { lat: 40.4168, lng: -3.7038 };
  });

  protected readonly zoomMapa = 6;

  protected readonly opcionesMapa: google.maps.MapOptions = {
    scrollwheel: false,
    streetViewControl: false,
    mapTypeControl: false,
    fullscreenControl: false,
  };

  protected readonly actividadInfo = signal<Actividad | null>(null);

  @ViewChild('infoWindow') protected infoWindow?: MapInfoWindow;

  protected readonly albumes = this.galeriaService.albumes;
  protected readonly fotos = this.galeriaService.fotos;
  protected readonly albumSeleccionado = signal<UUID | null>(null);
  protected readonly modalSubirFotoAbierto = signal(false);
  protected readonly modalCrearAlbumAbierto = signal(false);
  protected readonly nombreNuevoAlbum = signal('');
  protected readonly archivoFoto = signal<File | null>(null);
  protected readonly albumFoto = signal<UUID | null>(null);
  protected readonly subiendoFoto = signal(false);
  protected readonly errorFoto = signal<string | null>(null);
  protected readonly creandoAlbum = signal(false);
  protected readonly errorAlbum = signal<string | null>(null);
  protected readonly borrandoFotoId = signal<UUID | null>(null);
  protected readonly borrandoAlbumId = signal<UUID | null>(null);

  protected readonly fotosVisibles = computed<Foto[]>(() => {
    const idAlbum = this.albumSeleccionado();
    if (!idAlbum) {
      return this.fotos();
    }
    return this.fotos().filter((f) => f.albumId === idAlbum);
  });

  protected readonly usuarioSesion = this.authService.usuario;

  protected readonly esOrganizador = computed(
    () => this.viaje()?.rolEnViaje === 'PROPIETARIO'
  );

  protected readonly usuarioIdPropio = computed<UUID | null>(() => {
    const email = this.usuarioSesion()?.email;
    if (!email) {
      return null;
    }
    return (
      this.participantes().find((p) => p.email === email)?.id ?? null
    );
  });

  private mapaGoogle: google.maps.Map | null = null;
  private readonly busquedaLugar$ = new Subject<string>();
  private readonly suscripcionBusqueda: Subscription;

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
    notas: [''],
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
    {
      key: 'galeria',
      label: 'Galería',
      icon: 'M6.827 6.175A2.31 2.31 0 0 1 5.186 7.23c-.38.054-.757.112-1.134.175C2.999 7.58 2.25 8.507 2.25 9.574V18a2.25 2.25 0 0 0 2.25 2.25h15A2.25 2.25 0 0 0 21.75 18V9.574c0-1.067-.75-1.994-1.802-2.169a47.865 47.865 0 0 0-1.134-.175 2.31 2.31 0 0 1-1.64-1.055l-.822-1.316a2.192 2.192 0 0 0-1.736-1.039 48.774 48.774 0 0 0-5.232 0 2.192 2.192 0 0 0-1.736 1.039l-.821 1.316Z',
    },
  ];

  constructor() {
    if (this.viajeId) {
      this.viajeService.cargarViaje(this.viajeId);
      this.actividadService.cargarActividades(this.viajeId);
      this.tareaChecklistService.cargarTareas(this.viajeId);
      this.viajeService.cargarParticipantes(this.viajeId);
      this.galeriaService.cargarAlbumes(this.viajeId);
      this.galeriaService.cargarFotos(this.viajeId);
    }

    void this.googleMapsService.cargar();

    effect(() => {
      const primerDia = this.dias()[0]?.valor;
      if (primerDia && !this.diaSeleccionado()) {
        this.diaSeleccionado.set(primerDia);
      }
    });

    effect(() => {
      this.actividadesMapa();
      this.autoCentrarMapa();
    });

    this.suscripcionBusqueda = this.busquedaLugar$
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap((consulta) => {
          this.buscandoLugar.set(true);
          this.errorBusqueda.set(null);
          return this.consultarAutocomplete(consulta).pipe(
            finalize(() => this.buscandoLugar.set(false))
          );
        })
      )
      .subscribe({
        next: (resultados) => this.resultadosBusqueda.set(resultados),
        error: () => {
          this.resultadosBusqueda.set([]);
          this.buscandoLugar.set(false);
          this.errorBusqueda.set(
            'No se pudieron buscar lugares. Inténtalo de nuevo.'
          );
        },
      });
  }

  ngOnDestroy(): void {
    this.suscripcionBusqueda.unsubscribe();
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
    this.errorBusqueda.set(null);
    this.resultadosBusqueda.set([]);
    this.formActividad.reset();
    this.formActividad.patchValue({ dia: this.diaActivo() ?? '' });
    this.editandoId.set(null);
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(actividad: Actividad): void {
    this.errorForm.set(null);
    this.errorBusqueda.set(null);
    this.resultadosBusqueda.set([]);
    this.formActividad.reset();
    this.formActividad.patchValue({
      dia: actividad.fechaHoraInicio.slice(0, 10),
      titulo: actividad.titulo,
      hora: actividad.fechaHoraInicio.slice(11, 16),
      ubicacion: actividad.descripcion ?? '',
      direccion: actividad.direccion ?? '',
      notas: actividad.notas ?? '',
      latitud: actividad.latitud ?? null,
      longitud: actividad.longitud ?? null,
    });
    this.editandoId.set(actividad.id);
    this.modalAbierto.set(true);
  }

  protected cerrarModal(): void {
    this.errorBusqueda.set(null);
    this.resultadosBusqueda.set([]);
    this.modalAbierto.set(false);
  }

  protected onBuscarInput(): void {
    const consulta = this.formActividad.get('direccion')?.value?.trim() ?? '';
    if (!consulta) {
      this.resultadosBusqueda.set([]);
      this.buscandoLugar.set(false);
      this.errorBusqueda.set(null);
      return;
    }
    this.busquedaLugar$.next(consulta);
  }

  protected seleccionarLugar(resultado: ResultadoBusqueda): void {
    if (resultado.prediccionLegacy) {
      this.aplicarLugarLegacy(resultado.prediccionLegacy);
      return;
    }
    const prediccion = resultado.sugerencia?.placePrediction;
    const place = prediccion?.toPlace();
    if (!place) {
      this.errorBusqueda.set('No se pudo obtener el detalle del lugar.');
      return;
    }

    void place
      .fetchFields({ fields: ['formattedAddress', 'location'] })
      .then(({ place: detalle }) => {
        const ubicacion = detalle.location;
        if (!ubicacion) {
          this.errorBusqueda.set('No se pudo obtener el detalle del lugar.');
          return;
        }
        this.formActividad.patchValue({
          direccion: this.truncarDireccion(
            detalle.formattedAddress ?? resultado.descripcion
          ),
          latitud: ubicacion.lat(),
          longitud: ubicacion.lng(),
        });
        this.resultadosBusqueda.set([]);
      })
      .catch(() => {
        this.errorBusqueda.set('No se pudo obtener el detalle del lugar.');
      });
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

  protected esImagen(documento: Documento): boolean {
    return /\.(jpg|jpeg|png|webp)$/i.test(documento.nombreOriginal);
  }

  protected truncarNombre(nombre: string, max = 28): string {
    const limpio = nombre.trim();
    if (limpio.length <= max) {
      return limpio;
    }
    const cortado = limpio.slice(0, Math.max(0, max - 1)).trimEnd();
    return `${cortado}…`;
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

    const { dia, titulo, hora, ubicacion, direccion, latitud, longitud, notas } =
      this.formActividad.getRawValue();
    const fechaHoraInicio = `${dia ?? ''}T${hora ?? ''}`;

    const request: Partial<ActividadRequest> = {
      titulo: titulo ?? '',
      tipoActividad: 'OTRO',
      fechaHoraInicio,
      fechaHoraFin: fechaHoraInicio,
      descripcion: ubicacion || undefined,
      direccion: direccion || undefined,
      notas: notas || undefined,
      latitud: latitud ?? undefined,
      longitud: longitud ?? undefined,
    };

    this.guardando.set(true);
    this.errorForm.set(null);

    const edicionId = this.editandoId();

    // Actualiza la actividad si existe su id; crea en caso contrario.
    const peticion = edicionId
      ? this.actividadService.updateActividad(this.viajeId, edicionId, request)
      : this.actividadService.crearActividad(this.viajeId, request);

    peticion.subscribe({
      next: () => {
        this.actividadService.cargarActividades(this.viajeId!);
        this.guardando.set(false);
        this.editandoId.set(null);
        this.cerrarModal();
      },
      error: () => {
        this.guardando.set(false);
        this.errorForm.set('No se pudo guardar el plan. Inténtalo de nuevo.');
      },
    });
  }

  protected abrirModalBorrarActividad(actividad: Actividad): void {
    this.actividadAEliminar.set(actividad);
    this.errorBorrarActividad.set(null);
    this.modalBorrarActividadAbierto.set(true);
  }

  protected cerrarModalBorrarActividad(): void {
    this.modalBorrarActividadAbierto.set(false);
    this.actividadAEliminar.set(null);
  }

  protected borrarActividad(): void {
    const actividad = this.actividadAEliminar();
    if (!this.viajeId || !actividad || this.borrandoActividad()) {
      return;
    }

    this.borrandoActividad.set(true);
    this.errorBorrarActividad.set(null);

    this.actividadService.deleteActividad(this.viajeId, actividad.id).subscribe({
      next: () => {
        this.borrandoActividad.set(false);
        this.actividadService.actividades.update((lista) =>
          lista.filter((a) => a.id !== actividad.id)
        );
        this.cerrarModalBorrarActividad();
      },
      error: () => {
        this.borrandoActividad.set(false);
        this.errorBorrarActividad.set(
          'No se pudo eliminar el plan. Inténtalo de nuevo.'
        );
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

  protected seleccionarAlbum(albumId: UUID | null): void {
    this.albumSeleccionado.set(albumId);
  }

  protected abrirCrearAlbum(): void {
    this.errorAlbum.set(null);
    this.nombreNuevoAlbum.set('');
    this.modalCrearAlbumAbierto.set(true);
  }

  protected cerrarCrearAlbum(): void {
    this.modalCrearAlbumAbierto.set(false);
  }

  protected crearAlbum(): void {
    if (!this.viajeId || this.creandoAlbum()) {
      return;
    }
    const nombre = this.nombreNuevoAlbum().trim();
    if (!nombre) {
      return;
    }
    this.creandoAlbum.set(true);
    this.errorAlbum.set(null);
    this.galeriaService.crearAlbum(this.viajeId, { nombre }).subscribe({
      next: () => {
        this.creandoAlbum.set(false);
        this.galeriaService.cargarAlbumes(this.viajeId!);
        this.cerrarCrearAlbum();
      },
      error: () => {
        this.creandoAlbum.set(false);
        this.errorAlbum.set(
          'No se pudo crear el álbum. Inténtalo de nuevo.'
        );
      },
    });
  }

  protected eliminarAlbum(album: Album): void {
    if (!this.viajeId || this.borrandoAlbumId()) {
      return;
    }
    if (
      !window.confirm(
        `¿Eliminar el álbum “${album.nombre}”? Las fotos se conservarán en la galería.`
      )
    ) {
      return;
    }
    this.borrandoAlbumId.set(album.id);
    this.galeriaService.eliminarAlbum(this.viajeId, album.id).subscribe({
      next: () => {
        this.borrandoAlbumId.set(null);
        if (this.albumSeleccionado() === album.id) {
          this.albumSeleccionado.set(null);
        }
        this.galeriaService.cargarAlbumes(this.viajeId!);
        this.galeriaService.cargarFotos(this.viajeId!);
      },
      error: () => {
        this.borrandoAlbumId.set(null);
      },
    });
  }

  protected portadaDe(albumId: UUID): string | null {
    return this.fotos().find((f) => f.albumId === albumId)?.url ?? null;
  }

  protected cantidadFotos(albumId: UUID | null): number {
    return this.fotos().filter((f) => f.albumId === albumId).length;
  }

  protected abrirSubirFoto(): void {
    this.errorFoto.set(null);
    this.archivoFoto.set(null);
    this.albumFoto.set(this.albumSeleccionado());
    this.modalSubirFotoAbierto.set(true);
  }

  protected cerrarSubirFoto(): void {
    this.modalSubirFotoAbierto.set(false);
  }

  protected onFotoSeleccionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.archivoFoto.set(input.files?.[0] ?? null);
  }

  protected subirFoto(): void {
    const archivo = this.archivoFoto();
    if (!this.viajeId || !archivo || this.subiendoFoto()) {
      return;
    }
    this.subiendoFoto.set(true);
    this.errorFoto.set(null);
    this.galeriaService
      .subirFoto(this.viajeId, archivo, this.albumFoto())
      .subscribe({
        next: (foto) => {
          this.subiendoFoto.set(false);
          this.galeriaService.fotos.update((lista) => [foto, ...lista]);
          this.cerrarSubirFoto();
        },
        error: () => {
          this.subiendoFoto.set(false);
          this.errorFoto.set(
            'No se pudo subir la foto. Comprueba el tamaño y vuelve a intentarlo.'
          );
        },
      });
  }

  protected eliminarFoto(foto: Foto): void {
    if (!this.viajeId || this.borrandoFotoId()) {
      return;
    }
    if (!window.confirm('¿Eliminar esta foto? Esta acción no se deshace.')) {
      return;
    }
    this.borrandoFotoId.set(foto.id);
    this.galeriaService.eliminarFoto(this.viajeId, foto.id).subscribe({
      next: () => {
        this.borrandoFotoId.set(null);
        this.galeriaService.fotos.update((lista) =>
          lista.filter((f) => f.id !== foto.id)
        );
      },
      error: () => {
        this.borrandoFotoId.set(null);
      },
    });
  }

  protected puedeBorrarFoto(foto: Foto): boolean {
    return (
      this.esOrganizador() || this.usuarioIdPropio() === foto.usuarioId
    );
  }

  protected inicial(nombre: string | null | undefined): string {
    return (nombre?.trim().charAt(0) ?? '?').toUpperCase();
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

  protected onMapaInicializado(mapa: google.maps.Map): void {
    this.mapaGoogle = mapa;
    const ubicacion = this.userLocation();
    if (ubicacion) {
      this.centrarEnUbicacion(mapa, ubicacion);
    } else {
      setTimeout(() => this.autoCentrarMapa(), 0);
    }
  }

  protected setFiltroMapa(tipo: MapFilterType, valor?: string | null): void {
    this.mapFilterType.set(tipo);
    this.mapFilterValue.set(valor ?? null);
  }

  protected irAlMapaDe(actividad: Actividad): void {
    this.setFiltroMapa('ACTIVITY', actividad.id);
    this.setTab('mapa');
  }

  protected abrirInfoWindow(
    marker: MapAdvancedMarker,
    actividad: Actividad
  ): void {
    this.actividadInfo.set(actividad);
    this.infoWindow?.open(marker);
  }

  protected localizar(): void {
    if (this.localizando()) {
      return;
    }
    if (!navigator.geolocation) {
      this.errorGeo.set('Tu navegador no soporta geolocalización.');
      return;
    }
    this.localizando.set(true);
    this.errorGeo.set(null);
    if (!this.userMarkerContent()) {
      this.userMarkerContent.set(this.crearPuntoAzul());
    }

    navigator.geolocation.getCurrentPosition(
      (posicion) => {
        const ubicacion: google.maps.LatLngLiteral = {
          lat: posicion.coords.latitude,
          lng: posicion.coords.longitude,
        };
        this.userLocation.set(ubicacion);
        this.localizando.set(false);
        if (this.mapaGoogle) {
          this.centrarEnUbicacion(this.mapaGoogle, ubicacion);
        }
      },
      () => {
        this.localizando.set(false);
        this.errorGeo.set('No se pudo obtener tu ubicación.');
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 30000 }
    );
  }

  private centrarEnUbicacion(
    mapa: google.maps.Map,
    ubicacion: google.maps.LatLngLiteral
  ): void {
    mapa.panTo(ubicacion);
    mapa.setZoom(16);
  }

  private crearPuntoAzul(): HTMLElement {
    const contenedor = document.createElement('div');
    contenedor.innerHTML = `
      <svg width="44" height="44" viewBox="0 0 44 44" xmlns="http://www.w3.org/2000/svg" style="filter: drop-shadow(0 1px 2px rgba(0,0,0,0.25));">
        <circle cx="22" cy="22" r="18" fill="#3b82f6" fill-opacity="0.16" stroke="#3b82f6" stroke-width="2" stroke-opacity="0.35"/>
        <circle cx="22" cy="22" r="11" fill="#ffffff" stroke="#3b82f6" stroke-width="3.5"/>
        <circle cx="22" cy="22" r="5" fill="#2563eb"/>
      </svg>`;
    return contenedor.firstElementChild as HTMLElement;
  }

  private consultarAutocomplete(
    consulta: string
  ): Observable<ResultadoBusqueda[]> {
    const termino = consulta.trim();
    if (!termino) {
      return of([]);
    }

    // Places API (New): AutocompleteSuggestion.fetchAutocompleteSuggestions
    // devuelve una Promise con las sugerencias (PlacePrediction). Si la nueva
    // clase no está disponible (API no habilitada / librería antigua), se
    // hace fallback al legado AutocompleteService.getPlacePredictions.
    const nuevaApi = google.maps.places.AutocompleteSuggestion;
    if (nuevaApi?.fetchAutocompleteSuggestions) {
      return from(
        nuevaApi.fetchAutocompleteSuggestions({ input: termino })
      ).pipe(
        map((respuesta) =>
          respuesta.suggestions.map((sugerencia) => {
            const prediccion = sugerencia.placePrediction;
            return {
              placeId: prediccion?.placeId ?? '',
              descripcion: prediccion?.text?.text ?? '',
              sugerencia,
            };
          })
        )
      );
    }

    return new Observable((observer) => {
      const service = new google.maps.places.AutocompleteService();
      service.getPlacePredictions(
        { input: termino },
        (resultados, estado) => {
          if (estado === google.maps.places.PlacesServiceStatus.OK) {
            observer.next(
              (resultados ?? []).map((prediccion) => ({
                placeId: prediccion.place_id,
                descripcion: prediccion.description,
                prediccionLegacy: prediccion,
              }))
            );
          } else {
            observer.next([]);
          }
          observer.complete();
        }
      );
    });
  }

  private aplicarLugarLegacy(
    prediccion: google.maps.places.AutocompletePrediction
  ): void {
    const places = new google.maps.places.PlacesService(
      document.createElement('div')
    );

    places.getDetails({ placeId: prediccion.place_id }, (lugar, estado) => {
      if (
        estado === google.maps.places.PlacesServiceStatus.OK &&
        lugar?.geometry?.location
      ) {
        this.formActividad.patchValue({
          direccion: this.truncarDireccion(
            lugar.formatted_address ?? prediccion.description ?? ''
          ),
          latitud: lugar.geometry.location.lat(),
          longitud: lugar.geometry.location.lng(),
        });
        this.resultadosBusqueda.set([]);
      } else {
        this.errorBusqueda.set('No se pudo obtener el detalle del lugar.');
      }
    });
  }

  private truncarDireccion(direccion: string): string {
    return direccion.length > 250
      ? `${direccion.slice(0, 249)}…`
      : direccion;
  }

  private autoCentrarMapa(): void {
    const mapa = this.mapaGoogle;
    if (!mapa) {
      return;
    }
    const puntos = this.actividadesMapa();
    if (puntos.length === 0) {
      return;
    }
    if (puntos.length === 1) {
      mapa.panTo({ lat: puntos[0].latitud!, lng: puntos[0].longitud! });
      mapa.setZoom(15);
      return;
    }
    const bounds = new google.maps.LatLngBounds();
    for (const punto of puntos) {
      bounds.extend({ lat: punto.latitud ?? 0, lng: punto.longitud ?? 0 });
    }
    mapa.fitBounds(bounds, 56);
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
