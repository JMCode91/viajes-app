# AGENTS.md - Estado del Proyecto (Viajes App)

## 📱 Stack Tecnológico
- **Framework:** Angular 17+ (Standalone Components)
- **Estilos:** Tailwind CSS v3 (Stable)
- **Lenguaje:** TypeScript (Strict)
- **Enfoque UI:** PWA Mobile-First (Navegación Inferior, Diseño Táctil)

### 🏗️ Estructura de Carpetas Propuesta (Objetivo)
```text
src/app/
├── core/
│   ├── interceptors/
│   │   └── auth.interceptor.ts    # Inyector de JWT
│   ├── guards/
│   │   └── auth.guard.ts          # Protección funcional
│   └── services/
│       ├── auth.service.ts        # Login/Registro
│       ├── viaje.service.ts       # CRUD Viajes, Invitaciones
│       ├── actividad.service.ts   # Itinerario geolocalizado
│       ├── gasto.service.ts       # Finanzas y saldos
│       └── diccionario.service.ts # Traducción DeepL
├── shared/
│   ├── ui/                        # Componentes UI reutilizables (Botones, Cards)
│   └── types/
│       └── index.ts               # Interfaces (ViajeDTO, AuthResponse, etc.)
├── features/
│   ├── auth/                      # Login, Registro
│   ├── dashboard/                 # Listado de Viajes del Usuario
│   ├── viaje-detalle/             # Contenedor principal de un viaje
│   ├── itinerario/                # Timeline de Actividades y Calendario
│   ├── finanzas/                  # Gastos, Gráficos y Saldos
│   ├── utilidades/                # Checklists
│   ├── diccionario/               # Traductor y Frases guardadas
│   └── layout/                    # Bottom Navigation Bar y Topbar
├── app.config.ts
├── app.routes.ts
└── styles.css

---

## 📊 Estado Actual del Proyecto

*Esta sección será actualizada automáticamente por el agente OpenCode al finalizar cada sesión.*

- **Última actualización:** 08/08/2026
- **Hito actual:** FASE 1 + B1-P2 + B2-P1 + B2-P2 + B3-P1 (Dashboard) + B3-P2 (Crear Viaje + Invitaciones) + B4-P1 (Detalle Viaje) + B4-P2 (Itinerario + Mini-Calendario) + Dark Mode + Proxy + HOTFIX Tailwind v3 COMPLETADOS.

### ⚠️ HOTFIX DE EMERGENCIA: Tailwind v4 → v3 (COMPLETADO)
- **Problema:** El compilador de Angular ignoraba Tailwind al prohibir `tailwind.config.js` (necesario en la config CSS-first).
- **Solución aplicada:**
  - [x] Eliminado `postcss.config.js`.
  - [x] `npm uninstall tailwindcss @tailwindcss/postcss postcss` → `npm install -D tailwindcss@3 postcss autoprefixer`.
  - [x] Inicializado `tailwind.config.js` con `content: ["./src/**/*.{html,ts}"]`.
  - [x] `styles.css` con directivas clásicas v3: `@tailwind base; @tailwind components; @tailwind utilities;` (manteniendo el reset `bg-gray-100 text-gray-900` y `-webkit-tap-highlight-color`).
  - [x] `AGENTS_TEMPLATE.md`/`AGENTS.md` actualizados: **Tailwind CSS v3 (Stable)**, sin restricción sobre `tailwind.config.js`.
- [x] **Verificación:** `npx tailwindcss` genera utilidades reales (`min-h-screen`, `rounded-3xl`, `bg-blue-600`, etc.) y `ng build` compila sin errores (16.96 kB de CSS purged).

### ✅ Completado en esta sesión (Fase 1: Inicialización del Frontend)
- [x] **Inicialización Angular:** Proyecto Angular (Standalone Components, Routing, CSS) creado directamente en `frontend/`. Versión instalada: Angular CLI `^21.2` (≥17). Git no inicializado en `frontend/` (el repo vive en la raíz).
- [x] **Tailwind CSS v3 (Hotfix):** Versión estabilizada a `v3` con `tailwind.config.js` (content `./src/**/*.{html,ts}`) y directivas clásicas v3 en `styles.css`. Ver sección de Hotfix.
- [x] **Mapeo de DTOs (Backend-First):** Creado `src/app/shared/types/index.ts` con interfaces TypeScript que reflejan literalmente los DTOs de Spring Boot (`AuthResponseDTO`, `ViajeResponseDTO`, `ActividadResponseDTO`, `GastoResponseDTO`, `DiccionarioResponseDTO`) y entidades (`Usuario`), más `LoginRequest`/`RegisterRequest` y los enums `Rol` y `TipoActividad`.
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas
- Fechas `LocalDate`/`LocalDateTime` del backend se modelan como `string` ISO-8601 en el frontend; `BigDecimal` y coordenadas como `number`; `UUID` como `string` (alias `UUID`).
- Rol mapeado como `RolEnViaje` en `ViajeResponseDTO` → campo `rolEnViaje: string` en la interfaz `Viaje`.
- Los DTOs de archivos (`ArchivoResponseDTO`), checklist (`ItemChecklistResponseDTO`) y traducción (`TraduccionResponseDTO`) aún NO se han mapeado; se añadirán junto a sus Services en fases posteriores.

---

### ✅ Completado en esta sesión (Bloque 1, Parte 2: El Cascarón Mobile)
- [x] **MainLayoutComponent:** Standalone en `src/app/features/layout/main-layout/`. Plantilla maestra para usuarios logueados. Imports: `RouterOutlet`, `RouterLink`, `RouterLinkActive`.
- [x] **Diseño Mobile-First (Tailwind v3):** Contenedor `min-h-screen pb-20` (deja hueco para la barra inferior). `<router-outlet />` en el centro para el contenido dinámico.
- [x] **Bottom Navigation Bar:** `<nav class="fixed bottom-0 w-full">` con `grid grid-cols-4`. 4 botones táctiles (mín. `min-h-14` = 56px, cumple zona +44px) con icono SVG inline estilo Heroicons + micro-título bajo el icono. `routerLinkActive` resalta la pestaña activa (`text-blue-600`):
  - Viajes → `/dashboard`
  - Finanzas → `/gastos`
  - Diccionario → `/diccionario`
  - Perfil → `/perfil`
- [x] **Rutas Base:** En `app.routes.ts`, ruta vacía renderizando `MainLayout` con `children`. Ruta en blanco probando enrutamiento con `loadComponent` lazy hacia un `Dashboard` placeholder en `src/app/features/dashboard/dashboard/`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `dashboard` generado correctamente).

### 📝 Notas técnicas (Bloque 1, Parte 2)
- El binding `routerLinkActiveOptions="..."` (entre comillas, sin corchetes) lanza TS2322; se omitió porque `{ exact: false }` es el valor por defecto de Angular.
- Los 4 destinos de la bartra Navegación (`/dashboard`, `/gastos`, `/diccionario`, `/perfil`) son rutas futuras; su render real llegará con los features correspondientes.
- `Dashboard` es placeholder temporal; en el dashboard real se listarán los viajes (`GET /api/viajes` según `backend/AGENTS.md`).

---

## 🔐 Bloque 2, Parte 1: Motor de Seguridad (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **AuthService** (`src/app/core/services/auth.service.ts`, `providedIn: 'root'`):
  - `inject(HttpClient)` para peticiones.
  - Signal privado `_isAuthenticated` inicializado desde `localStorage` (clave `auth_token`) y expuesto como `computed` de solo lectura (`isAuthenticated`).
  - `login(credentials: LoginRequest)` → `POST /api/auth/login` (200 OK con token JWT).
  - `register(userData: RegisterRequest)` → `POST /api/auth/register` (201 Created con token JWT).
  - Ambos usan `tap()` para persistir el token en `localStorage` y actualizar el signal.
  - `logout()` borra el token y resetea el signal; `getToken()` recupera el token guardado.
- [x] **auth.interceptor.ts** (`src/app/core/interceptors/auth.interceptor.ts`): `HttpInterceptorFn` que clona la request e inyecta `Authorization: Bearer <token>` SOLO si la URL empieza por `/api/` y existe token.
- [x] **auth.guard.ts** (`src/app/core/guards/auth.guard.ts`): `canActivateFn` que inyecta `AuthService` + `Router`; si `isAuthenticated()` → `true`; si no, redirige a `/login` devolviendo `UrlTree`.
- [x] **Configuración global** (`src/app/app.config.ts`): añadido `provideHttpClient(withInterceptors([authInterceptor]))`.
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas (Bloque 2, Parte 1)
- Endpoints de autenticación extraídos literalmente de `backend/AGENTS.md` (POST `/api/auth/login`, POST `/api/auth/register`).
- El guard protege rutas devolviendo `router.createUrlTree(['/login'])` (redirección reactiva) en lugar de un `boolean` literal.

---

## 🎨 Bloque 2, Parte 2: Login y Registro (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **LoginComponent** (`src/app/features/auth/login/`, Standalone, `ReactiveFormsModule`): formulario `email` + `password` (requeridos, email válido). Injeta `AuthService` + `Router`; al submit válido llama a `login()` y redirige a `/dashboard`.
- [x] **Login UI Premium:** Vista centrada con `min-h-screen`, degradado sutil `bg-gradient-to-br from-blue-50 via-white to-gray-100`. Card `rounded-3xl shadow-xl shadow-gray-200/50`. Inputs `h-14 rounded-2xl` táctiles. Soporte `dark:` mode. Errores con `@if` (no directivas antiguas) bajo cada input sucio e inválido.
- [x] **Acceso rápido de desarrollo:** Sección tras `border-t` con chip/píldora redondeada que hace `patchValue` rellenando `demo@viajes.dev` / `demo123` (credenciales genéricas a adaptar).
- [x] **RegistroComponent** (`src/app/features/auth/registro/`): formulario `nombre` + `email` + `password` (nombre máx 150, email válido, password mín 6). Registro exitoso → redirige a `/login`.
- [x] **Rutas:** `/login` y `/registro` (fuera de `MainLayout`, pantalla completa sin Bottom Nav). `authGuard` añadido a la ruta raíz que envuelve `MainLayout`. Todos usan `loadComponent` (lazy).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunks `login`/`registro` generados).

### 📝 Notas técnicas (Bloque 2, Parte 2)
- **Backend-First:** `RegisterRequestDTO` del backend usa `nombre` (no `username`); la interfaz `RegisterRequest` y el formulario reflejan dicho campo.
- Flujo de errores HTTP del `login()`/`register()` (4xx) está sin manejo visual aún → se añadirá un manejo de errores global/UX en el Bloque 3.

---

## 🗂️ Bloque 3, Parte 1: Dashboard de Viajes (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **Limpieza del Root:** `src/app/app.html` (template raíz que genera el CLI Angular 21 en lugar de `app.component.html`) limpio: única y exclusivamente `<router-outlet></router-outlet>`.
- [x] **ViajeService** (`src/app/core/services/viaje.service.ts`, `providedIn: 'root'`):
  - `getMisViajes(): Observable<Viaje[]>` → `GET /api/viajes` (listado de viajes ACEPTADOS del usuario autenticado, según `backend/AGENTS.md`).
  - `viajes = signal<Viaje[]>([])` expuesto público; `cargarMisViajes()` hace la petición y actualiza el signal con `viajes.set(viajes)`.
- [x] **DashboardComponent** (`src/app/features/dashboard/`) reescrito sobre placeholder:
  - `ngOnInit` → `viajeService.cargarMisViajes()`. Signal expuesto como `protected readonly viajes = this.viajeService.viajes`.
  - Lista premium: `@for ... track viaje.id` sobre `viajes()`. Cards `rounded-3xl bg-white dark:bg-gray-800 p-5 shadow-lg shadow-gray-200/50`. Muestra `titulo`, fechas formateadas con pipe `date` y `presupuestoBase` con `currency: 'EUR'`.
  - **Empty State** con `@empty` (pegado al `@for`): ilustración SVG + texto invitando a crear el primer viaje + botón secundario.
  - **FAB:** `fixed bottom-24 right-6` (12px por encima de la navegación inferior), `h-16 w-16 rounded-full`, `bg-blue-600`, icono SVG "+" grande.
- [x] **Rutas/Navegación:** en `app.routes.ts`, `path: ''` de los children → `redirectTo: 'dashboard'` y nueva ruta `path: 'dashboard'` con `loadComponent`. Login exitoso ya navega a `/dashboard` (bloque previo), que ahora existe.
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas (Bloque 3, Parte 1)
- **Backend-First:** el `ViajeResponseDTO` real expone `titulo`, `fechaInicio`, `fechaFin`, `presupuestoBase` y `rolEnViaje` (NO `nombre`/`destino` como se sugirió en el enunciado). El UI se mapeó sobre los campos reales del backend.
- `@empty` debe ser bloque hermano inmediato de `@for` (no se puede anidar dentro de `@if`), por eso la lista no usa contenedor condicional extra.
- FAB y botones del empty state aún sin acción (requieren modal/formulario de creación de viaje en B3-P2); FAB sin routerLink activo aún.

---

## ➕ Bloque 3, Parte 2: Crear Viaje e Invitaciones (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **ViajeService** (`src/app/core/services/viaje.service.ts`):
  - `crearViaje(viaje: Partial<ViajeRequest>)` → `POST /api/viajes` (201 Created, creador queda como PROPIETARIO).
  - `unirseViaje(uuid: string)` → `PUT /api/viajes/{uuid}/responder-invitacion?aceptar=true` (200 OK, acepta la invitación pendiente y vincula al usuario al viaje).
  - Ambos endpoints extraídos literalmente de `backend/AGENTS.md`. Tras el éxito, quien llame al servicio debe usar `cargarMisViajes()` (el signal `viajes` solo se refresca con dicha llamada).
- [x] **ViajeRequest** (`src/app/shared/types/index.ts`): nueva interfaz espejo de `ViajeRequestDTO` (`titulo`, `fechaInicio`, `fechaFin`, `presupuestoBase`).
- [x] **CrearViajeComponent** (`src/app/features/viajes/crear-viaje/`, Standalone, `ReactiveFormsModule`):
  - Pantalla completa fuera del `MainLayout` (`min-h-screen` + degradado premium), botón "Atrás" (flecha SVG) arriba a la izquierda navegando a `/dashboard`.
  - Formulario con `titulo` (requerido, máx 200), `fechaInicio`/`fechaFin` (inputs `type="date"`) y `presupuestoBase` (núm. ≥ 0, requerido por el backend). Inputs `h-14 rounded-2xl`, card `rounded-3xl`, errores con `@if`. Submit válido → `crearViaje()` → navega a `/dashboard`.
  - **Nota Backend-First:** el DTO real de creación (`ViajeRequestDTO`) NO tiene campo `destino` ni `nombre`; solo `titulo` + fechas + presupuesto. Por eso el formulario usa `titulo` y no `destino` (se respetó el contrato real del backend).
- [x] **Sistema de Invitaciones (DashboardComponent):**
  - Empty State: botón secundario "Unirse a un viaje con código" (icono candado) + botón primario "Crear mi primer viaje" que ahora navega con `routerLink="/crear-viaje"`.
  - Modal Tailwind `fixed inset-0 z-50 bg-black/50 backdrop-blur-sm` con card inferior (bottom-sheet en móvil, centrada en `sm:`). Input monoespaciado para el UUID + botón "Unirse". Llama a `unirseViaje(uuid)`; éxito → cierra el modal (el servicio recarga el signal vía patrón de recarga). Error → mensaje inline bajo el input.
  - Form del modal con `ReactiveFormsModule` (validación UUID con `Validators.pattern`).
  - **FAB** ahora navega a `/crear-viaje` con `routerLink`.
- [x] **Rutas** (`app.routes.ts`): nueva ruta `/crear-viaje` con `canActivate: [authGuard]` y `loadComponent` (lazy), renderizada FUERA del `MainLayoutComponent` para simular una pantalla completa nueva (como `/login` y `/registro`).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `crear-viaje` generado correctamente).

### 📝 Notas técnicas (Bloque 3, Parte 2)
- **Backend-First:** `unirseViaje(uuid)` se mapeó a `PUT /api/viajes/{uuid}/responder-invitacion?aceptar=true`. Requisito: el usuario debe tener una invitación pendiente (creada por el propietario con `POST /api/viajes/{id}/invitar`); si no, el backend lanza 403 (el frontend muestra "La invitación no es válida o ya ha sido respondida").
- El `crearViaje()`/`unirseViaje()` devuelven el `Observable` directamente; el componente de creación y el modal del Dashboard deciden la navegación/cierre tras `next`, y ambos dependen de `cargarMisViajes()` para refrescar el signal `viajes`.
- Modal sin "X de a11y": se cierra con el botón X, con tap en el backdrop y con `$event.stopPropagation()` en la card para no cerrarse al tocar dentro.

---

## 🌐 Infraestructura: Proxy local + Dark Mode + Regla de Versiones (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **Regla Control de Versiones** en `AGENTS_TEMPLATE.md`: al finalizar cada tarea/fase hay que ejecutar `git add .` + `git commit -m` con mensaje descriptivo. **Commit inicial aplicado:** `feat(frontend): auth, dashboard y creacion de viajes con proxy a backend - B1-B3 completados`.
- [x] **Proxy local:** `frontend/proxy.conf.json` reenvía todo `/api/*` a `http://localhost:8080` (`secure: false`, `changeOrigin: true`). Añadido en `angular.json` → `architect.serve.options.proxyConfig`. Evita CORS en desarrollo. Los Services usan rutas relativas `/api/**` (nunca el host literal).
- [x] **Dark Mode (Tailwind v3):** `tailwind.config.js` con `darkMode: 'class'`.
- [x] **ThemeService** (`src/app/core/services/theme.service.ts`): signal `theme` ('light'/'dark') persistido en `localStorage` (clave `theme`, por defecto claro). Un `effect()` añade/quita `dark` en `<html>`. Método `toggle()`.
- [x] **Toggle en MainLayout:** botón circular `fixed right-4 top-4 z-40 h-10 w-10 rounded-full` con icono Sol/Luna (Heroicons) según `theme()`, con `@if/@else`.
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas (Infraestructura)
- El dark mode funciona con `@media` reemplazado por la clase `.dark` en `<html>`; todas las vistas ya tenían variantes `dark:`.
- `ThemeService` inyecta `effect()` en constructor para aplicar la clase en arranque y en cada cambio.

---

## 🧭 Bloque 4, Parte 1: Detalle del Viaje (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **ViajeService** (`src/app/core/services/viaje.service.ts`):
  - `getViajeById(id): Observable<Viaje | undefined>` → `GET /api/viajes` y filtra por `id` (el backend NO expone un GET de un único viaje; se reutiliza el listado real y se filtra en cliente).
  - `viajeDetalle = signal<Viaje | null>(null)` expuesto y `cargarViaje(id)` que actualiza el signal.
- [x] **ViajeDetalleComponent** (`src/app/features/viajes/detalle-viaje/`, Standalone):
  - Lee `id`/`uuid` con `ActivatedRoute.snapshot.paramMap` y llama a `cargarViaje(id)`.
  - Cabecera con botón "Atrás" (flecha SVG → `/dashboard`), título del viaje y fechas con `pipe date`.
  - **Tabs Pills** desplazables (`overflow-x-auto`) con 3 opciones: Itinerario, Mapa y Checklist (icono + label, `h-11 shrink-0 rounded-full`, activa = `bg-blue-600`).
  - Signal `tabActiva` + `@switch` en HTML mostrando textos provisionales ("Contenido del Itinerario...", etc). Tipos exportados `Tab`/`TabItem` y array `tabs` con iconos en el `.ts` (binding SVG con `[attr.d]`).
- [x] **Rutas** (`app.routes.ts`): `/viajes/:id` con `canActivate: [authGuard]` y `loadComponent` (lazy), FUERA del `MainLayoutComponent` (pantalla completa).
- [x] **DashboardComponent:** cada `<article>` de viaje es ahora clicable con `routerLink="/viajes/{{ viaje.id }}"` + `cursor-pointer` y `active:scale-[0.99]`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` generado).

### 📝 Notas técnicas (Bloque 4, Parte 1)
- **Backend-First:** no existe `GET /api/viajes/{id}`; `getViajeById` filtra el listado ACEPTADO en cliente. Si no se encuentra, `viajeDetalle` queda `null` y la cabecera muestra "Viaje".
- Los templates de Angular no pueden referenciar tipos TS; por eso `tabs` está tipado en el componente y la plantilla usa `tab.key` directamente.

---

## 📅 Bloque 4, Parte 2: Itinerario Interactivo + Mini-Calendario (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **ActividadService** (`src/app/core/services/actividad.service.ts`):
  - `getActividadesPorViaje(viajeId)` → `GET /api/viajes/{viajeId}/actividades` (listado ordenado por `fechaHoraInicio` asc; JWT + pertenencia).
  - `crearActividad(viajeId, actividad)` → `POST /api/viajes/{viajeId}/actividades` (201 Created); el body es `ActividadRequestDTO`.
  - `actividades = signal<Actividad[]>([])` y `cargarActividades(viajeId)` que lo actualiza.
- [x] **ActividadRequest** (`src/app/shared/types/index.ts`): interfaz espejo de `ActividadRequestDTO` (`titulo`, `descripcion?`, `tipoActividad`, `fechaHoraInicio`, `fechaHoraFin`, coords/url opcionales). El `Actividad` ya existía.
- [x] **Mini-Calendario (Selector de Días)** en el `@case ('itinerario')` de `ViajeDetalleComponent`:
  - `computed días` generado desde `viaje.fechaInicio..fechaFin` (objetos `Dia { valor/numero/etiqueta }`). `diaSeleccionado = signal<string|null>` y `diaActivo` computado con fallback al primer día (vía `effect` en constructor).
  - Scroll horizontal `overflow-x-auto snap-x snap-mandatory` (+ clase `.scrollbar-hide` en CSS del componente). Chips `w-20 rounded-2xl`, formato "Día N" + "21 Sep"; activo = `bg-blue-600 text-white shadow-lg`.
- [x] **Timeline Vertical de Actividades:**
  - Contenedor `relative ml-6` con línea `absolute left-[6px] w-0.5 bg-gray-200`; cada actividad: nodo circular `h-3.5 w-3.5 rounded-full border-2 border-blue-600` superpuesto a la línea + tarjeta `rounded-2xl` con hora (`date: 'HH:mm'`), título y ubicación/descripción si la tiene.
  - Filtro por día: `computed 'actividadesDelDia'` compara `fechaHoraInicio.startsWith(dia)`.
  - `@empty` muestra ilustración + "Este día aún no tiene planes".
- [x] **Creación de Actividades (Modal Rápido):**
  - Botón "Añadir plan" (full-width al final de la lista del día) → Modal `fixed inset-0 z-50 backdrop-blur-sm` con formato: Título, Hora (`type="time"`) y Ubicación (opcional).
  - `ReactiveFormsModule`; al guardar construye `fechaHoraInicio = diaActivo + 'T' + hora`, `tipoActividad: 'OTRO'` (el `@NotNull` del backend lo exige) y mapea Ubicación a `descripcion` (campo libre del backend; no existe campo "ubicación" en el DTO). Éxito → cierra modal + `cargarActividades()`.
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas (Bloque 4, Parte 2)
- **Backend-First:** `ActividadRequestDTO` exige `tipoActividad` (`@NotNull`, se manda `OTRO`) y `fechaHoraInicio`/`fechaHoraFin`. El text "Ubicación" del formulario se guarda en `descripcion` (el backend NO tiene campo `ubicacion`: tiene `descripcion`, `latitud`, `longitud`).
- `diaSeleccionado` se inicializa con `effect()` una vez que el viaje carga (el default es el primer día del viaje). Los demás textos ("Contenido del mapa...") siguen provisionales hasta B4-P3.