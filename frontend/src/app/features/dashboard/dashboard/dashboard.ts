import { Component, inject, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ViajeService } from '../../../core/services/viaje.service';

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, CurrencyPipe, ReactiveFormsModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private readonly viajeService = inject(ViajeService);
  private readonly fb = inject(FormBuilder);

  protected readonly viajes = this.viajeService.viajes;

  protected modalUnirse = false;
  protected errorUnirse = '';

  protected readonly formUnirse = this.fb.nonNullable.group({
    uuid: ['', [Validators.required, Validators.pattern(/^[0-9a-fA-F-]{8,}$/)]],
  });

  ngOnInit(): void {
    this.viajeService.cargarMisViajes();
  }

  protected abrirModalUnirse(): void {
    this.formUnirse.reset();
    this.errorUnirse = '';
    this.modalUnirse = true;
  }

  protected cerrarModalUnirse(): void {
    this.modalUnirse = false;
  }

  protected unirse(): void {
    if (this.formUnirse.invalid) {
      this.formUnirse.markAllAsTouched();
      return;
    }

    const uuid = this.formUnirse.getRawValue().uuid.trim();
    this.viajeService.unirseViaje(uuid).subscribe({
      next: () => this.cerrarModalUnirse(),
      error: () => {
        this.errorUnirse = 'La invitación no es válida o ya ha sido respondida.';
      },
    });
  }

  protected hasError(field: 'uuid'): boolean {
    const control = this.formUnirse.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }
}