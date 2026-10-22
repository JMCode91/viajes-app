import { Component, inject, OnInit } from '@angular/core';
import { DatePipe, NgClass } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { ThemeService } from '../../../core/services/theme.service';
import { ViajeService } from '../../../core/services/viaje.service';
import { Viaje } from '../../../shared/types';

@Component({
  selector: 'app-dashboard',
  imports: [DatePipe, NgClass, ReactiveFormsModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
  private readonly viajeService = inject(ViajeService);
  private readonly themeService = inject(ThemeService);
  private readonly fb = inject(FormBuilder);

  protected readonly viajes = this.viajeService.viajes;

  protected modalUnirse = false;
  protected errorUnirse = '';

  protected modalAccion = false;
  protected viajeAccion: Viaje | null = null;
  protected tipoAccion: 'eliminar' | 'abandonar' | null = null;
  protected procesandoAccion = false;
  protected errorAccion = '';

  protected readonly formUnirse = this.fb.nonNullable.group({
    uuid: ['', [Validators.required, Validators.pattern(/^[0-9a-fA-F-]{8,}$/)]],
  });

  ngOnInit(): void {
    this.themeService.cargarFondoPantalla();
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
      next: () => {
        this.cerrarModalUnirse();
        this.viajeService.cargarMisViajes();
      },
      error: () => {
        this.errorUnirse = 'La invitación no es válida o ya ha sido respondida.';
      },
    });
  }

  protected esEliminar(viaje: Viaje): boolean {
    return viaje.rolEnViaje === 'PROPIETARIO';
  }

  protected inicialParticipante(nombre: string | null | undefined): string {
    const limpio = (nombre ?? '').trim();
    return limpio ? limpio.charAt(0).toUpperCase() : '?';
  }

  protected abrirAccion(viaje: Viaje, tipo: 'eliminar' | 'abandonar'): void {
    this.viajeAccion = viaje;
    this.tipoAccion = tipo;
    this.errorAccion = '';
    this.modalAccion = true;
  }

  protected cerrarAccion(): void {
    this.modalAccion = false;
    this.viajeAccion = null;
    this.tipoAccion = null;
    this.errorAccion = '';
  }

  protected confirmarAccion(): void {
    if (!this.viajeAccion || !this.tipoAccion || this.procesandoAccion) {
      return;
    }

    const viaje = this.viajeAccion;
    const esEliminacion = this.tipoAccion === 'eliminar';
    this.procesandoAccion = true;
    this.errorAccion = '';

    const operacion = esEliminacion
      ? this.viajeService.eliminarViaje(viaje.id)
      : this.viajeService.abandonarViaje(viaje.id);

    operacion.subscribe({
      next: () => {
        this.procesandoAccion = false;
        this.cerrarAccion();
        this.viajeService.cargarMisViajes();
      },
      error: () => {
        this.procesandoAccion = false;
        this.errorAccion = esEliminacion
          ? 'No se pudo eliminar el viaje.'
          : 'No se pudo abandonar el viaje.';
      },
    });
  }

  protected hasError(field: 'uuid'): boolean {
    const control = this.formUnirse.get(field);
    return !!control && control.invalid && (control.dirty || control.touched);
  }
}