import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';

import { ViajeService } from '../../../core/services/viaje.service';

export type Tab = 'itinerario' | 'mapa' | 'checklist';

export interface TabItem {
  key: Tab;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-detalle-viaje',
  imports: [DatePipe],
  templateUrl: './detalle-viaje.html',
  styleUrl: './detalle-viaje.css',
})
export class DetalleViaje {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly viajeService = inject(ViajeService);

  protected readonly viaje = this.viajeService.viajeDetalle;
  protected readonly tabActiva = signal<Tab>('itinerario');

  protected readonly tabs: TabItem[] = [
    {
      key: 'itinerario',
      label: 'Itinerario',
      icon: 'M6.75 3v2.25M17.25 3v2.25M3 18.75V7.5a2.25 2.25 0 0 1 2.25-2.25h13.5A2.25 2.25 0 0 1 21 7.5v11.25m-18 0A2.25 2.25 0 0 0 5.25 21h13.5A2.25 2.25 0 0 0 21 18.75m-18 0v-7.5A2.25 2.25 0 0 1 5.25 9h13.5A2.25 2.25 0 0 1 21 11.25v7.5',
    },
    {
      key: 'mapa',
      label: 'Mapa',
      icon: 'M12 21a9 9 0 1 1 0-18 9 9 0 0 1 0 18Zm-3.5-5.5 1.75-4.25 4.25-1.75-1.75 4.25-4.25 1.75Zm2-2.75 1.25-1.25',
    },
    {
      key: 'checklist',
      label: 'Checklist',
      icon: 'M9 12.75 11.25 15 15 9.75M21 12c0 1.268-.63 2.39-1.593 3.068a3.745 3.745 0 0 1-1.043 3.296 3.745 3.745 0 0 1-3.296 1.043A3.745 3.745 0 0 1 12 21c-1.268 0-2.39-.63-3.068-1.593a3.746 3.746 0 0 1-3.296-1.043 3.745 3.745 0 0 1-1.043-3.296A3.745 3.745 0 0 1 3 12c0-1.268.63-2.39 1.593-3.068a3.745 3.745 0 0 1 1.043-3.296 3.746 3.746 0 0 1 3.296-1.043A3.746 3.746 0 0 1 12 3c1.268 0 2.39.63 3.068 1.593a3.746 3.746 0 0 1 3.296 1.043 3.746 3.746 0 0 1 1.043 3.296A3.745 3.745 0 0 1 21 12Z',
    },
  ];

  constructor() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.viajeService.cargarViaje(id);
    }
  }

  protected goBack(): void {
    void this.router.navigate(['/dashboard']);
  }

  protected setTab(tab: Tab): void {
    this.tabActiva.set(tab);
  }
}