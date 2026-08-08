import { effect, Injectable, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

const THEME_KEY = 'theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  readonly theme = signal<Theme>(
    localStorage.getItem(THEME_KEY) === 'dark' ? 'dark' : 'light'
  );

  constructor() {
    effect(() => this.apply(this.theme()));
  }

  toggle(): void {
    const next: Theme = this.theme() === 'dark' ? 'light' : 'dark';
    localStorage.setItem(THEME_KEY, next);
    this.theme.set(next);
  }

  private apply(theme: Theme): void {
    document.documentElement.classList.toggle('dark', theme === 'dark');
  }
}