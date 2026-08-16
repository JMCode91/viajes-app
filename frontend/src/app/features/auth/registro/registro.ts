import { Component, inject } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { take } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-registro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css',
})
export class Registro {
  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly form = this.fb.nonNullable.group(
    {
      nombre: ['', [Validators.required, Validators.maxLength(150)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmarPassword: ['', Validators.required],
    },
    { validators: this.passwordsCoinciden }
  );

  protected registrando = false;
  protected registrado = false;
  protected error = '';

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.registrando = true;
    this.error = '';

    const { nombre, email, password, confirmarPassword } =
      this.form.getRawValue();
    this.authService
      .register({ nombre, email, password, confirmarPassword })
      .pipe(take(1))
      .subscribe({
        next: () => {
          this.registrando = false;
          this.registrado = true;
          setTimeout(() => this.router.navigate(['/login']), 3000);
        },
        error: (err: unknown) => {
          this.registrando = false;
          this.error =
            this.extraerMensajeError(err) ??
            'No se pudo crear la cuenta. Inténtalo de nuevo.';
        },
      });
  }

  protected hasError(
    field: 'nombre' | 'email' | 'password' | 'confirmarPassword'
  ): boolean {
    const control = this.form.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected passwordsNoCoinciden(): boolean {
    return (
      !!this.form.hasError('passwordsNoCoinciden') &&
      (this.form.get('confirmarPassword')?.dirty ||
        this.form.get('confirmarPassword')?.touched) === true
    );
  }

  private passwordsCoinciden(form: AbstractControl): { [key: string]: boolean } | null {
    const password = form.get('password')?.value as string;
    const confirmar = form.get('confirmarPassword')?.value as string;
    return password !== confirmar ? { passwordsNoCoinciden: true } : null;
  }

  private extraerMensajeError(error: unknown): string | null {
    const body = error as { error?: { message?: string } };
    return typeof body?.error?.message === 'string'
      ? body.error.message
      : null;
  }
}