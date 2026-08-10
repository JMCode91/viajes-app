import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

type WindowWithGoogleMaps = Window & {
  google?: unknown;
  __viajesGoogleMapsCallback?: () => void;
};

/**
 * Carga dinámicamente el script de la Google Maps JavaScript API usando la
 * API Key del environment. Se cargan las librerías `places` (Autocomplete)
 * y `marker` (AdvancedMarkerElement) y se usa `loading=async` para no
 * bloquear el arranque de la app. La promesa se cachea para no inyectar el
 * script más de una vez.
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
        `&libraries=places,marker&loading=async&callback=__viajesGoogleMapsCallback`;
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

  /**
   * Obtiene las reseñas de Google del lugar indicado usando el
   * `google.maps.places.PlacesService` (requiere un elemento HTML dummy).
   * Devuelve el array de `PlaceReview` o rechaza si el detalle falla.
   */
  async obtenerResenasGoogle(
    placeId: string
  ): Promise<google.maps.places.PlaceReview[]> {
    await this.cargar();
    const w = window as WindowWithGoogleMaps;
    const mapsApi = w.google as typeof google | undefined;
    if (!mapsApi) {
      return [];
    }

    const service = new mapsApi.maps.places.PlacesService(
      document.createElement('div')
    );

    return new Promise<google.maps.places.PlaceReview[]>((resolve, reject) => {
      service.getDetails(
        { placeId, fields: ['reviews'] },
        (resultado, estado) => {
          if (
            estado === mapsApi.maps.places.PlacesServiceStatus.OK &&
            resultado?.reviews
          ) {
            resolve(resultado.reviews);
          } else {
            reject(new Error('No se pudieron obtener las reseñas de Google.'));
          }
        }
      );
    });
  }

  /**
   * Obtiene hasta 3 fotos de Google del lugar indicado usando el
   * `google.maps.places.PlacesService` (requiere un elemento HTML dummy).
   * Devuelve las URLs de las fotos (máx 400x300) o un array vacío si el
   * detalle falla o el lugar no tiene fotos.
   */
  async obtenerFotosLugar(placeId: string): Promise<string[]> {
    await this.cargar();
    const w = window as WindowWithGoogleMaps;
    const mapsApi = w.google as typeof google | undefined;
    if (!mapsApi) {
      return [];
    }

    const service = new mapsApi.maps.places.PlacesService(
      document.createElement('div')
    );

    return new Promise<string[]>((resolve) => {
      service.getDetails(
        { placeId, fields: ['photos'] },
        (resultado, estado) => {
          if (
            estado !== mapsApi.maps.places.PlacesServiceStatus.OK ||
            !resultado?.photos
          ) {
            resolve([]);
            return;
          }
          resolve(
            resultado.photos.slice(0, 3).map((foto) =>
              foto.getUrl({ maxWidth: 400, maxHeight: 300 })
            )
          );
        }
      );
    });
  }
}
