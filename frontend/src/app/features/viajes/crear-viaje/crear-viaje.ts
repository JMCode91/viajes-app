import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { ViajeService } from '../../../core/services/viaje.service';

@Component({
  selector: 'app-crear-viaje',
  imports: [ReactiveFormsModule],
  templateUrl: './crear-viaje.html',
  styleUrl: './crear-viaje.css',
})
export class CrearViaje {
  private readonly fb = inject(FormBuilder);
  private readonly viajeService = inject(ViajeService);
  private readonly router = inject(Router);

  protected readonly form = this.fb.nonNullable.group({
    titulo: ['', [Validators.required, Validators.maxLength(200)]],
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required],
    presupuestoBase: [0, [Validators.required, Validators.min(0)]],
  });

  protected goBack(): void {
    void this.router.navigate(['/dashboard']);
  }

  protected hasError(field: 'titulo' | 'fechaInicio' | 'fechaFin' | 'presupuestoBase'): boolean {
    const control = this.form.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { titulo, fechaInicio, fechaFin, presupuestoBase } = this.form.getRawValue();
    this.viajeService.crearViaje({ titulo, fechaInicio, fechaFin, presupuestoBase }).subscribe({
      next: () => void this.router.navigate(['/dashboard']),
    });
  }
}