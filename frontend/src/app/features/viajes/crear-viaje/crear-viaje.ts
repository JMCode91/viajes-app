import { Component, inject, OnDestroy, signal } from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import {
  debounceTime,
  distinctUntilChanged,
  from,
  map,
  Subject,
  switchMap,
  tap,
} from 'rxjs';

import { ViajeService } from '../../../core/services/viaje.service';
import { GoogleMapsService } from '../../../core/services/google-maps.service';
import { CustomDatePicker } from '../../../shared/components/custom-date-picker/custom-date-picker';
import { ParadaViaje } from '../../../shared/types';

interface ResultadoBusqueda {
  placeId: string;
  descripcion: string;
  sugerencia?: google.maps.places.AutocompleteSuggestion;
  prediccionLegacy?: google.maps.places.AutocompletePrediction;
}

interface ValorParada {
  nombreCiudad: string;
  latitud: number | null;
  longitud: number | null;
  fechaLlegada: string;
  fechaSalida: string;
  orden: number;
}

@Component({
  selector: 'app-crear-viaje',
  imports: [ReactiveFormsModule, CustomDatePicker],
  templateUrl: './crear-viaje.html',
  styleUrl: './crear-viaje.css',
})
export class CrearViaje implements OnDestroy {
  private readonly fb = inject(FormBuilder);
  private readonly viajeService = inject(ViajeService);
  private readonly router = inject(Router);
  private readonly googleMapsService = inject(GoogleMapsService);

  private readonly busquedaParada$ = new Subject<{
    index: number;
    consulta: string;
  }>();

  protected readonly resultadosPorParada = signal<
    Map<number, ResultadoBusqueda[]>
  >(new Map());
  protected readonly buscandoParada = signal<Set<number>>(new Set());

  protected readonly form = this.fb.nonNullable.group({
    titulo: ['', [Validators.required, Validators.maxLength(200)]],
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required],
    presupuestoBase: [0, [Validators.required, Validators.min(0)]],
    paradas: this.fb.array([this.crearParada(1)]),
  });

  private readonly suscripcionBusqueda = this.busquedaParada$
    .pipe(
      debounceTime(500),
      distinctUntilChanged(
        (a, b) => a.index === b.index && a.consulta === b.consulta
      ),
      tap(({ index }) => {
        this.buscandoParada.update((set) => new Set(set).add(index));
      }),
      switchMap(({ index, consulta }) =>
        from(this.consultarAutocomplete(consulta)).pipe(
          map((resultados) => ({ index, resultados }))
        )
      )
    )
    .subscribe(({ index, resultados }) => {
      this.resultadosPorParada.update(
        (mapa) => new Map(mapa).set(index, resultados)
      );
      this.buscandoParada.update((set) => {
        const nuevo = new Set(set);
        nuevo.delete(index);
        return nuevo;
      });
    });

  protected get paradas(): FormArray {
    return this.form.get('paradas') as FormArray;
  }

  protected goBack(): void {
    void this.router.navigate(['/dashboard']);
  }

  protected hasError(
    field: 'titulo' | 'fechaInicio' | 'fechaFin' | 'presupuestoBase'
  ): boolean {
    const control = this.form.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected errorParada(
    index: number,
    error: 'fechasObligatorias' | 'ciudadObligatoria'
  ): boolean {
    const grupo = this.paradas.at(index) as FormGroup;
    return !!grupo.getError(error) && (grupo.dirty || grupo.touched);
  }

  protected agregarParada(): void {
    this.paradas.push(this.crearParada(this.paradas.length + 1));
  }

  protected eliminarParada(index: number): void {
    this.paradas.removeAt(index);
    this.reordenarParadas();
    this.resultadosPorParada.set(new Map());
    this.buscandoParada.set(new Set());
  }

  protected onBuscarCiudad(index: number): void {
    const grupo = this.paradas.at(index) as FormGroup;
    const consulta = (grupo.get('nombreCiudad')?.value ?? '').trim();
    if (!consulta) {
      this.cerrarResultados(index);
      return;
    }
    this.busquedaParada$.next({ index, consulta });
  }

  protected seleccionarCiudad(
    index: number,
    resultado: ResultadoBusqueda
  ): void {
    const grupo = this.paradas.at(index) as FormGroup;
    if (resultado.prediccionLegacy) {
      this.aplicarCiudadLegacy(index, grupo, resultado.prediccionLegacy);
      return;
    }
    const prediccion = resultado.sugerencia?.placePrediction;
    const place = prediccion?.toPlace();
    if (!place) {
      return;
    }
    void place
      .fetchFields({
        fields: ['id', 'displayName', 'formattedAddress', 'location'],
      })
      .then(({ place: detalle }) => {
        const ubicacion = detalle.location;
        if (!ubicacion) {
          return;
        }
        grupo.patchValue({
          nombreCiudad: (
            detalle.displayName ??
            detalle.formattedAddress ??
            resultado.descripcion ??
            ''
          ).slice(0, 200),
          latitud: ubicacion.lat(),
          longitud: ubicacion.lng(),
        });
        this.cerrarResultados(index);
      })
      .catch(() => {
        this.cerrarResultados(index);
      });
  }

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const {
      titulo,
      fechaInicio,
      fechaFin,
      presupuestoBase,
      paradas: paradasRaw,
    } = this.form.getRawValue();

    const paradas: ParadaViaje[] = (paradasRaw as ValorParada[])
      .filter((p) => (p.nombreCiudad ?? '').trim() !== '')
      .map((p, i) => ({
        nombreCiudad: p.nombreCiudad.trim().slice(0, 200),
        latitud: p.latitud,
        longitud: p.longitud,
        fechaLlegada: p.fechaLlegada,
        fechaSalida: p.fechaSalida,
        orden: i + 1,
      }));

    this.viajeService
      .crearViaje({ titulo, fechaInicio, fechaFin, presupuestoBase, paradas })
      .subscribe({
        next: () => void this.router.navigate(['/dashboard']),
      });
  }

  ngOnDestroy(): void {
    this.suscripcionBusqueda.unsubscribe();
  }

  private crearParada(orden: number): FormGroup {
    const grupo = this.fb.group({
      nombreCiudad: ['', Validators.maxLength(200)],
      latitud: [null as number | null],
      longitud: [null as number | null],
      fechaLlegada: [''],
      fechaSalida: [''],
      orden: [orden, Validators.required],
    });
    grupo.addValidators(this.validarParada);
    return grupo;
  }

  private validarParada(grupo: AbstractControl): ValidationErrors | null {
    const valores = grupo.value as Partial<ValorParada>;
    const ciudad = (valores.nombreCiudad ?? '').trim();
    const llegada = valores.fechaLlegada;
    const salida = valores.fechaSalida;
    const errores: ValidationErrors = {};
    if (ciudad && (!llegada || !salida)) {
      errores['fechasObligatorias'] = true;
    }
    if ((llegada || salida) && !ciudad) {
      errores['ciudadObligatoria'] = true;
    }
    return Object.keys(errores).length > 0 ? errores : null;
  }

  private reordenarParadas(): void {
    this.paradas.controls.forEach((control, i) => {
      control.get('orden')?.setValue(i + 1);
    });
  }

  private cerrarResultados(index: number): void {
    this.resultadosPorParada.update((mapa) => {
      const nuevo = new Map(mapa);
      nuevo.delete(index);
      return nuevo;
    });
    this.buscandoParada.update((set) => {
      const nuevo = new Set(set);
      nuevo.delete(index);
      return nuevo;
    });
  }

  private async consultarAutocomplete(
    consulta: string
  ): Promise<ResultadoBusqueda[]> {
    const termino = consulta.trim();
    if (!termino) {
      return [];
    }
    await this.googleMapsService.cargar();

    const nuevaApi = google.maps.places.AutocompleteSuggestion;
    if (nuevaApi?.fetchAutocompleteSuggestions) {
      try {
        const respuesta = await nuevaApi.fetchAutocompleteSuggestions({
          input: termino,
        });
        return respuesta.suggestions.map((sugerencia) => {
          const prediccion = sugerencia.placePrediction;
          return {
            placeId: prediccion?.placeId ?? '',
            descripcion: prediccion?.text?.text ?? '',
            sugerencia,
          };
        });
      } catch {
        return [];
      }
    }

    return new Promise<ResultadoBusqueda[]>((resolve) => {
      const service = new google.maps.places.AutocompleteService();
      service.getPlacePredictions(
        { input: termino },
        (resultados, estado) => {
          if (estado === google.maps.places.PlacesServiceStatus.OK) {
            resolve(
              (resultados ?? []).map((prediccion) => ({
                placeId: prediccion.place_id,
                descripcion: prediccion.description,
                prediccionLegacy: prediccion,
              }))
            );
          } else {
            resolve([]);
          }
        }
      );
    });
  }

  private aplicarCiudadLegacy(
    index: number,
    grupo: FormGroup,
    prediccion: google.maps.places.AutocompletePrediction
  ): void {
    const places = new google.maps.places.PlacesService(
      document.createElement('div')
    );
    places.getDetails(
      {
        placeId: prediccion.place_id,
        fields: ['name', 'formatted_address', 'geometry'],
      },
      (lugar, estado) => {
        if (
          estado === google.maps.places.PlacesServiceStatus.OK &&
          lugar?.geometry?.location
        ) {
          const latlng = lugar.geometry.location;
          grupo.patchValue({
            nombreCiudad: (lugar.name ?? prediccion.description ?? '').slice(
              0,
              200
            ),
            latitud: latlng.lat(),
            longitud: latlng.lng(),
          });
        }
        this.cerrarResultados(index);
      }
    );
  }
}
