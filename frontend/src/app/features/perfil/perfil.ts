import { Component, computed, effect, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthService } from '../../core/services/auth.service';
import { ThemeService, type Theme } from '../../core/services/theme.service';
import { UsuarioService } from '../../core/services/usuario.service';
import { UsuarioPerfil } from '../../shared/types';

@Component({
  selector: 'app-perfil',
  imports: [ReactiveFormsModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css',
})
export class Perfil implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly router = inject(Router);
  private readonly themeService = inject(ThemeService);
  private readonly fb = inject(FormBuilder);

  protected readonly usuario = this.authService.usuario;
  protected readonly theme = this.themeService.theme;

  protected readonly perfil = signal<UsuarioPerfil | null>(null);

  protected readonly notificacionesGastos = signal(true);
  protected readonly notificacionesPlanes = signal(true);

  protected subiendoAvatar = false;
  protected errorAvatar: string | null = null;

  protected panelCuenta = false;
  protected panelNotificaciones = false;
  protected panelApariencia = false;
  protected guardandoPassword = false;
  protected errorPassword: string | null = null;
  protected passwordOk = false;

  protected guardandoPreferencias = false;
  protected errorPreferencias: string | null = null;

  protected subiendoFondo = false;
  protected errorFondo: string | null = null;

  protected readonly formPassword = this.fb.nonNullable.group({
    passwordActual: ['', [Validators.required]],
    nuevaPassword: [
      '',
      [Validators.required, Validators.minLength(6), Validators.maxLength(255)],
    ],
  });

  constructor() {
    // Sincroniza las preferencias del servidor con las senales de los toggles.
    effect(() => {
      const perfil = this.perfil();
      if (perfil) {
        this.notificacionesGastos.set(perfil.notificacionesGastos);
        this.notificacionesPlanes.set(perfil.notificacionesPlanes);
      }
    });
  }

  ngOnInit(): void {
    this.refrescarPerfil();
  }

  protected readonly inicial = computed(() =>
    (this.perfil()?.nombre ?? this.usuario()?.nombre ?? '?')
      .charAt(0)
      .toUpperCase()
  );

  protected readonly claseToggleGastos = computed(() =>
    this.notificacionesGastos()
      ? 'bg-blue-600'
      : 'bg-gray-300 dark:bg-gray-600'
  );

  protected readonly claseTogglePlanes = computed(() =>
    this.notificacionesPlanes()
      ? 'bg-blue-600'
      : 'bg-gray-300 dark:bg-gray-600'
  );

  protected cambiarTema(tema: Theme): void {
    this.themeService.setTheme(tema);
  }

  protected togglePanelCuenta(): void {
    this.panelCuenta = !this.panelCuenta;
    if (this.panelCuenta) {
      this.panelNotificaciones = false;
      this.panelApariencia = false;
      this.errorPassword = null;
      this.passwordOk = false;
    }
  }

  protected togglePanelNotificaciones(): void {
    this.panelNotificaciones = !this.panelNotificaciones;
    if (this.panelNotificaciones) {
      this.panelCuenta = false;
      this.panelApariencia = false;
      this.errorPreferencias = null;
    }
  }

  protected togglePanelApariencia(): void {
    this.panelApariencia = !this.panelApariencia;
    if (this.panelApariencia) {
      this.panelCuenta = false;
      this.panelNotificaciones = false;
      this.errorFondo = null;
    }
  }

  protected hasErrorPassword(campo: 'passwordActual' | 'nuevaPassword'): boolean {
    const control = this.formPassword.get(campo);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected cambiarPassword(): void {
    if (this.formPassword.invalid) {
      this.formPassword.markAllAsTouched();
      return;
    }
    if (this.guardandoPassword) {
      return;
    }

    const { passwordActual, nuevaPassword } = this.formPassword.getRawValue();
    this.guardandoPassword = true;
    this.errorPassword = null;
    this.passwordOk = false;

    this.usuarioService.cambiarPassword({ passwordActual, nuevaPassword }).subscribe({
      next: () => {
        this.guardandoPassword = false;
        this.passwordOk = true;
        this.formPassword.reset();
      },
      error: (error: unknown) => {
        this.guardandoPassword = false;
        this.errorPassword =
          this.extraerMensajeError(error) ??
          'No se pudo cambiar la contraseña. Revisa los datos e inténtalo de nuevo.';
      },
    });
  }

  protected cambiarNotificacion(
    campo: 'notificacionesGastos' | 'notificacionesPlanes',
    valor: boolean
  ): void {
    if (campo === 'notificacionesGastos') {
      this.notificacionesGastos.set(valor);
    } else {
      this.notificacionesPlanes.set(valor);
    }
    this.guardarPreferencias();
  }

  private guardarPreferencias(): void {
    if (this.guardandoPreferencias) {
      return;
    }
    this.guardandoPreferencias = true;
    this.errorPreferencias = null;

    this.usuarioService
      .actualizarPreferencias({
        notificacionesGastos: this.notificacionesGastos(),
        notificacionesPlanes: this.notificacionesPlanes(),
      })
      .subscribe({
        next: (perfil) => {
          this.guardandoPreferencias = false;
          this.perfil.set(perfil);
        },
        error: () => {
          this.guardandoPreferencias = false;
          this.errorPreferencias =
            'No se pudieron guardar las preferencias. Inténtalo de nuevo.';
        },
      });
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
        this.refrescarPerfil();
      },
      error: () => {
        this.subiendoAvatar = false;
        this.errorAvatar = 'No se pudo actualizar tu foto de perfil.';
      },
    });
  }

  protected onFondoSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];
    input.value = '';
    if (!file || this.subiendoFondo) {
      return;
    }

    this.subiendoFondo = true;
    this.errorFondo = null;
    this.usuarioService.subirFondoPantalla(file).subscribe({
      next: (perfil) => {
        this.subiendoFondo = false;
        this.perfil.set(perfil);
        this.themeService.setFondoPantallaUrl(perfil.fondoPantallaUrl);
      },
      error: () => {
        this.subiendoFondo = false;
        this.errorFondo = 'No se pudo actualizar tu fondo de pantalla.';
      },
    });
  }

  protected cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  private refrescarPerfil(): void {
    this.usuarioService.obtenerPerfil().subscribe({
      next: (perfil) => {
        this.perfil.set(perfil);
        this.themeService.setFondoPantallaUrl(perfil.fondoPantallaUrl);
      },
      error: () => this.perfil.set(null),
    });
  }

  private extraerMensajeError(error: unknown): string | null {
    const body = error as { error?: { message?: string } };
    return typeof body?.error?.message === 'string'
      ? body.error.message
      : null;
  }
}