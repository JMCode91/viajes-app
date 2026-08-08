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
- **Hito actual:** FASE 1 + B1-P2 + B2-P1 + B2-P2 + B3-P1 (Dashboard) + B3-P2 (Crear Viaje + Invitaciones) + B4-P1 (Detalle Viaje) + B4-P2 (Itinerario + Mini-Calendario) + B5-P1 (Finanzas) + Dark Mode + Proxy + HOTFIX Tailwind v3 + BUGFIX Y PULIDO + ITERACIÓN UI/UX + HOTFIX ddl-auto + Interceptor 401/403 + Módulo Perfil + USABILIDAD UI/UX + **INTEGRACIÓN CHECKLIST CON BACKEND (TareaChecklistService + refactor sin localStorage)** COMPLETADOS.

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

---

## 🎨 Refactor UI: Selector de Día, Documentos y Checklist Dual (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **Modal de Actividades — Selector de día explícito:** se sustituyó el texto hardcodeado del modal por un `<select>` (`h-14 appearance-none rounded-2xl`) que itera sobre `dias()` para poblar las `<option>` "Día N — etiqueta". Nuevo control `dia` en `formActividad` (requerido); al abrir el modal se `patchValue` con `diaActivo()`; al guardar, `fechaHoraInicio = ${dia}T${hora}` (ya no depende del día activo del mini-calendario). Subtítulo del modal ahora genérico.
- [x] **Modal de actividad — Adjuntar documento:** `input type="file"` oculto (`#archivoInput`, `accept="application/pdf,image/*"`) disparado por botón estilizado con borde punteado e icono de clip: "Adjuntar reserva (PDF/Imagen)". Estado `archivo = signal<{nombre,url}>(null)` + `subiendoArchivo`; al seleccionar se muestra chip con icono de documento + nombre y botón X para quitar. `ActividadRequest.urlDocumento` se rellena con la URL devuelta. El campo se limpia al abrir/cerrar el modal.
- [x] **ActividadService.subirDocumento(file):** función mockeada que devuelve `Observable<string>` con una URL temporal inventada (`https://r2.placeholder.viajes.app/reservas/<nombre-saneado>`) tras un `delay(600)` — arquitectura lista para Cloudflare R2 (devolverá la URL real del upload).
- [x] **Checklist dual (pestaña "Checklist"):** **Checklist dual (pestaña "Checklist"):** contenedor con **dos pestañas** (pills con fondo `bg-gray-100 dark:bg-gray-900`): **"Lista Común (Grupo)"** vs **"Mi Lista Privada"** → `listaChecklist = signal<'comun'|'privada'>` (default `comun`); `listaActiva` computed devuelve la lista correspondiente.
  - **Añadir tarea:** input (h-14) + botón `+` cuadrado; Enter también añade. `anadirTarea()` empuja a la lista activa (`nuevaTarea` se resetea).
  - **Items:** `@for` con tarjetas `rounded-xl bg-gray-50 dark:bg-gray-800 px-3.5 py-3.5`, checkbox nativo `h-6 w-6 accent-blue-600`; si está marcado → `line-through text-gray-400`. `toggleTarea(id)` muta inmutablemente la lista activa con `update`.
  - `@empty` con ilustración y texto según lista activa.
  - **Estado provisional local** (Signals), sin backend. IDs `Date.now()`.
- [x] **Verificación:** `ng build` compila sin errores.

### 📝 Notas técnicas (Refactor UI)
- El selector de día usa el mismo `dias()` del mini-calendario; `Día N — 12 sep`. Si no hay viaje cargado (`dias()` vacío), el `<select>` queda sin opciones y la validación `dia` bloquea el guardado.
- `subirDocumento` es un mock deliberado: preparado para incrustar la lógica de presign de Cloudflare R2 (subida directa a bucket y persistencia de la URL en `Actividad.urlDocumento`).
- Las listas de checklist son provisionales (Signals locales); en futuras fases se conectarán a `ItemChecklist` del backend.

---

## 🐛 Bugfix y Pulido: Fechas itinerario, gastos con usuario real, checklist asignado y UUID visible (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **Fix Selector de Días (Itinerario):** reescrito `generarDias()` en `detalle-viaje.ts` para calcular el rango de fechas de forma **timezone-safe** a partir de `fechaInicio`/`fechaFin` del `viajeDetalle`. Se parsea con `Date.UTC`, se genera el `valor` ISO-8601 local (`isoLocal()`) y la etiqueta `Día N — etiqueta` con `timeZone: 'UTC'`. Evita el desfase de un día que provocaba el `<select>` vacío al abrir el modal "Añadir plan".
- [x] **Finanzas — Presupuesto base:** la tarjeta azul ahora muestra comparativa "Gastado: X€" + "de Y€ (Presupuesto)", porcentaje usado con badge y barra de progreso (`computed`: `viajeActual`, `presupuestoBase`, `porcentajePresupuesto`).
- [x] **Fix POST Gasto (`pagadorId`):** 
  - Corregido `uuidDesdeTexto()` en `gasto.service.ts` (generaba un UUID mal formado de 38 caracteres → rechazo de PostgreSQL). Ahora devuelve un UUID canónico 8-4-4-4-12.
  - **AuthService** ahora persiste la sesión (`auth_usuario` en localStorage) y expone `usuario = computed<UsuarioSesion|null>` con `email`/`nombre`.
  - `GastoService.pagadorAutenticado(email, nombre)` resuelve el `pagadorId` real desde el email del usuario logueado.
  - En `Finanzas`, el campo libre "Pagado por" se sustituyó por un `<select>` cuya única opción es el usuario autenticado (form `pagadorId`).
- [x] **Checklist — Asignado (Lista Común):** `<select>` junto al input para asignar tarea a un participante (mock `['Yo','Lucía','Marco']`). La tarjeta de la tarea muestra un mini badge azul con el nombre del asignado. `ChecklistItem.asignado: string | null`.
- [x] **Compartir UUID (Invitaciones):** botón de "Compartir" (icono) en la cabecera de `ViajeDetalle` → modal "Añadir personas" que muestra en grande `viaje.id` (el UUID de invitación usado por `unirseViaje`) con el texto "Comparte este código con tus amigos para que se unan al viaje".
- [x] **Verificación:** `ng build` compila sin errores (lazy chunks `detalle-viaje` y `finanzas` regenerados).

### 📝 Notas técnicas (Bugfix y Pulido)
- **UUID de invitación:** el backend no expone un campo `uuidInvitacion`; el código de invitación que se comparte es el propio `id` del viaje (el que usa `PUT /api/viajes/{uuid}/responder-invitacion`). Se muestra `viajeDetalle.id` en el modal.
- El `pagadorId` de gastos sigue siendo un UUID determinista generado en cliente a partir del email real del usuario; el backend solo lo persiste como columna UUID (sin FK), por lo que el nombre se reconstruye desde el mapa `finanzas_pagadores`.
- `resolverPagador(nombre)` (libre) se eliminó en favor de `pagadorAutenticado(email, nombre)`.

---

## 🎨 Iteración UI/UX: Picker custom fecha/hora, modal detalle de actividad y botón de unirse global (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **Modal "Detalle del Plan" (Itinerario):** las tarjetas del timeline son clicables (`cursor-pointer` + `active:scale-[0.98]`). Al pulsar se abre un bottom-sheet `z-50 backdrop-blur-sm` que muestra en grande: hora (chip azul `HH:mm`), título y ubicación (`.descripcion`). Si `actividad.urlDocumento` es válido, botón destacado "Ver documento adjunto" con icono de clip que abre la URL en pestaña nueva (`window.open(url, '_blank', 'noopener')`). Botón "Cerrar" + cierre por backdrop.
- [x] **CustomTimePicker** (`src/app/shared/components/custom-time-picker/`, ControlValueAccessor standalone): botón estilizado que muestra la hora y al pulsarlo abre un bottom-sheet con un **reloj digital** (`font-mono text-5xl` sobre fondo oscuro) y dos columnas desplegables (horas 00-23 / minutos 00-55 en pasos de 5). Scroll con `scrollbar-hide` (CSS propio). Confirma con "Confirmar hora". Valor `HH:MM`.
- [x] **CustomDatePicker** (`src/app/shared/components/custom-date-picker/`, ControlValueAccessor standalone): botón que muestra la fecha formateada (`d mes año`) y al pulsarlo abre un popover de **calendario** construido con Tailwind: cabecera con navegación mes `‹ ›`, días de la semana L-X, cuadrícula `grid-cols-7`, día de hoy con anillo y día seleccionado resaltado en `bg-blue-600`. Botón "Ir a hoy". Valor `YYYY-MM-DD`.
- [x] **Integración de pickers:** en `Añadir plan` el `input type="time"` se sustituyó por `<app-custom-time-picker formControlName="hora" />`; en `Crear Viaje` los dos `input type="date"` se sustituyeron por `<app-custom-date-picker formControlName="fechaInicio" />` / `fechaFin`. Se mantienen las validaciones `hasError` en los padres.
- [x] **Botón permanente "Unirse" (Dashboard):** cabecera "Mis viajes" ahora es `flex justify-between` con botón permanente de "Unirse" (icono de candado/eslabón + texto) en la esquina superior derecha, que abre el mismo modal de UUID ya existente (`abrirModalUnirse()`). También sigue disponible en el empty state.
- [x] **Limpieza de mocks (Checklist):** eliminados los nombres fake "Lucía"/"Marco" del array `participantes`. Ahora `participantes` es un `computed` que usa `AuthService.usuario().nombre` (fallback `'Yo'`). La lista de asignados solo contiene al usuario autenticado.
- [x] **Verificación:** `ng build` compila sin errores (chunks `detalle-viaje`, `crear-viaje` y `dashboard` regenerados).

### 📝 Notas técnicas (Iteración UI/UX)
- Los pickers son `ControlValueAccessor` (`NG_VALUE_ACCESSOR` + `forwardRef`) y por tanto funcionan con `ReactiveFormsModule` y `formControlName` sin librerías externas.
- `scrollbar-hide` (ocultar scrollbar de las columnas) vive ahora también en `custom-time-picker.css`; el existente en `detalle-viaje.css` sigue usándose para el mini-calendario.
- Los formatos de valor son compatibles con el backend: `YYYY-MM-DD` para fechas (`LocalDate`) y `HH:MM` para horas (concatenado como `${dia}T${hora}`).

---

## 🔐🔒 HOTFIX Backend + Escudo 401/403 + Módulo de Perfil (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **HOTFIX Crítico (Backend):** `spring.jpa.hibernate.ddl-auto` quedó estrictamente en **`update`** en `backend/src/main/resources/application.properties` (revertido un cambio accidental de `create` que caía en el commit anterior). `update` crea/actualiza el esquema sin perder datos en cada reinicio.
- [x] **Escudo de Seguridad (Interceptor 401/403):** `auth.interceptor.ts` ahora inyecta `AuthService` y `Router`. Con `pipe(catchError(...))` captura cualquier `HttpErrorResponse` de URLs `/api/`; si el status es **401 (Unauthorized)** o **403 (Forbidden)** → llama `authService.logout()` (borra `auth_token`/`auth_usuario` y resetea los Signals `isAuthenticated`/`usuario`) y redirige inmediatamente a `/login` con `router.navigate(['/login'])`. El error se re-lanza (`throwError`) para que cada componente siga gestionando su `error` callback.
- [x] **Módulo Perfil** (`features/perfil/`, Standalone, dentro del `MainLayout`):
  - **Lógica:** inyecta `AuthService`; expone `usuario` (computed signal) y `inicial` (computed: primera letra en mayúscula del nombre, fallback `'?'`). Método `cerrarSesion()` → `logout()` + `router.navigate(['/login'])`.
  - **UI Premium:** card superior centrada con **avatar grande circular** (`h-28 w-28`, gradiente azul→índigo, inicial en `text-5xl`), nombre (`text-2xl font-bold`) y email. Debajo: si no hay sesión muestra "Invitado".
  - **Lista de opciones estilo menú de ajustes iOS/Android:** sección `rounded-3xl` con fila "Cuenta" (icono persona), "Notificaciones" (icono campana) y —al final— un botón **rojo "Cerrar Sesión"** (`text-red-600`, icono de logout, `active:bg-red-50`) que ejecuta `cerrarSesion()`. Dividers `h-px bg-gray-100 dark:bg-gray-700`.
- [x] **Ruta `/perfil`:** añadida a `app.routes.ts` dentro de los `children` de `MainLayout` (protegida por `authGuard` y con Bottom Nav visible), `loadComponent` lazy → `Perfil`. El botón "Perfil" de la Bottom Navigation Bar ya apuntaba a `/perfil` (última pestaña).
- [x] **Verificación:** `ng build` compila sin errores (nuevo lazy chunk `perfil`, 5.73 kB).

### 📝 Notas técnicas (Hotfix + Interceptor + Perfil)
- La intercepción 401/403 se aplica SOLO a requests `/api/**` (protege `diccionario`, `gasto`, etc.); APIs externas (ej. files) no se ve afectadas.
- El re-lanzamiento del error con `throwError` permite que los componentes (Finanzas, Dashboard, etc.) sigan mostrando su propio mensaje inline tras el logout.
- `ddl-auto=update` es el valor correcto para desarrollo local; en producción se recomienda `validate` con migraciones.

---

## 🧰 Usabilidad UI/UX: Cabecera sin solapamiento, copiar UUID, eliminar viaje y checklist persistente (COMPLETADO)

### ✅ Completado en esta sesión
- [x] **Fix Solapamiento (Dashboard):** la cabecera de `Dashboard` ahora es `flex justify-between items-center gap-4`. Título + subtítulo a la izquierda (`min-w-0`) y, a la derecha, un contenedor `flex shrink-0 items-center gap-2` con el **toggle de Modo Noche** (botón 11x11 redondeado con icono Sol/Luna) y el botón **"Unirse"**, perfectamente alineados y sin pisarse.
  - **Refactor del toggle de tema:** se eliminó el botón flotante `fixed right-4 top-4` del `MainLayout` (que solapaba cualquier cabecera con botones a la derecha) y ahora cada pantalla con cabecera lo muestra inline: `Dashboard`, `Finanzas` y `Perfil` (todos `ThemeService.theme` + `toggleTheme()`).
- [x] **Copiar UUID (Modal "Añadir personas"):** bajo el `<code>` del UUID hay un botón "Copiar Código" con icono de portapapeles. Usa `navigator.clipboard.writeText(uuid)`. Al pulsar, cambia a "¡Copiado!" con icono de check y `setTimeout(2000)` para volver al estado normal. El feedback es visual (texto + icono), no requiere toast.
- [x] **Eliminar Viaje con Confirmación:**
  - `ViajeService.eliminarViaje(id)` → `DELETE /api/viajes/{id}`.
  - Botón papelera rojo en la cabecera de `ViajeDetalle` (junto al de compartir) → abre **modal de confirmación** (`modalEliminarAbierto`): icono papelera en chip rojo, título "Eliminar viaje", mensaje "¿Estás seguro de que quieres eliminar este viaje? Esta acción no se puede deshacer.", botón rojo "Eliminar viaje" + botón "Cancelar".
  - Si acepta: `eliminarViaje(viajeId)` → actualiza `viajes` del servicio, limpia el localStorage del checklist de ese viaje y `router.navigate(['/dashboard'])`. Estado `eliminando` deshabilita el botón; errores se muestran inline (`errorEliminar`).
- [x] **Checklist Persistente (localStorage por viaje):**
  - Claves con el ID del viaje: `checklist_comun_viaje_{id}` y `checklist_privada_viaje_{id}`. Así los items de Japón no se mezclan con los de Madrid.
  - `cargarChecklist()` en el constructor lee ambas listas (`JSON.parse` con try/catch → `[]` si corrupto). Un `effect()` escribe ambas listas en `localStorage` cada vez que cambian (añadir/marcar), activo solo si hay `viajeId`.
- [x] **Verificación:** `ng build` compila sin errores (chunks `detalle-viaje`, `dashboard`, `finanzas`, `perfil` regenerados).

### 📝 Notas técnicas (Usabilidad UI/UX)
- **Backend sin endpoint de borrado:** el `ViajeController` NO expone aún `DELETE /api/viajes/{id}` (solo tienen `@DeleteMapping` los controladores de Actividad, Gasto y Checklist). El frontend ya lo consume; el backend debe añadir `@DeleteMapping("/{id}")` en `ViajeController` (vía `ViajeService` + repositorio) para que el flujo funcione. Actualmente `eliminarViaje` devolverá 404/405.
- El toggle de tema ya no está fijo/flotante: vive en la cabecera de cada pantalla logueada para evitar solapamientos con botones de acción.
- `navigator.clipboard` requiere contexto seguro (HTTPS o localhost); en despliegues no seguros convendría un fallback con `document.execCommand('copy')`.
- La persistencia del checklist es puramente local (sin backend); si más adelante se conecta `ItemChecklist` del backend, se migrará de localStorage a Signals + API.