import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  CategoriaDiccionario,
  DiccionarioService,
  FraseDiccionario,
} from '../../core/services/diccionario.service';
import {
  IaService,
  TraduccionResponseDTO,
} from '../../core/services/ia.service';

@Component({
  selector: 'app-diccionario',
  imports: [FormsModule],
  templateUrl: './diccionario.html',
})
export class Diccionario implements OnInit {
  private readonly diccionarioService = inject(DiccionarioService);
  private readonly iaService = inject(IaService);

  protected readonly categorias = signal<CategoriaDiccionario[]>([]);
  protected readonly categoriaSeleccionada =
    signal<CategoriaDiccionario | null>(null);
  protected readonly frasesActuales = signal<FraseDiccionario[]>([]);
  protected readonly cargandoCategorias = signal(false);
  protected readonly cargandoFrases = signal(false);
  protected readonly error = signal<string | null>(null);
  protected readonly idiomaDestino = signal<'ja' | 'en'>('ja');

  protected readonly textoOrigen = signal('');
  protected readonly idiomaTraduccion = signal<'es' | 'en' | 'ja'>('ja');
  protected readonly traduccion = signal<TraduccionResponseDTO | null>(null);
  protected readonly traduciendo = signal(false);
  protected readonly estaGrabando = signal(false);
  protected readonly errorTraduccion = signal<string | null>(null);

  ngOnInit(): void {
    this.cargarCategorias();
  }

  protected cargarCategorias(): void {
    this.cargandoCategorias.set(true);
    this.error.set(null);

    this.diccionarioService.getCategorias().subscribe({
      next: (categorias) => {
        this.categorias.set(categorias);
        this.cargandoCategorias.set(false);
      },
      error: () => {
        this.cargandoCategorias.set(false);
        this.error.set(
          'No se pudieron cargar las categorías. Inténtalo de nuevo.'
        );
      },
    });
  }

  protected abrirCategoria(categoria: CategoriaDiccionario): void {
    this.categoriaSeleccionada.set(categoria);
    this.frasesActuales.set([]);
    this.cargandoFrases.set(true);
    this.error.set(null);

    this.diccionarioService.getFrasesPorCategoria(categoria.id).subscribe({
      next: (frases) => {
        this.frasesActuales.set(frases);
        this.cargandoFrases.set(false);
      },
      error: () => {
        this.cargandoFrases.set(false);
        this.error.set(
          'No se pudieron cargar las frases. Inténtalo de nuevo.'
        );
      },
    });
  }

  protected cerrarCategoria(): void {
    this.categoriaSeleccionada.set(null);
    this.frasesActuales.set([]);
    this.error.set(null);
  }

  protected setIdioma(idioma: 'ja' | 'en'): void {
    this.idiomaDestino.set(idioma);
  }

  protected cambiarIdiomaTraduccion(idioma: 'es' | 'en' | 'ja'): void {
    this.idiomaTraduccion.set(idioma);
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
        idiomaDestino: this.idiomaTraduccion(),
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
    const lang = this.idiomaTraduccion() === 'ja'
      ? 'ja-JP'
      : this.idiomaTraduccion() === 'en'
        ? 'en-US'
        : 'es-ES';
    utterance.lang = lang;
    window.speechSynthesis.speak(utterance);
  }

  protected iconoEmoji(icono: string): string {
    const mapa: Record<string, string> = {
      'train-outline': '🚆',
      'restaurant-outline': '🍜',
      'bag-handle-outline': '🛍️',
      'medkit-outline': '🚑',
    };
    return mapa[icono] ?? '💬';
  }
}