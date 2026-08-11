import {
  Component,
  computed,
  effect,
  inject,
  OnDestroy,
  signal,
  untracked,
  ViewChild,
} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe, DecimalPipe } from '@angular/common';
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
import { ClimaService } from '../../../core/services/clima.service';
import { Finanzas } from '../../finanzas/finanzas';
import {
  Actividad,
  ActividadRequest,
  Album,
  Clima,
  Documento,
  Foto,
  Participante,
  TareaChecklistResponse,
  UUID,
  Viaje,
  ViajeUpdateRequest,
} from '../../../shared/types';

export type Tab =
  | 'itinerario'
  | 'mapa'
  | 'checklist'
  | 'galeria'
  | 'gastos'
  | 'ajustes';

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
    DecimalPipe,
    ReactiveFormsModule,
    FormsModule,
    CustomTimePicker,
    GoogleMap,
    MapAdvancedMarker,
    MapInfoWindow,
    Finanzas,
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
  private readonly climaService = inject(ClimaService);

  private static readonly LAT_TOKIO = 35.6895;
  private static readonly LON_TOKIO = 139.6917;

  protected readonly climasPorDia = signal<Map<string, Clima>>(new Map());

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
  protected readonly metadatosGoogle = signal<{
    googlePlaceId: string | null;
    rating: number | null;
    horarios: string | null;
    urlGoogleMaps: string | null;
  }>({ googlePlaceId: null, rating: null, horarios: null, urlGoogleMaps: null });

  protected readonly resenasGoogle = signal<google.maps.places.PlaceReview[]>([]);
  protected readonly fotosLugar = signal<string[]>([]);
  protected readonly mostrarResenas = signal(false);
  protected readonly cargandoResenas = signal(false);
  protected readonly errorResenas = signal<string | null>(null);
  protected readonly actividadResenas = signal<Actividad | null>(null);

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
    const conCoordenadas = this.actividadesConCoordenadas()
      .slice()
      .sort((a, b) => a.fechaHoraInicio.localeCompare(b.fechaHoraInicio));
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

  protected readonly esOrganizador = computed(() => {
    if (this.viaje()?.rolEnViaje === 'PROPIETARIO') {
      return true;
    }
    const miId = this.usuarioIdPropio();
    return (
      miId != null &&
      this.participantes().some(
        (p) => p.usuarioId === miId && p.rolEnViaje === 'PROPIETARIO'
      )
    );
  });

  protected readonly usuarioIdPropio = computed<UUID | null>(() => {
    const email = this.usuarioSesion()?.email;
    if (!email) {
      return null;
    }
    return (
      this.participantes().find((p) => p.email === email)?.usuarioId ?? null
    );
  });

  protected readonly miParticipante = computed<Participante | null>(() => {
    const miId = this.usuarioIdPropio();
    if (!miId) {
      return null;
    }
    return this.participantes().find((p) => p.usuarioId === miId) ?? null;
  });

  private mapaGoogle: google.maps.Map | null = null;
  private readonly busquedaLugar$ = new Subject<string>();
  private readonly suscripcionBusqueda: Subscription;

  protected readonly uuidInvitacion = computed<UUID | null>(
    () => this.viaje()?.id ?? null
  );

  protected readonly formAjustes = this.fb.group({
    titulo: ['', [Validators.required, Validators.maxLength(200)]],
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required],
  });

  protected readonly guardandoAjustes = signal(false);
  protected readonly errorAjustes = signal<string | null>(null);
  protected readonly subiendoPortada = signal(false);
  protected readonly errorPortada = signal<string | null>(null);
  protected readonly expulsandoUsuario = signal<UUID | null>(null);
  protected readonly errorExpulsar = signal<string | null>(null);
  protected readonly modalInvitarAbierto = signal(false);
  protected readonly copiadoInvitacion = signal(false);
  protected readonly abandonandoAjustes = signal(false);
  protected readonly errorAbandonar = signal<string | null>(null);
  protected readonly seccionAbierta = signal<string | null>(null);

  protected toggleSeccion(seccion: string): void {
    this.seccionAbierta.update((actual) =>
      actual === seccion ? null : seccion
    );
  }

  protected readonly presupuestoIndividual = signal<number>(0);
  protected readonly guardandoPresupuesto = signal(false);
  protected readonly errorPresupuesto = signal<string | null>(null);
  protected readonly presupuestoOk = signal(false);

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
    const dia = this.diaSeleccionado();
    if (!dia) {
      return this.actividades();
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
    direccion: [''],
    notas: [''],
    latitud: [null as number | null],
    longitud: [null as number | null],
    descripcionGoogle: [''],
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
    {
      key: 'gastos',
      label: 'Gastos',
      icon: 'M2.25 18.75a60.07 60.07 0 0 1 15.797 2.101c.727.198 1.453-.342 1.453-1.096V18.75M3.75 4.5v.75A.75.75 0 0 1 3 6h-.75m0 0v-.375c0-.621.504-1.125 1.125-1.125H20.25M2.25 6v9m18-10.5v.75c0 .414.336.75.75.75h.75m-1.5-1.5h.375c.621 0 1.125.504 1.125 1.125v9.75c0 .621-.504 1.125-1.125 1.125h-.75m1.5-1.5H21a.75.75 0 0 0-.75.75v.75m0 0H3.75m0 0h-.375a1.125 1.125 0 0 1-1.125-1.125V15m1.5 1.5v-.75A.75.75 0 0 0 3 15h-.75M15 10.5a3 3 0 1 1-6 0 3 3 0 0 1 6 0Z',
    },
    {
      key: 'ajustes',
      label: 'Ajustes',
      icon: 'M11.42 15.17 17.25 21A2.652 2.652 0 0 0 21 17.25l-5.877-5.877M11.42 15.17l2.496-3.03c.317-.384.74-.626 1.208-.766M11.42 15.17l-4.655 5.653a2.548 2.548 0 1 1-3.586-3.586l6.837-5.63m5.108-.233c.55-.164 1.163-.188 1.743-.14a4.5 4.5 0 0 0 4.486-6.336l-3.276 3.277a3.004 3.004 0 0 1-2.25-2.25l3.276-3.276a4.5 4.5 0 0 0-6.336 4.486c.091 1.076-.071 2.264-.904 2.95l-.102.085m-1.745 1.437L5.909 7.5H4.5L2.25 3.75l1.5-1.5L7.5 4.5v1.409l4.26 4.26m-1.745 1.437 1.745-1.437m6.615 8.206L15.75 15.75M4.867 19.125h.008v.008h-.008v-.008Z',
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
      const dias = this.dias();
      const yaCargados = untracked(() => this.climasPorDia());
      for (const dia of dias) {
        if (yaCargados.has(dia.valor)) {
          continue;
        }
        this.climaService
          .getClima(DetalleViaje.LAT_TOKIO, DetalleViaje.LON_TOKIO, dia.valor)
          .subscribe({
            next: (clima) => {
              this.climasPorDia.update((mapa) => {
                const actualizado = new Map(mapa);
                actualizado.set(dia.valor, clima);
                return actualizado;
              });
            },
            error: () => undefined,
          });
      }
    });

    effect(() => {
      this.actividadesMapa();
      this.autoCentrarMapa();
    });

    effect(() => {
      const viaje = this.viaje();
      if (viaje) {
        this.formAjustes.patchValue({
          titulo: viaje.titulo,
          fechaInicio: viaje.fechaInicio,
          fechaFin: viaje.fechaFin,
        });
      }
    });

    effect(() => {
      const participante = this.miParticipante();
      if (participante && !this.guardandoPresupuesto()) {
        this.presupuestoIndividual.set(participante.presupuestoIndividual ?? 0);
      }
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

  protected seleccionarDia(valor: string | null): void {
    this.diaSeleccionado.set(valor);
  }

  protected abrirModal(): void {
    this.errorForm.set(null);
    this.errorBusqueda.set(null);
    this.resultadosBusqueda.set([]);
    this.metadatosGoogle.set({
      googlePlaceId: null,
      rating: null,
      horarios: null,
      urlGoogleMaps: null,
    });
    this.formActividad.reset();
    this.formActividad.patchValue({ dia: this.diaActivo() ?? '' });
    this.editandoId.set(null);
    this.modalAbierto.set(true);
  }

  protected abrirEdicion(actividad: Actividad): void {
    this.errorForm.set(null);
    this.errorBusqueda.set(null);
    this.resultadosBusqueda.set([]);
    this.metadatosGoogle.set({
      googlePlaceId: actividad.googlePlaceId ?? null,
      rating: actividad.rating ?? null,
      horarios: actividad.horarios ?? null,
      urlGoogleMaps: actividad.urlGoogleMaps ?? null,
    });
    this.formActividad.reset();
    this.formActividad.patchValue({
      dia: actividad.fechaHoraInicio.slice(0, 10),
      titulo: actividad.titulo,
      hora: actividad.fechaHoraInicio.slice(11, 16),
      direccion: actividad.direccion ?? '',
      notas: actividad.notas ?? '',
      latitud: actividad.latitud ?? null,
      longitud: actividad.longitud ?? null,
      descripcionGoogle: actividad.descripcionGoogle ?? '',
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
      .fetchFields({
        fields: [
          'id',
          'displayName',
          'formattedAddress',
          'location',
          'rating',
          'regularOpeningHours',
          'editorialSummary',
        ],
      })
      .then(({ place: detalle }) => {
        const ubicacion = detalle.location;
        if (!ubicacion) {
          this.errorBusqueda.set('No se pudo obtener el detalle del lugar.');
          return;
        }
        const placeId = detalle.id;
        const horarios = detalle.regularOpeningHours
          ? detalle.regularOpeningHours.weekdayDescriptions
              .join(' · ')
              .slice(0, 500)
          : null;
        this.formActividad.patchValue({
          direccion: this.truncarDireccion(
            detalle.formattedAddress ?? resultado.descripcion
          ),
          latitud: ubicacion.lat(),
          longitud: ubicacion.lng(),
          descripcionGoogle: (detalle.editorialSummary ?? '').slice(0, 2000),
        });
        this.autorellenarTitulo(detalle.displayName ?? null);
        this.aplicarMetadatosGoogle(
          placeId ?? null,
          detalle.rating ?? null,
          horarios,
          this.urlGoogleMapsPara(placeId ?? null, ubicacion)
        );
        this.resultadosBusqueda.set([]);
      })
      .catch(() => {
        this.errorBusqueda.set('No se pudo obtener el detalle del lugar.');
      });
  }

  private autorellenarTitulo(nombreLugar: string | null): void {
    if (!nombreLugar) {
      return;
    }
    const control = this.formActividad.controls.titulo;
    if (!control.value?.trim() || control.pristine) {
      const titulo = nombreLugar.trim().slice(0, 200);
      if (titulo) {
        this.formActividad.patchValue({ titulo });
      }
    }
  }

  protected abrirDetalleActividad(actividad: Actividad): void {
    this.actividadSeleccionada.set(actividad);
    this.fotosLugar.set([]);
    this.modalDetalleAbierto.set(true);
    if (actividad.googlePlaceId) {
      void this.googleMapsService
        .obtenerFotosLugar(actividad.googlePlaceId)
        .then((fotos) => this.fotosLugar.set(fotos));
    }
  }

  protected async abrirResenas(actividad: Actividad): Promise<void> {
    if (!actividad.googlePlaceId) {
      return;
    }
    this.actividadResenas.set(actividad);
    this.mostrarResenas.set(true);
    this.cargandoResenas.set(true);
    this.errorResenas.set(null);
    this.resenasGoogle.set([]);
    try {
      const resenas = await this.googleMapsService.obtenerResenasGoogle(
        actividad.googlePlaceId
      );
      this.resenasGoogle.set(resenas.slice(0, 5));
    } catch {
      this.errorResenas.set(
        'No se pudieron cargar las reseñas de Google.'
      );
    } finally {
      this.cargandoResenas.set(false);
    }
  }

  protected cerrarResenas(): void {
    this.mostrarResenas.set(false);
    this.resenasGoogle.set([]);
    this.actividadResenas.set(null);
  }

  protected cerrarDetalleActividad(): void {
    this.modalDetalleAbierto.set(false);
    this.fotosLugar.set([]);
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

  protected obtenerHorarioDia(
    horariosStr: string | null | undefined,
    fechaActividad: Date | string
  ): string {
    if (!horariosStr) {
      return '';
    }
    const fecha =
      typeof fechaActividad === 'string'
        ? new Date(fechaActividad)
        : fechaActividad;
    if (isNaN(fecha.getTime())) {
      return '';
    }
    const etiquetasDia: Array<[string, string]> = [
      ['domingo', 'sunday'],
      ['lunes', 'monday'],
      ['martes', 'tuesday'],
      ['miércoles', 'wednesday'],
      ['jueves', 'thursday'],
      ['viernes', 'friday'],
      ['sábado', 'saturday'],
    ];
    const etiquetas = etiquetasDia[fecha.getDay()] ?? [];
    const parte = (horariosStr.split('·') as string[])
      .map((p) => p.trim())
      .find((p) =>
        etiquetas.some((etiqueta) => p.toLowerCase().startsWith(etiqueta))
      );
    if (!parte) {
      return '';
    }
    const indice = parte.indexOf(':');
    return indice >= 0 ? parte.slice(indice + 1).trim() : '';
  }

  protected obtenerEmojiClima(condicion: string): string {
    const texto = condicion.toLowerCase();
    if (
      texto.includes('despejado') ||
      texto.includes('soleado') ||
      texto.includes('sunny') ||
      texto.includes('clear')
    ) {
      return '☀️';
    }
    if (
      texto.includes('nublado') ||
      texto.includes('nubes') ||
      texto.includes('cloud')
    ) {
      return '☁️';
    }
    if (
      texto.includes('lluvia') ||
      texto.includes('llovizna') ||
      texto.includes('rain')
    ) {
      return '🌧️';
    }
    if (
      texto.includes('tormenta') ||
      texto.includes('truenos') ||
      texto.includes('storm') ||
      texto.includes('thunder')
    ) {
      return '⛈️';
    }
    if (texto.includes('nieve') || texto.includes('snow')) {
      return '❄️';
    }
    return '🌤️';
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

  protected guardarAjustes(): void {
    if (!this.viajeId || this.formAjustes.invalid) {
      this.formAjustes.markAllAsTouched();
      return;
    }
    const { titulo, fechaInicio, fechaFin } = this.formAjustes.getRawValue();
    this.guardandoAjustes.set(true);
    this.errorAjustes.set(null);
    const request: ViajeUpdateRequest = {
      titulo: (titulo ?? '').trim(),
      fechaInicio: fechaInicio ?? '',
      fechaFin: fechaFin ?? '',
    };
    this.viajeService.updateViaje(this.viajeId, request).subscribe({
      next: (viajeActualizado) => {
        this.guardandoAjustes.set(false);
        this.viajeService.viajeDetalle.set(viajeActualizado);
        this.reactualizarEnLista(viajeActualizado);
      },
      error: () => {
        this.guardandoAjustes.set(false);
        this.errorAjustes.set('No se pudieron guardar los cambios.');
      },
    });
  }

  protected onPresupuestoInput(event: Event): void {
    const valor = (event.target as HTMLInputElement).value;
    this.presupuestoIndividual.set(valor === '' ? 0 : Number(valor));
  }

  protected actualizarPresupuesto(): void {
    if (!this.viajeId || this.guardandoPresupuesto()) {
      return;
    }
    const presupuesto = this.presupuestoIndividual();
    if (!Number.isFinite(presupuesto) || presupuesto < 0) {
      this.errorPresupuesto.set('Introduce un presupuesto válido.');
      return;
    }
    this.guardandoPresupuesto.set(true);
    this.errorPresupuesto.set(null);
    this.presupuestoOk.set(false);
    this.viajeService
      .actualizarPresupuestoIndividual(this.viajeId, { presupuesto })
      .subscribe({
        next: (participante) => {
          this.guardandoPresupuesto.set(false);
          this.viajeService.participantes.update((lista) =>
            lista.map((p) =>
              p.usuarioId === participante.usuarioId ? participante : p
            )
          );
          const actualizado = this.viajeService.participantes().find(
            (p) => p.usuarioId === this.usuarioIdPropio()
          );
          if (actualizado) {
            this.presupuestoIndividual.set(actualizado.presupuestoIndividual ?? 0);
          }
          this.presupuestoOk.set(true);
          setTimeout(() => this.presupuestoOk.set(false), 2500);
        },
        error: () => {
          this.guardandoPresupuesto.set(false);
          this.errorPresupuesto.set(
            'No se pudo actualizar tu presupuesto. Inténtalo de nuevo.'
          );
        },
      });
  }

  protected onPortadaSeleccionada(event: Event): void {
    const input = event.target as HTMLInputElement;
    const archivo = input.files?.[0];
    input.value = '';
    if (!archivo || !this.viajeId || this.subiendoPortada()) {
      return;
    }
    this.subiendoPortada.set(true);
    this.errorPortada.set(null);
    this.viajeService.uploadPortada(this.viajeId, archivo).subscribe({
      next: (viajeActualizado) => {
        this.subiendoPortada.set(false);
        this.viajeService.viajeDetalle.set(viajeActualizado);
        this.reactualizarEnLista(viajeActualizado);
      },
      error: () => {
        this.subiendoPortada.set(false);
        this.errorPortada.set('No se pudo subir la portada.');
      },
    });
  }

  protected expulsarParticipante(participante: Participante): void {
    if (!this.viajeId || this.expulsandoUsuario()) {
      return;
    }
    if (!window.confirm(`¿Expulsar a “${participante.nombre}” del viaje?`)) {
      return;
    }
    this.expulsandoUsuario.set(participante.id);
    this.errorExpulsar.set(null);
    this.viajeService
      .kickParticipante(this.viajeId, participante.id)
      .subscribe({
        next: () => {
          this.expulsandoUsuario.set(null);
          this.viajeService.participantes.update((lista) =>
            lista.filter((p) => p.id !== participante.id)
          );
        },
        error: () => {
          this.expulsandoUsuario.set(null);
          this.errorExpulsar.set('No se pudo expulsar al participante.');
        },
      });
  }

  protected rolLabel(rol: string): string {
    return rol === 'PROPIETARIO' ? 'Organizador' : 'Invitado';
  }

  protected abrirModalInvitar(): void {
    this.copiadoInvitacion.set(false);
    this.modalInvitarAbierto.set(true);
  }

  protected cerrarModalInvitar(): void {
    this.modalInvitarAbierto.set(false);
  }

  protected copiarUuidInvitacion(): void {
    const uuid = this.uuidInvitacion();
    if (!uuid) {
      return;
    }
    navigator.clipboard.writeText(uuid).then(() => {
      this.copiadoInvitacion.set(true);
      setTimeout(() => this.copiadoInvitacion.set(false), 2000);
    });
  }

  protected confirmarAbandonarViaje(): void {
    if (!this.viajeId || this.abandonandoAjustes()) {
      return;
    }
    if (
      !window.confirm(
        '¿Abandonar este viaje? Podrás volver a unirte más tarde con el código.'
      )
    ) {
      return;
    }
    this.abandonandoAjustes.set(true);
    this.errorAbandonar.set(null);
    this.viajeService.abandonarViaje(this.viajeId).subscribe({
      next: () => {
        this.abandonandoAjustes.set(false);
        this.viajeService.viajes.update((viajes) =>
          viajes.filter((viaje) => viaje.id !== this.viajeId)
        );
        void this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.abandonandoAjustes.set(false);
        this.errorAbandonar.set('No se pudo abandonar el viaje.');
      },
    });
  }

  private reactualizarEnLista(viaje: Viaje): void {
    this.viajeService.viajes.update((viajes) =>
      viajes.map((v) => (v.id === viaje.id ? viaje : v))
    );
  }

  protected hasError(field: 'dia' | 'titulo' | 'hora'): boolean {
    const control = this.formActividad.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected guardarActividad(): void {
    if (!this.viajeId || this.formActividad.invalid) {
      this.formActividad.markAllAsTouched();
      return;
    }

    const { dia, titulo, hora, direccion, latitud, longitud, notas, descripcionGoogle } =
      this.formActividad.getRawValue();
    const fechaHoraInicio = `${dia ?? ''}T${hora ?? ''}`;
    const meta = this.metadatosGoogle();

    const request: Partial<ActividadRequest> = {
      titulo: titulo ?? '',
      tipoActividad: 'OTRO',
      fechaHoraInicio,
      fechaHoraFin: fechaHoraInicio,
      ...(meta.googlePlaceId ? { googlePlaceId: meta.googlePlaceId } : {}),
      ...(meta.rating != null ? { rating: meta.rating } : {}),
      ...(meta.horarios ? { horarios: meta.horarios } : {}),
      ...(meta.urlGoogleMaps ? { urlGoogleMaps: meta.urlGoogleMaps } : {}),
      direccion: direccion || undefined,
      notas: notas || undefined,
      descripcionGoogle: descripcionGoogle || undefined,
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

  protected estrellasResena(rating: number | undefined): string {
    const redondeo = Math.round(rating ?? 0);
    return (
      '★'.repeat(redondeo) +
      '☆'.repeat(Math.max(0, Math.min(5, 5 - redondeo)))
    );
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

  /**
   * Crea (y cachea por actividad) el contenido HTML de un marcador numerado
   * (AdvancedMarkerElement). Devuelve un círculo azul con el número del
   * índice + 1 centrado en el punto. La caché mantiene estable la referencia
   * del elemento entre ciclos de detección de cambios.
   */
  private readonly contenidosMarcador = new Map<string, HTMLElement>();

  protected crearMarcadorNumerado(actividadId: string, index: number): HTMLElement {
    let contenido = this.contenidosMarcador.get(actividadId);
    if (!contenido) {
      contenido = document.createElement('div');
      contenido.className =
        'flex h-7 w-7 items-center justify-center rounded-full bg-blue-600 text-[11px] font-bold text-white ring-2 ring-white shadow-md';
      this.contenidosMarcador.set(actividadId, contenido);
    }
    contenido.textContent = String(index + 1);
    return contenido;
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

    places.getDetails(
      {
        placeId: prediccion.place_id,
        fields: [
          'formatted_address',
          'geometry',
          'name',
          'rating',
          'opening_hours',
          'editorial_summary',
        ],
      },
      (lugar, estado) => {
        if (
          estado === google.maps.places.PlacesServiceStatus.OK &&
          lugar?.geometry?.location
        ) {
          const latlng = lugar.geometry.location;
          const resumenEditorial = (
            lugar as { editorial_summary?: { overview?: string } }
          ).editorial_summary?.overview;
          this.formActividad.patchValue({
            direccion: this.truncarDireccion(
              lugar.formatted_address ?? prediccion.description ?? ''
            ),
            latitud: latlng.lat(),
            longitud: latlng.lng(),
            descripcionGoogle: (resumenEditorial ?? '').slice(0, 2000),
          });
        this.autorellenarTitulo(lugar.name ?? null);
        this.aplicarMetadatosGoogle(
          prediccion.place_id,
          lugar.rating ?? null,
          (lugar.opening_hours?.weekday_text ?? []).join(' · ').slice(0, 500) ??
            null,
          this.urlGoogleMapsPara(prediccion.place_id, latlng)
        );
        this.resultadosBusqueda.set([]);
      } else {
        this.errorBusqueda.set('No se pudo obtener el detalle del lugar.');
      }
    });
  }

  private aplicarMetadatosGoogle(
    googlePlaceId: string | null,
    rating: number | null,
    horarios: string | null,
    urlGoogleMaps: string | null
  ): void {
    this.metadatosGoogle.set({ googlePlaceId, rating, horarios, urlGoogleMaps });
  }

  private urlGoogleMapsPara(
    placeId: string | null,
    ubicacion: google.maps.LatLng
  ): string {
    const lat = Number(ubicacion.lat().toFixed(5));
    const lng = Number(ubicacion.lng().toFixed(5));
    const params = new URLSearchParams({ api: '1', query: `${lat},${lng}` });
    if (placeId) {
      params.set('query_place_id', placeId);
    }
    return `https://www.google.com/maps/search/?${params.toString()}`;
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
