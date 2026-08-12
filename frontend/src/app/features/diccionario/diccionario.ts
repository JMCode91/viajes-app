import { Component, inject, OnInit, signal } from '@angular/core';

import {
  CategoriaDiccionario,
  DiccionarioService,
  FraseDiccionario,
} from '../../core/services/diccionario.service';

@Component({
  selector: 'app-diccionario',
  imports: [],
  templateUrl: './diccionario.html',
})
export class Diccionario implements OnInit {
  private readonly diccionarioService = inject(DiccionarioService);

  protected readonly categorias = signal<CategoriaDiccionario[]>([]);
  protected readonly categoriaSeleccionada =
    signal<CategoriaDiccionario | null>(null);
  protected readonly frasesActuales = signal<FraseDiccionario[]>([]);
  protected readonly cargandoCategorias = signal(false);
  protected readonly cargandoFrases = signal(false);
  protected readonly error = signal<string | null>(null);

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