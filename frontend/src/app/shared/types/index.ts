export type UUID = string;

export type Rol = 'USER' | 'ADMIN';

export type TipoActividad =
  | 'VUELO'
  | 'TREN'
  | 'HOTEL'
  | 'EXCURSION'
  | 'RESTAURANTE'
  | 'OTRO';

export interface Usuario {
  id: UUID;
  email: string;
  password: string;
  nombre: string;
  rol: Rol;
}

export interface UsuarioResponse {
  id: UUID;
  email: string;
  nombre: string;
  rol: Rol;
}

export interface Viaje {
  id: UUID;
  titulo: string;
  fechaInicio: string;
  fechaFin: string;
  presupuestoBase: number;
  rolEnViaje: string;
  fotoPortada: string | null;
  participantes: Participante[];
}

export interface ViajeUpdateRequest {
  titulo: string;
  fechaInicio: string;
  fechaFin: string;
}

export interface Participante {
  id: UUID;
  nombre: string;
  email: string;
  rolEnViaje: string;
}

export interface ViajeRequest {
  titulo: string;
  fechaInicio: string;
  fechaFin: string;
  presupuestoBase: number;
}

export interface Documento {
  id: UUID;
  nombreOriginal: string;
  urlArchivo: string;
}

export interface Actividad {
  id: UUID;
  viajeId: UUID;
  titulo: string;
  descripcion: string;
  tipoActividad: TipoActividad;
  fechaHoraInicio: string;
  fechaHoraFin: string;
  latitud: number | null;
  longitud: number | null;
  direccion: string | null;
  notas: string | null;
  documentos: Documento[];
}

export interface ActividadRequest {
  titulo: string;
  descripcion?: string;
  tipoActividad: TipoActividad;
  fechaHoraInicio: string;
  fechaHoraFin: string;
  latitud?: number;
  longitud?: number;
  direccion?: string;
  notas?: string;
}

export interface Gasto {
  id: UUID;
  viajeId: UUID;
  concepto: string;
  cantidad: number;
  pagadorId: UUID;
}

export interface GastoRequest {
  concepto: string;
  cantidad: number;
  pagadorId: UUID;
}

export interface Album {
  id: UUID;
  viajeId: UUID;
  nombre: string;
  fechaCreacion: string;
}

export interface AlbumRequest {
  nombre: string;
}

export interface Foto {
  id: UUID;
  viajeId: UUID;
  albumId: UUID | null;
  url: string;
  fechaSubida: string;
  usuarioId: UUID;
  usuarioNombre: string;
}

export interface TareaChecklistRequest {
  contenido: string;
  esPrivada: boolean;
  asignadoId?: UUID | null;
}

export interface TareaChecklistResponse {
  id: number;
  contenido: string;
  completada: boolean;
  esPrivada: boolean;
  nombreCreador: string;
  nombreAsignado: string | null;
}

export interface Diccionario {
  id: UUID;
  fraseEspanol: string;
  fraseJapones: string;
  pronunciacion: string;
  categoria: string;
  idioma: string;
}

export interface AuthResponse {
  token: string;
  email: string;
  nombre: string;
  avatarUrl?: string | null;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
}