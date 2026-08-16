import { ChangeDetectorRef, Component, inject } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected cargando = false;
  protected mensajeError = '';

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando = true;
    this.mensajeError = '';

    const { email, password } = this.form.getRawValue();
    this.authService.login({ email, password }).subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err: unknown) => {
        console.error('Error capturado en login:', err);
        this.mensajeError =
          this.mensajeCuentaNoActivada(err) ??
          this.extraerMensajeError(err) ??
          'No se pudo iniciar sesión. Inténtalo de nuevo.';
        this.cargando = false;
        this.cdr.detectChanges();
      },
    });
  }

  protected hasError(field: 'email' | 'password'): boolean {
    const control = this.form.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  /**
   * Detecta el 403 de una cuenta no activada (backend lanza
   * `CuentaNoActivadaException`) y devuelve un mensaje claro para el banner.
   */
  private mensajeCuentaNoActivada(error: unknown): string | null {
    const body = error as { status?: number; error?: { message?: string } };
    const es403 = body?.status === 403;
    const mensaje = body?.error?.message ?? '';
    const esCuentaNoActivada =
      mensaje.toLowerCase().includes('no activada') ||
      mensaje.toLowerCase().includes('verificar');
    return es403 || esCuentaNoActivada
      ? 'Falta verificar el correo. Revisa tu bandeja de entrada.'
      : null;
  }

  private extraerMensajeError(error: unknown): string | null {
    const body = error as { error?: { message?: string } };
    return typeof body?.error?.message === 'string'
      ? body.error.message
      : null;
  }
}