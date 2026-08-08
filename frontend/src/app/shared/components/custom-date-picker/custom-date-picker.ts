import {
  Component,
  computed,
  forwardRef,
  signal,
} from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

export interface DiaCalendario {
  numero: number;
  fecha: string;
  hoy: boolean;
}

const MESES = [
  'Enero',
  'Febrero',
  'Marzo',
  'Abril',
  'Mayo',
  'Junio',
  'Julio',
  'Agosto',
  'Septiembre',
  'Octubre',
  'Noviembre',
  'Diciembre',
];

const DIAS_SEMANA = ['L', 'M', 'X', 'J', 'V', 'S', 'D'];

@Component({
  selector: 'app-custom-date-picker',
  imports: [],
  templateUrl: './custom-date-picker.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CustomDatePicker),
      multi: true,
    },
  ],
})
export class CustomDatePicker implements ControlValueAccessor {
  protected readonly abierto = signal(false);
  protected readonly valor = signal('');
  protected readonly mesVisible = signal(
    new Date(new Date().getFullYear(), new Date().getMonth(), 1)
  );

  protected readonly meses = MESES;
  protected readonly diasSemana = DIAS_SEMANA;

  private onChange: (valor: string) => void = () => {};
  private onTouched: () => void = () => {};

  protected readonly etiquetaMes = computed(
    () => `${MESES[this.mesVisible().getMonth()]} ${this.mesVisible().getFullYear()}`
  );

  protected readonly diasCalendario = computed<DiaCalendario[]>(() => {
    const anio = this.mesVisible().getFullYear();
    const mes = this.mesVisible().getMonth();
    const primerDia = new Date(anio, mes, 1);
    const offset = (primerDia.getDay() + 6) % 7; // Semana comienza en lunes
    const totalDias = new Date(anio, mes + 1, 0).getDate();
    const hoy = new Date();
    const hoyKey = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}-${String(hoy.getDate()).padStart(2, '0')}`;

    const celdas: DiaCalendario[] = [];

    for (let i = 0; i < offset; i++) {
      celdas.push({ numero: -1, fecha: '', hoy: false });
    }
    for (let dia = 1; dia <= totalDias; dia++) {
      const key = `${anio}-${String(mes + 1).padStart(2, '0')}-${String(dia).padStart(2, '0')}`;
      celdas.push({ numero: dia, fecha: key, hoy: key === hoyKey });
    }
    return celdas;
  });

  protected readonly fechaSeleccionada = computed(() => {
    const v = this.valor();
    if (!v) {
      return 'Selecciona una fecha';
    }
    const [anio, mes, dia] = v.split('-').map(Number);
    return `${dia} ${MESES[mes - 1].toLowerCase()} ${anio}`;
  });

  protected abrir(): void {
    const v = this.valor();
    if (v) {
      const [anio, mes] = v.split('-').map(Number);
      this.mesVisible.set(new Date(anio, mes - 1, 1));
    }
    this.abierto.set(true);
  }

  protected cerrar(): void {
    this.abierto.set(false);
  }

  protected cambiarMes(delta: number): void {
    this.mesVisible.set(
      new Date(
        this.mesVisible().getFullYear(),
        this.mesVisible().getMonth() + delta,
        1
      )
    );
  }

  protected esSeleccionado(fecha: string): boolean {
    return !!fecha && fecha === this.valor();
  }

  protected elegirDia(dia: DiaCalendario): void {
    if (!dia.fecha) {
      return;
    }
    this.valor.set(dia.fecha);
    this.onChange(dia.fecha);
    this.onTouched();
    this.cerrar();
  }

  protected irAHoy(): void {
    const hoy = new Date();
    this.mesVisible.set(new Date(hoy.getFullYear(), hoy.getMonth(), 1));
    const key = `${hoy.getFullYear()}-${String(hoy.getMonth() + 1).padStart(2, '0')}-${String(hoy.getDate()).padStart(2, '0')}`;
    this.valor.set(key);
    this.onChange(key);
    this.onTouched();
    this.cerrar();
  }

  writeValue(valor: string | null): void {
    this.valor.set(valor ?? '');
  }

  registerOnChange(fn: (valor: string) => void): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: () => void): void {
    this.onTouched = fn;
  }
}