import { Component, inject } from '@angular/core';
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

  protected readonly form = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  protected logueando = false;
  protected error = '';

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.logueando = true;
    this.error = '';

    const { email, password } = this.form.getRawValue();
    this.authService.login({ email, password }).subscribe({
      next: () => this.router.navigate(['/dashboard']),
      error: (err: unknown) => {
        this.logueando = false;
        this.error =
          this.extraerMensajeError(err) ??
          'No se pudo iniciar sesión. Inténtalo de nuevo.';
      },
    });
  }

  /**
   * Inicio de sesion de un solo clic para los usuarios de desarrollo
   * sembrados por el backend (DatabaseSeeder): rellena el formulario y
   * dispara el login inmediatamente.
   */
  protected accesoRapido(usuario: 'javi' | 'cynthia'): void {
    const credenciales =
      usuario === 'javi'
        ? { email: 'javi@viajes.com', password: 'password' }
        : { email: 'cynthia@viajes.com', password: 'password' };
    this.form.patchValue(credenciales);
    this.onSubmit();
  }

  protected hasError(field: 'email' | 'password'): boolean {
    const control = this.form.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private extraerMensajeError(error: unknown): string | null {
    const body = error as { error?: { message?: string } };
    return typeof body?.error?.message === 'string'
      ? body.error.message
      : null;
  }
}