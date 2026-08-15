import { effect, Injectable, signal } from '@angular/core';

export type Theme = 'light' | 'dark' | 'image';

const THEME_KEY = 'theme';
const VALID_THEMES: readonly Theme[] = ['light', 'dark', 'image'];

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly theme = signal<Theme>(readTheme());

  constructor() {
    effect(() => this.apply(this.theme()));
  }

  setTheme(theme: Theme): void {
    if (!VALID_THEMES.includes(theme)) {
      return;
    }
    localStorage.setItem(THEME_KEY, theme);
    this.theme.set(theme);
  }

  private apply(theme: Theme): void {
    // El modo imagen comparte el esquema oscuro (mismo contraste de texto),
    // pero se distingue por la clase `theme-image` en <body>, que aplica el
    // fondo fotografico global y el glassmorfismo.
    const dark = theme === 'dark' || theme === 'image';
    document.documentElement.classList.toggle('dark', dark);
    document.body.classList.toggle('theme-image', theme === 'image');
  }
}

function readTheme(): Theme {
  const stored = localStorage.getItem(THEME_KEY) as Theme | null;
  return VALID_THEMES.includes(stored as Theme) ? (stored as Theme) : 'light';
}
