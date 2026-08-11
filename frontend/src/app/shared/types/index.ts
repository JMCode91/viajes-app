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

export interface UsuarioPerfil {
  id: UUID;
  email: string;
  nombre: string;
  avatarUrl: string | null;
  notificacionesGastos: boolean;
  notificacionesPlanes: boolean;
}

export interface CambiarPasswordRequest {
  passwordActual: string;
  nuevaPassword: string;
}

export interface PreferenciasNotificacionRequest {
  notificacionesGastos: boolean;
  notificacionesPlanes: boolean;
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
  usuarioId: UUID;
  nombre: string;
  email: string;
  rolEnViaje: string;
  avatarUrl?: string;
  presupuestoIndividual: number;
}

export interface ActualizarPresupuestoIndividualRequest {
  presupuesto: number;
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
  googlePlaceId: string | null;
  rating: number | null;
  horarios: string | null;
  urlGoogleMaps: string | null;
  descripcionGoogle: string | null;
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
  googlePlaceId?: string;
  rating?: number;
  horarios?: string;
  urlGoogleMaps?: string;
  descripcionGoogle?: string;
  tipoActividad: TipoActividad;
  fechaHoraInicio: string;
  fechaHoraFin: string;
  latitud?: number;
  longitud?: number;
  direccion?: string;
  notas?: string;
}

export type TipoGasto = 'PERSONAL' | 'COMPARTIDO';

export type TipoDivision =
  | 'PARTES_IGUALES'
  | 'PORCENTAJE'
  | 'DEUDA_TERCERO';

export interface DetalleDivision {
  usuarioId: UUID;
  valorAsignado: number;
}

export interface DetalleDeuda {
  deudorId: UUID;
  deudorNombre: string;
  importe: number;
}

export interface Gasto {
  id: UUID;
  viajeId: UUID;
  concepto: string;
  importe: number;
  fecha: string;
  tipoGasto: TipoGasto;
  tipoDivision: TipoDivision | null;
  pagadorId?: UUID;
  pagadorNombre: string;
  deudas?: DetalleDeuda[];
}

export interface GastoRequest {
  concepto: string;
  importe: number;
  pagadorId: UUID;
  tipoGasto?: TipoGasto;
  tipoDivision?: TipoDivision;
  detalles?: DetalleDivision[];
}

export interface DeudaResumen {
  deudorId: UUID;
  deudorNombre: string;
  acreedorId: UUID;
  acreedorNombre: string;
  importe: number;
}

export interface BalanceResponse {
  presupuestoGlobal: number;
  totalGastadoGrupo: number;
  presupuestoIndividualUsuario: number;
  totalGastadoUsuario: number;
  deudas: DeudaResumen[];
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
  usuarioAvatarUrl?: string;
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

export interface Clima {
  tempMax: number;
  tempMin: number;
  condicion: string;
  icono: string;
  esHistorico: boolean;
}