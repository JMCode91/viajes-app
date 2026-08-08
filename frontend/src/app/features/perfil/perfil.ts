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

  protected readonly inicial = computed(() =>
    (this.usuario()?.nombre || '?').charAt(0).toUpperCase()
  );

  protected toggleTheme(): void {
    this.themeService.toggle();
  }

  protected cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}