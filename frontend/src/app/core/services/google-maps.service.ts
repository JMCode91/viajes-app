import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

type WindowWithGoogleMaps = Window & {
  google?: unknown;
  __viajesGoogleMapsCallback?: () => void;
};

/**
 * Carga dinámicamente el script de la Google Maps JavaScript API usando la
 * API Key del environment. Se incluye la librería `places` (Autocomplete).
 * La promesa se cachea para no inyectar el script más de una vez.
 */
@Injectable({ providedIn: 'root' })
export class GoogleMapsService {
  private promesa: Promise<void> | null = null;

  cargar(): Promise<void> {
    const w = window as WindowWithGoogleMaps;

    if (w.google) {
      return Promise.resolve();
    }
    if (this.promesa) {
      return this.promesa;
    }

    this.promesa = new Promise<void>((resolve, reject) => {
      w.__viajesGoogleMapsCallback = () => resolve();

      const script = document.createElement('script');
      script.src =
        `https://maps.googleapis.com/maps/api/js?key=${environment.googleMapsApiKey}` +
        `&libraries=places&callback=__viajesGoogleMapsCallback`;
      script.async = true;
      script.defer = true;
      script.onerror = () => {
        delete w.__viajesGoogleMapsCallback;
        this.promesa = null;
        reject(new Error('No se pudo cargar Google Maps JavaScript API'));
      };
      document.head.appendChild(script);
    });

    return this.promesa;
  }
}
