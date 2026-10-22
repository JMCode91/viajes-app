import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { GoogleMapsService } from './core/services/google-maps.service';
import { ThemeService } from './core/services/theme.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  protected readonly title = signal('frontend');

  private readonly googleMapsService = inject(GoogleMapsService);
  private readonly themeService = inject(ThemeService);

  ngOnInit(): void {
    void this.googleMapsService.cargar();
  }
}
