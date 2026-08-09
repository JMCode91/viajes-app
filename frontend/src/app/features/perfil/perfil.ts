import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-perfil',
  imports: [],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly themeService = inject(ThemeService);

  protected readonly usuario = this.authService.usuario;
  protected readonly theme = this.themeService.theme;

  protected subiendoAvatar = false;
  protected errorAvatar: string | null = null;

  protected readonly inicial = computed(() =>
    (this.usuario()?.nombre || '?').charAt(0).toUpperCase()
  );

  protected toggleTheme(): void {
    this.themeService.toggle();
  }

  protected onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file || this.subiendoAvatar) {
      return;
    }

    this.subiendoAvatar = true;
    this.errorAvatar = null;
    this.authService.uploadAvatar(file).subscribe({
      next: () => {
        this.subiendoAvatar = false;
      },
      error: () => {
        this.subiendoAvatar = false;
        this.errorAvatar = 'No se pudo actualizar tu foto de perfil.';
      },
    });
  }

  protected cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}