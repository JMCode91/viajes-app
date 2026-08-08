import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-perfil',
  imports: [],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly usuario = this.authService.usuario;

  protected readonly inicial = computed(() =>
    (this.usuario()?.nombre || '?').charAt(0).toUpperCase()
  );

  protected cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}