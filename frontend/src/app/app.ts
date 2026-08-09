import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { GoogleMapsService } from './core/services/google-maps.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');

  private readonly googleMapsService = inject(GoogleMapsService);

  constructor() {
    void this.googleMapsService.cargar();
  }
}
