import { computed, effect, inject, Injectable, signal } from '@angular/core';

import { UsuarioService } from './usuario.service';

export type Theme = 'light' | 'dark' | 'image';

const THEME_KEY = 'theme';
const VALID_THEMES: readonly Theme[] = ['light', 'dark', 'image'];

/** Fondo por defecto del Modo Imagen (Unsplash) cuando el usuario no tiene uno propio. */
const DEFAULT_IMAGE_URL =
  'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?q=80&w=2070&auto=format&fit=crop';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly usuarioService = inject(UsuarioService);

  /** Fondo personalizado del usuario (fondoPantallaUrl del perfil) o null para el por defecto. */
  private readonly fondoPantallaUrl = signal<string | null>(null);

  readonly theme = signal<Theme>(readTheme());

  constructor() {
    // Intenta usar el fondo guardado en el perfil del usuario autenticado;
    // si no hay sesion o falla la peticion, se queda en null (Unsplash).
    this.cargarFondoPantalla();
    effect(() => this.apply(this.theme()));
  }

  setTheme(theme: Theme): void {
    if (!VALID_THEMES.includes(theme)) {
      return;
    }
    localStorage.setItem(THEME_KEY, theme);
    this.theme.set(theme);
  }

  /** Actualiza el fondo personalizado del usuario (o null para usar el por defecto). */
  setFondoPantallaUrl(url: string | null): void {
    this.fondoPantallaUrl.set(url);
  }

  /** URL efectiva del fondo del Modo Imagen (la del perfil o la de Unsplash). */
  readonly fondoImagenUrl = computed(() => this.fondoPantallaUrl() || DEFAULT_IMAGE_URL);

  private cargarFondoPantalla(): void {
    this.usuarioService.obtenerPerfil().subscribe({
      next: (perfil) => this.fondoPantallaUrl.set(perfil.fondoPantallaUrl),
      error: () => this.fondoPantallaUrl.set(null),
    });
  }

  private apply(theme: Theme): void {
    // El modo imagen comparte el esquema oscuro (mismo contraste de texto),
    // pero se distingue por la clase `theme-image` en <body>, que aplica el
    // fondo fotografico global y el glassmorfismo.
    const dark = theme === 'dark' || theme === 'image';
    document.documentElement.classList.toggle('dark', dark);
    const isImage = theme === 'image';
    document.body.classList.toggle('theme-image', isImage);
    if (isImage) {
      // Usa la URL del perfil si existe; si no, la foto por defecto de Unsplash.
      const url = this.fondoPantallaUrl() || DEFAULT_IMAGE_URL;
      document.body.style.setProperty('--theme-image-bg', `url("${url}")`);
    }
  }
}

function readTheme(): Theme {
  const stored = localStorage.getItem(THEME_KEY) as Theme | null;
  return VALID_THEMES.includes(stored as Theme) ? (stored as Theme) : 'image';
}
