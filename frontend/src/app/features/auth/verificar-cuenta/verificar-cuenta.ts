import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-verificar-cuenta',
  imports: [RouterLink],
  templateUrl: './verificar-cuenta.html',
  styleUrl: './verificar-cuenta.css',
})
export class VerificarCuenta implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected verificando = true;
  protected verificado = false;
  protected error = '';

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.verificando = false;
      this.error = 'No se encontró el token de verificación.';
      return;
    }

    this.authService.verificarCuenta(token).subscribe({
      next: () => {
        this.verificando = false;
        this.verificado = true;
      },
      error: (err: unknown) => {
        this.verificando = false;
        this.error =
          this.extraerMensajeError(err) ??
          'No se pudo verificar la cuenta. Inténtalo de nuevo.';
      },
    });
  }

  protected irAlLogin(): void {
    this.router.navigate(['/login']);
  }

  private extraerMensajeError(error: unknown): string | null {
    const body = error as { error?: { message?: string } };
    return typeof body?.error?.message === 'string'
      ? body.error.message
      : null;
  }
}