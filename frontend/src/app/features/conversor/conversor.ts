import { Component, inject, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { DivisaService } from '../../core/services/divisa.service';
import { DivisaResponseDTO } from '../../shared/types';

@Component({
  selector: 'app-conversor',
  imports: [FormsModule, DecimalPipe],
  templateUrl: './conversor.html',
  styleUrl: './conversor.css',
})
export class Conversor {
  private readonly divisaService = inject(DivisaService);

  protected readonly monto = signal(1);
  protected readonly monedaOrigen = signal('EUR');
  protected readonly monedaDestino = signal('JPY');
  protected readonly resultado = signal<DivisaResponseDTO | null>(null);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);

  constructor() {
    this.convertir();
  }

  protected actualizarMonto(valor: number): void {
    this.monto.set(valor);
    this.convertir();
  }

  protected convertir(): void {
    const monto = this.monto();
    if (!(monto > 0)) {
      this.error.set('Introduce una cantidad mayor que 0.');
      return;
    }
    this.cargando.set(true);
    this.error.set(null);
    this.divisaService
      .convertir(monto, this.monedaOrigen(), this.monedaDestino())
      .subscribe({
        next: (res) => {
          this.resultado.set(res);
          this.cargando.set(false);
        },
        error: () => {
          this.resultado.set(null);
          this.cargando.set(false);
          this.error.set(
            'No se pudo realizar la conversión. Inténtalo de nuevo.'
          );
        },
      });
  }

  protected invertirMonedas(): void {
    const origen = this.monedaOrigen();
    this.monedaOrigen.set(this.monedaDestino());
    this.monedaDestino.set(origen);
    this.convertir();
  }
}
