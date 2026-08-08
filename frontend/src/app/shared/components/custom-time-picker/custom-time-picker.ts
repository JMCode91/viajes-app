import { Component, computed, forwardRef, signal } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

const HORAS = Array.from({ length: 24 }, (_, i) => String(i).padStart(2, '0'));
const MINUTOS = Array.from({ length: 12 }, (_, i) =>
  String(i * 5).padStart(2, '0')
);

@Component({
  selector: 'app-custom-time-picker',
  imports: [],
  templateUrl: './custom-time-picker.html',
  styleUrl: './custom-time-picker.css',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => CustomTimePicker),
      multi: true,
    },
  ],
})
export class CustomTimePicker implements ControlValueAccessor {
  protected readonly abierto = signal(false);
  protected readonly valor = signal('');
  protected readonly horaSel = signal('09');
  protected readonly minutoSel = signal('00');

  protected readonly horas = HORAS;
  protected readonly minutos = MINUTOS;

  private onChange: (valor: string) => void = () => {};
  private onTouched: () => void = () => {};

  protected readonly horaDisplay = computed(
    () => `${this.horaSel()}:${this.minutoSel()}`
  );

  protected abrir(): void {
    const v = this.valor();
    if (v) {
      const [h, m] = v.split(':');
      this.horaSel.set(h || '09');
      this.minutoSel.set(m || '00');
    }
    this.abierto.set(true);
  }

  protected cerrar(): void {
    this.abierto.set(false);
  }

  protected elegirHora(hora: string): void {
    this.horaSel.set(hora);
  }

  protected elegirMinuto(minuto: string): void {
    this.minutoSel.set(minuto);
  }

  protected confirmar(): void {
    const resultado = `${this.horaSel()}:${this.minutoSel()}`;
    this.valor.set(resultado);
    this.onChange(resultado);
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