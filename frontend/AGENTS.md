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
- **Hito actual:** FASE 1 + B1-P2 + B2-P1 + B2-P2 + B3-P1 (Dashboard) + B3-P2 (Crear Viaje + Invitaciones) + B4-P1 (Detalle Viaje) + B4-P2 (Itinerario + Mini-Calendario) + B5-P1 (Finanzas) + Dark Mode + Proxy + HOTFIX Tailwind v3 COMPLETADOS.

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

## 🧩 Bloque 1, Parte 2: El Cascarón Mobile (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **MainLayoutComponent:** Standalone en `src/app/features/layout/main-layout/`. Plantilla maestra para usuarios logueados. Imports: `RouterOutlet`, `RouterLink`, `RouterLinkActive`.
- [x] **Diseño Mobile-First (Tailwind v3):** Contenedor `min-h-screen pb-20` (deja espacio para la barra inferior). `<router-outlet />` en el centro para el contenido dinámico.
- [x] **Bottom Navigation Bar:** `<nav class="fixed bottom-0 w-full">` con `grid grid-cols-4`. 4 botones táctiles (mín. `min-h-14` = 56px, cumplen zona +44px) con icono SVG inline estilo Heroicons + micro-título bajo el icono. `routerLinkActive` resalta la pestaña activa (`text-blue-600`):
  - Viajes → `/dashboard`
  - Finanzas → `/gastos`
  - Diccionario → `/diccionario`
  - Perfil → `/perfil`
- [x] **Rutas Base:** En `app.routes.ts`, ruta vacía renderizando `MainLayout` con `children`. Ruta en blanco probando enrutamiento con `loadComponent` lazy hacia un Dashboard placeholder.
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas (Bloque 1, Parte 2)
- Los 4 destinos de la barra navegación (`/dashboard`, `/gastos`, `/diccionario`, `/perfil`) son rutas futuras.
- `Dashboard` era placeholder temporal; en el dashboard real se listarán los viajes (`GET /api/viajes`).

---

## 🔐 Bloque 2, Parte 1: Motor de Seguridad (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **AuthService** (`src/app/core/services/auth.service.ts`, `providedIn: 'root'`):
  - `inject(HttpClient)` para peticiones.
  - Signal privado `_isAuthenticated` inicializado desde `localStorage` (clave `auth_token`) y expuesto como `computed`.
  - `login(credentials)` → `POST /api/auth/login`; `register(userData)` → `POST /api/auth/register`. Ambos con `tap()` persistiendo el token y actualizando el signal.
  - `logout()` borra el token. `getToken()` recupera el token.
- [x] **auth.interceptor.ts:** `HttpInterceptorFn` que inyecta `Authorization: Bearer <token>` SOLO si la URL empieza por `/api/`.
- [x] **auth.guard.ts:** `canActivateFn` que redirige a `/login` si no hay sesión (devuelve `UrlTree`).
- [x] Configuración: `provideHttpClient(withInterceptors([authInterceptor]))`.
- [x] **Verificación:** `ng build` compila sin errores.

---

## 🎨 Bloque 2, Parte 2: Login y Registro (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **LoginComponent** (`src/app/features/auth/login/`, Standalone, `ReactiveFormsModule`): `email` + `password`, submit válido → `login()` → `/dashboard`.
- [x] **Login UI Premium:** vista centrada `min-h-screen`, card `rounded-3xl shadow-xl sharp`, inputs `h-14 rounded-2xl`, soporte `dark:`, errores con `@if`.
- [x] **Acceso rápido de desarrollo:** chip que `patchValue` los datos demo (`demo@viajes.dev` / `demo123`).
- [x] **RegistroComponent**: `nombre` + `email` + `password` (nombre máx 150, email válido, password mín 6). Registro OK → `/login`.
- [x] **Rutas:** `/login` y `/registro` fuera de `MainLayout`, pantalla completa sin Bottom Nav; el resto protegido por `authGuard`. Todos `loadComponent` (lazy).
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas (Bloque 2)
- `RegisterRequestDTO` del backend usa `nombre` (no `username`).
- Errores HTTP 4xx del login/register sin manejo visual aún → previsto para Bloque 3.

---

## 🗂️ Bloque 3, Parte 1: Dashboard de Viajes (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **ViajeService** (`src/app/core/services/viaje.service.ts`): `getMisViajes()` → `GET /api/viajes`; signal `viajes` + `cargarMisViajes()`.
- [x] **DashboardComponent** reescrito: lista premium de cards (`rounded-3xl bg-white p-5 shadow-lg`), muestran `titulo`, fechas (`pipe date`) y `presupuestoBase` (`currency: 'EUR'`).
  - **Empty State** con `@empty` (ilustración + texto + botón).
  - **FAB** fijo.
- [x] **Rutas/Navegación:** `path: 'dashboard'` con `loadComponent`. Login exitoso navega a `/dashboard`.
- [x] **Verificación:** `ng build` compila sin errores.

---

## ➕ Bloque 3, Parte 2: Crear Viaje e Invitaciones (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **ViajeService:** `crearViaje(viaje)` → `POST /api/viajes`; `unirseViaje(uuid)` → `PUT /api/viajes/{uuid}/responder-invitacion?aceptar=true`.
- [x] **ViajeRequest** en `shared/types`: espejo de `ViajeRequestDTO` (`titulo`, `fechaInicio`, `fechaFin`, `presupuestoBase`).
- [x] **CrearViajeComponent** (`features/viajes/crear-viaje/`): pantalla completa fuera del layout, botón "Atrás", form con `titulo` + fechas `type="date"` + `presupuestoBase`.
- [x] **Invitaciones:** modal en Dashboard "Unirse a un viaje con código" (input UUID + botón Unirse), FAB navegando a `/crear-viaje`.
- [x] **Rutas:** `/crear-viaje` protegida, fuera del MainLayout (lazy).
- [x] **Verificación:** `ng build` compila sin errores.

---

## 🌐 Infraestructura: Proxy local + Dark Mode + Regla de Versiones (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **Regla Control de Versiones** en `AGENTS_TEMPLATE.md`: `git add .` + commit tras cada fase. **Commit aplicado** al inicio de B4.
- [x] **Proxy:** `frontend/proxy.conf.json` reenvía `/api/*` → `http://localhost:8080`; en `angular.json` (`serve.options.proxyConfig`). Evita CORS; Services con rutas relativas `/api/**`.
- [x] **Dark Mode:** `tailwind.config.js` con `darkMode: 'class'`; **ThemeService** (signal `theme`, persistencia `localStorage`, `effect()` añade `dark` en `<html>`); botón circular Sol/Luna en `MainLayout`.
- [x] **Verificación:** `ng build` compila sin errores.

---

## 🧭 Bloque 4, Parte 1: Detalle del Viaje (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **ViajeService:** `getViajeById(id)` → filtra `GET /api/viajes`; `viajeDetalle = signal<Viaje|null>` + `cargarViaje(id)`.
- [x] **ViajeDetalleComponent** (`features/viajes/detalle-viaje/`): lee `id` de URL, cabecera "Atrás" + título, **tabs pills** horizontales con `@switch` (Itinerario/Mapa/Checklist).
- [x] **Rutas:** `/viajes/:id` protegida, FUERA del MainLayout (pantalla completa). Dashboard cards clicables.
- [x] **Verificación:** `ng build` compila sin errores.

---

## 📅 Bloque 4, Parte 2: Itinerario + Mini-Calendario (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **ActividadService:** `getActividadesPorViaje` → `GET /api/viajes/{viajeId}/actividades`; `crearActividad` → `POST`; signal `actividades`.
- [x] **Mini-calendario** horizontal (chips `Día N` + fecha, scroll `overflow-x-auto snap-x` + `scrollbar-hide`), signal `diaSeleccionado` (default primer día).
- [x] **Timeline vertical:** nodos circulares con hora + tarjetas (`rounded-2xl`) por día; `@empty` amigable.
- [x] **Modal "Añadir plan":** Título, Hora (`type="time"`), Ubicación → `descripcion`; `tipoActividad: 'OTRO'`.
- [x] **Verificación:** `ng build` compila sin errores.

---

## 💰 Bloque 5, Parte 1: Módulo de Finanzas (Gastos) (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **GastoService** (`src/app/core/services/gasto.service.ts`):
  - `getGastosPorViaje(viajeId)` → `GET /api/viajes/{viajeId}/gastos`. **Nota Backend-First:** no existe `/api/gastos/viaje/...`; el controlador real cuelga de `/api/viajes/`. Listado ordenado por cantidad DESC.
  - `crearGasto(viajeId, gasto)` → `POST /api/viajes/{viajeId}/gastos` (201 Created); body = `GastoRequestDTO`.
  - `gastos = signal<Gasto[]>([])` + `cargarGastos(viajeId)`.
  - **Pagador → Avatar:** el backend solo persiste `pagadorId` (UUID). `resolverPagador(nombre)` genera UUID determinista por nombre y lo guarda en `localStorage` (clave `finanzas_pagadores`) para renderizar las iniciales del avatar tras recargar. `nombrePagador(id)` lo recupera (fallback "Invitado").
- [x] **GastoRequest** (`src/app/shared/types/index.ts`): espejo de `GastoRequestDTO` (`concepto`, `cantidad`, `pagadorId`).
- [x] **FinanzasComponent** (`src/app/features/finanzas/`, Standalone, dentro del `MainLayout`):
  - Inyecta `ViajeService` (signal `viajes`) + `GastoService`. Un `effect()` selecciona el primer viaje por defecto y carga sus gastos.
  - `<select>` premium (`h-14 appearance-none rounded-2xl`) para elegir viaje; onchange → `cargarGastos()`.
  - **Tarjeta Resumen:** `rounded-3xl bg-gradient-to-br from-blue-600 to-indigo-700 shadow-xl`, texto "Total Gastado" + `totalGastado` (computed, suma de `cantidad`) con `currency: 'EUR'` y contador.
  - **Lista:** `@for` → avatar circular (inicial del pagador), `concepto` + nombre del pagador, monto `currency`. `@empty` con ilustración "Aún no hay gastos registrados".
  - **FAB** `fixed bottom-24 right-6 z-20 h-16 w-16 rounded-full` (no tapa la Bottom Nav) → Modal `fixed inset-0 z-50 backdrop-blur-sm` bottom-sheet con `formGasto`: `concepto` (máx 200), `cantidad` (num ≥ 0), `pagadoPor` (→ `resolverPagador`).
  - Al guardar → `crearGasto()` → cierra modal + `cargarGastos()`. Error → mensaje inline.
- [x] **Rutas** (`app.routes.ts`): `path: 'gastos'` dentro de los `children` de `MainLayout` (Bottom bar visible) con `loadComponent` lazy → `Finanzas`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` generado).

### 📝 Notas técnicas (Bloque 5, Parte 1)
- **Backend-First:** el DTO de gastos no tiene campo `fecha` ni nombre del pagador (solo `pagadorId` UUID); el formulario no incluye `type="date"` ni se muestra fecha en la tarjeta. Soportarla exigiría evolución del backend.
- El avatar/nombre del pagador se reconstruye en cliente con el mapa `finanzas_pagadores` del `localStorage` (UUID determinista por nombre normalizado); gastos creados fuera de este frontend se muestran como "Invitado".
- El FAB `bottom-24` queda 12px por encima de la BottomNav (mismo criterio que Dashboard); el modal usa `z-50` para quedar por encima.