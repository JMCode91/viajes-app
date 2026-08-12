import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CategoriaDiccionario {
  id: number;
  nombre: string;
  icono: string;
}

export interface FraseDiccionario {
  id: number;
  textoEspanol: string;
  textoIngles: string;
  textoJapones: string;
  textoJaponesRomaji: string;
}

@Injectable({ providedIn: 'root' })
export class DiccionarioService {
  private readonly http = inject(HttpClient);

  getCategorias(): Observable<CategoriaDiccionario[]> {
    return this.http.get<CategoriaDiccionario[]>('/api/diccionario/categorias');
  }

  getFrasesPorCategoria(categoriaId: number): Observable<FraseDiccionario[]> {
    return this.http.get<FraseDiccionario[]>(
      `/api/diccionario/frases/${categoriaId}`
    );
  }
}