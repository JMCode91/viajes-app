import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { DivisaService } from '../../core/services/divisa.service';
import { IaService, TraduccionResponseDTO } from '../../core/services/ia.service';
import { DivisaResponseDTO } from '../../shared/types';

interface MonedaDisponible {
  codigo: string;
  nombre: string;
}

@Component({
  selector: 'app-conversor',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './conversor.html',
  styleUrl: './conversor.css',
})
export class Conversor implements OnInit {
  private readonly divisaService = inject(DivisaService);
  private readonly iaService = inject(IaService);

  protected readonly monto = signal(1);
  protected readonly monedaOrigen = signal('EUR');
  protected readonly monedaDestino = signal('JPY');
  protected readonly monedasDisponibles = signal<MonedaDisponible[]>([]);
  protected readonly resultado = signal<DivisaResponseDTO | null>(null);
  protected readonly cargando = signal(false);
  protected readonly error = signal<string | null>(null);

  protected readonly textoOrigen = signal('');
  protected readonly idiomaDestino = signal<'ja' | 'en'>('ja');
  protected readonly traduccion = signal<TraduccionResponseDTO | null>(null);
  protected readonly traduciendo = signal(false);
  protected readonly estaGrabando = signal(false);
  protected readonly errorTraduccion = signal<string | null>(null);

  ngOnInit(): void {
    this.convertir();
    this.cargarMonedas();
  }

  protected cargarMonedas(): void {
    this.divisaService.obtenerMonedas().subscribe({
      next: (monedas) => {
        this.monedasDisponibles.set(
          Object.entries(monedas).map(([codigo, nombre]) => ({
            codigo,
            nombre,
          }))
        );
      },
      error: () => {
        this.error.set(
          'No se pudo cargar la lista de monedas. Inténtalo de nuevo.'
        );
      },
    });
  }

  protected obtenerNombreMoneda(codigo: string | undefined): string {
    if (!codigo) {
      return '';
    }
    return (
      this.monedasDisponibles().find((moneda) => moneda.codigo === codigo)
        ?.nombre ?? codigo
    );
  }

  protected actualizarMonto(valor: number): void {
    this.monto.set(valor);
    this.convertir();
  }

  protected cambiarMonedaOrigen(codigo: string): void {
    this.monedaOrigen.set(codigo);
    this.convertir();
  }

  protected cambiarMonedaDestino(codigo: string): void {
    this.monedaDestino.set(codigo);
    this.convertir();
  }

  protected convertir(): void {
    this.error.set(null);
    const monto = this.monto();
    if (!(monto > 0)) {
      this.error.set('Introduce una cantidad mayor que 0.');
      return;
    }
    if (this.monedaOrigen() === this.monedaDestino()) {
      this.resultado.set({
        montoOriginal: monto,
        monedaOriginal: this.monedaOrigen(),
        montoConvertido: monto,
        monedaDestino: this.monedaDestino(),
        ratioCambio: 1,
        fechaActualizacion: new Date().toISOString().slice(0, 10),
      });
      return;
    }
    this.cargando.set(true);
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

  protected cambiarIdiomaTraduccion(idioma: 'ja' | 'en'): void {
    this.idiomaDestino.set(idioma);
  }

  protected iniciarDictado(): void {
    const SpeechRecognition =
      (window as any).webkitSpeechRecognition ||
      (window as any).SpeechRecognition;

    if (!SpeechRecognition) {
      this.errorTraduccion.set(
        'Tu navegador no soporta el reconocimiento de voz.'
      );
      return;
    }

    const reconocimiento = new SpeechRecognition();
    reconocimiento.lang = 'es-ES';
    reconocimiento.interimResults = false;
    reconocimiento.maxAlternatives = 1;

    reconocimiento.onstart = () => {
      this.estaGrabando.set(true);
    };

    reconocimiento.onresult = (event: any) => {
      const transcripcion = Array.from(event.results)
        .map((resultado: any) => resultado[0].transcript)
        .join(' ');
      this.textoOrigen.set(transcripcion);
    };

    reconocimiento.onerror = () => {
      this.estaGrabando.set(false);
      this.errorTraduccion.set(
        'No se pudo capturar tu voz. Inténtalo de nuevo.'
      );
    };

    reconocimiento.onend = () => {
      this.estaGrabando.set(false);
    };

    reconocimiento.start();
  }

  protected traducir(): void {
    const texto = this.textoOrigen().trim();
    if (!texto) {
      this.errorTraduccion.set('Escribe o dicta un texto para traducir.');
      return;
    }

    this.traduciendo.set(true);
    this.errorTraduccion.set(null);

    this.iaService
      .traducirTexto({
        texto,
        idiomaDestino: this.idiomaDestino(),
      })
      .subscribe({
        next: (res) => {
          this.traduccion.set(res);
          this.traduciendo.set(false);
        },
        error: () => {
          this.traduccion.set(null);
          this.traduciendo.set(false);
          this.errorTraduccion.set(
            'No se pudo traducir el texto. Inténtalo de nuevo.'
          );
        },
      });
  }

  protected reproducirAudio(): void {
    const traduccion = this.traduccion();
    const texto = traduccion?.textoTraducido;
    if (!texto) {
      return;
    }

    const utterance = new SpeechSynthesisUtterance(texto);
    utterance.lang = this.idiomaDestino() === 'ja' ? 'ja-JP' : 'en-US';
    window.speechSynthesis.speak(utterance);
  }
}
