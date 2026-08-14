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

- **Última actualización:** 14/08/2026
- **Últimos commits:** `fix(frontend): fix ghost scroll by locking main layout to 100dvh flex column with scrollable content` (`f495e63`), `fix(frontend): remove translator block from Conversor and switch Viajes icon to a real airplane` (`072219e`), `feat(frontend): restore Conversor tab and update bottom nav with airplane and translate icons` (`c3bf021`), `refactor(backend): clean up AI service (IaServiceImpl) removing duplication and large methods`, `Security & Fix: Auditoría de seguridad y enrutamiento SPA`, `update: actualizamos la API para el traductor`, `feat(fullstack): switch translator to Google API for speed and merge UI into dictionary`, `refactor(frontend): merge dynamic translator into dictionary view and remove conversor tab`, `feat(frontend): build dynamic translator UI with Web Speech API and Gemini integration`, `feat(frontend): add target language selector to dictionary phrase list`, `feat(frontend): create survival dictionary UI with categories and phrases modal`, `feat(frontend): add regenerate button and logic for individual AI activities`, `fix(fullstack): adjust AI schedule prompt and unify datepicker UI in AI form`, `feat(frontend): add dates and extra context inputs, render structured AI locations`, `feat(frontend): add save trip functionality to AI assistant`, `feat(frontend): implement AI assistant UI and parse Gemini JSON response`, `fix(frontend): handle identical currency local conversion`, `style(frontend): fix select text overflow and move full origin currency name to header`, `style(frontend): display full currency names in converter dropdowns and results`, `feat(frontend): implement dynamic multi-currency selectors and display market update date`, `feat(frontend): implement conversor UI and integrate with DivisaService`, `fix(frontend): compute group expenses total excluding individual items`, `fix(frontend): replace weather text with emojis in days selector`, `fix(frontend): integrate finanzas as a nested tab inside trip layout`, `feat(frontend): consume clima service and display weather in days selector`, `refactor(frontend): move finanzas module into trip layout and update bottom navbar`, `fix(frontend): remove negative margin in InfoWindow to prevent scrollbar and content cutoff`, `refactor(frontend): fix map InfoWindow padding and implement accordion UI in trip settings`, `feat(frontend): add numbered map markers and styled InfoWindow`, `fix(frontend): repair card layout overflow and move photos to activity details`, `feat(frontend): add dynamic daily hours, editorial summary and place photos`, `fix(frontend): restore missing days selector in itinerary and modal`, `feat(frontend): auto-fill title from places and implement reviews modal`, `feat(frontend): integrate Google Places metadata and remove legacy location field`, `feat(frontend): add profile settings, password change and notification toggles`, `feat(frontend): improve expense splitting preview contrast on light mode`, `feat(frontend): quick dev access buttons on login`, `feat(frontend): rename Finanzas view to Gastos and tidy code`, `feat(frontend): simplify debts UI removing DEUDA_PAGADOR and fixing percentages`, `feat(frontend): implement liquidation button reusing expense modal`, `feat(frontend): expense edit/delete with dynamic split preview`, `feat(frontend): render debts summary ui, fix expense UUID mapping and finalize individual budget`.
- **Hito actual:** **DICCIONARIO DE SUPERVIVENCIA — UI Y CONEXIÓN DE DATOS (`DiccionarioService` CON `GET /api/diccionario/categorias` Y `GET /api/diccionario/frases/{categoriaId}`; VISTA `/diccionario` NUEVA EN LA BOTTOM NAV CON CUADRÍCULA `grid-cols-2` DE CATEGORÍAS (ICONO-EMOJI GIGANTE + NOMBRE) Y MODAL BOTTOM-SHEET CON LA LISTA DE FRASES (ESPAÑOL BOLD · INGLÉS GRIS · JAPONÉS REAL EN BLOQUE AZUL RESALTADO + ROMAJI EN CURSIVA))** + **REGENERACIÓN INDIVIDUAL DE ACTIVIDADES IA (BOTÓN REFRESH POR ACTIVIDAD EN LA PREVISUALIZACIÓN → `POST /API/IA/REGENERAR-ACTIVIDAD` CON `destino`/`tematica`/`hora`/`actividadActual` — SUSTITUCIÓN INMUTABLE EN LA POSICIÓN EXACTA DEL ARRAY + SPINNER POR TARJETA Y BLOQUEO DEL RESTO DE BOTONES)** + **ASISTENTE IA CON FECHAS REALES Y CONTEXTO DEL VIAJERO (INPUTS `type="date"` FECHA INICIO/FIN EN LUGAR DE DÍAS + TEXTAREA "DETALLES ADICIONALES" OPCIONAL — PAYLOAD `/API/IA/GENERAR` Y `/API/IA/GUARDAR` CON `fechaInicio`/`fechaFin`/`detallesAdicionales`; ACTIVIDADES ESTRUCTURADAS {`hora`, `titulo`, `lugar`, `descripcion`} RENDERIZADAS EN TARJETAS CON HORA AZUL + 📍 LUGAR + DESCRIPCIÓN)** + **PERSISTENCIA DEL ITINERARIO GENERADO POR IA EN LA BD (BOTÓN "¡GUARDAR ESTE VIAJE!" → `POST /API/IA/GUARDAR` → REDIRECCIÓN AL DETALLE DEL VIAJE CREADO — "VIAJE A {DESTINO} (IA)" CON ACTIVIDADES POR DÍA)** + **ASISTENTE IA (CONSUMO `/API/IA/GENERAR` DE GOOGLE GEMINI — UI MÁGICA CON GRADIENTES PÚRPURA/AZUL Y PARSEO DE LA RESPUESTA JSON)** + **FIX CONVERSIÓN LOCAL DE MONEDA IDÉNTICA (ERROR RESET + RATIO 1.0 SIN LLAMADA HTTP)** + FIX OVERFLOW DE LOS SELECTORES DEL CONVERSOR + NOMBRE COMPLETO DE LA MONEDA ORIGEN EN LA CABECERA DE LA TARJETA AZUL + NOMBRES COMPLETOS DE MONEDAS EN EL CONVERSOR (DROPDOWNS Y RESULTADO — UX LEGIBLE) + CONVERSOR MULTI-DIVISA (SELECTORES DINÁMICOS CON CATÁLOGO DE MONEDAS `/API/DIVISAS/MONEDAS` + FECHA DE ACTUALIZACIÓN DEL MERCADO EN LA RESPUESTA) + CONVERSOR DE DIVISAS (CONSUMO `/API/DIVISAS/CONVERTIR` CON EUR↔JPY Y BOTÓN INVERTIR) + FIX TOTAL GRUPO EN FINANZAS (EXCLUYE GASTOS INDIVIDUALES) + CLIMA CON EMOJIS EN EL SELECTOR DE DÍAS (UX, SIN TEXTO DESCRIPTIVO) + FINANZAS/GASTOS COMO PESTAÑA ANIDADA DEL DETALLE DEL VIAJE (FIX INTEGRACIÓN) + CLIMA EN SELECTOR DE DÍAS (CONSUMO `/API/CLIMA` OPEN-METEO) + FASE 1 + B1-P2 + B2-P1 + B2-P2 + B3-P1 (Dashboard) + B3-P2 (Crear Viaje + Invitaciones) + B4-P1 (Detalle Viaje) + B4-P2 (Itinerario + Mini-Calendario) + B5-P1 (Finanzas) + Dark Mode + Proxy + HOTFIX Tailwind v3 + BUGFIX Y PULIDO + ITERACIÓN UI/UX + HOTFIX ddl-auto + Interceptor 401/403 + Módulo Perfil + USABILIDAD UI/UX + INTEGRACIÓN CHECKLIST CON BACKEND + SUBIDA DE DOCUMENTOS REAL (R2) + SELECTOR DE ASIGNACIÓN CHECKLIST + **UI MULTI-DOCUMENTO (1:N) CON BORRADO + ABANDONAR VIAJE (BORRADO LÓGICO) + MAPA LEAFLET + GEOCODIFICACIÓN NOMINATIM + MIGRACIÓN A GOOGLE MAPS PLATFORM + CLEAN MAPS WARNINGS + GEOLOCALIZACIÓN HTML5 + CRUD COMPLETO DE ACTIVIDADES (EDITAR + BORRAR) + FILTROS DINÁMICOS Y AUTO-CENTRADO DEL MAPA + GALERÍA MULTIMEDIA (ÁLBUMES Y MURO DE FOTOS) + PANEL DE AJUSTES DEL VIAJE (PORTADA, EDICIÓN DE DATOS, PARTICIPANTES, INVITAR Y ZONA DE PELIGRO) + PULIDO UI (AVATAR EN CABECERA, PORTADA AJUSTES Y TARJETAS CON PORTADA) + AVATAR INTERACTIVO EN PERFIL (SUBIDA A R2) + AVATARES DE USUARIO RENDERIZADOS (CABECERA, PARTICIPANTES Y GALERÍA) + FINANZAS REACTIVAS (SELECTO GRUPO/PERSONAL CON BALANCES) + MODAL AVANZADO DE CREACIÓN DE GASTOS (DIVISIÓN DINÁMICA) + PRESUPUESTO INDIVIDUAL (AJUSTES) + FIX UUID PAGADOR EN GASTOS + RESUMEN DE DEUDAS (FINANZAS) + ETIQUETAS SPLITWISE EN LISTA DE GASTOS + EDICIÓN Y BORRADO DE GASTOS CON RECÁLCULO DE DEUDAS + PREVISUALIZACIÓN DINÁMICA DEL REPARTO + BOTÓN DE LIQUIDACIÓN DE DEUDA (GASTO INVERSO) + REFACTOR FRONTEND DE DEUDAS (ELIMINACIÓN DEUDA_PAGADOR, FIX MATEMÁTICO DE PORCENTAJES Y PREVISUALIZACIÓN ESTRUCTURADA) + UX RENAME "FINANZAS" → "GASTOS" + LIMPIEZA DE CÓDIGO + **ACCESO RÁPIDO DE DESARROLLO EN LOGIN (BOTONES "ENTRAR COMO JAVI/CYNTHIA")** + **FIX CONTRASTE DE LA PREVISUALIZACIÓN DEL REPARTO EN MODO CLARO** + **PERFIL Y AJUSTES (CUENTA — CAMBIO DE CONTRASEÑA — Y NOTIFICACIONES CON TOGGLES) + CENTRALIZACIÓN DEL MODO OSCURO EN PERFIL** + **INTEGRACIÓN GOOGLE PLACES EN ACTIVIDAD (METADATOS Y ELIMINACIÓN DE LA UBICACIÓN LIBRE/DESCRIPCION)** + **AUTO-RELLENO DEL TÍTULO DESDE GOOGLE PLACES + MODAL DE RESEÑAS GOOGLE (UX)** + **RESTAURACIÓN DEL SELECTOR DE DÍAS DEL ITINERARIO CON BOTÓN "TODOS"** + **HORARIOS DINÁMICOS DEL DÍA (TIMELINE) + DESCRIPCIÓN EDITORIAL DE GOOGLE (EDITORIAL SUMMARY) + FOTOS DE GOOGLE EN EL MODAL DE RESEÑAS** + **FIX DESBORDAMIENTO DE LAS TARJETAS DEL ITINERARIO Y FOTOS DE GOOGLE MOVIDAS AL MODAL DE DETALLES** + **MARCADORES NUMERADOS EN EL MAPA + INFOWINDOW RICO EN HTML** + **FIX PADDING DEL INFOWINDOW + VISTA DE AJUSTES CON ACORDEÓN (ESTILO PERFIL)** + **FIX SCROLLBAR Y CORTE DE CONTENIDO EN EL INFOWINDOW** + **GASTOS DENTRO DEL CONTEXTO DEL VIAJE (RUTA /VIAJES/:ID/GASTOS + PESTAÑA "GASTOS") + BARRA INFERIOR DE 5 PESTAÑAS CON IA Y CONVERSOR (PLACEHOLDERS)** COMPLETADOS.

### ✅ Completado en esta sesión (Fix scroll fantasma/overscroll — layout base fijado a 100dvh)
- [x] **`main-layout.html` reestructurado (contenedor raíz):** el wrapper del layout pasa de `min-h-screen bg-gray-100 pb-20` a **`flex h-[100dvh] w-full flex-col overflow-hidden bg-gray-100`** — ocupa exactamente el alto dinámico de la pantalla (sin rebote en móviles gracias a `100dvh`), es un flex en columna y con `overflow-hidden` impide que el documento crezca más allá de la pantalla.
- [x] **`<main>` (contenido del `<router-outlet />`):** ahora es **`w-full flex-1 overflow-y-auto`** — el scroll queda confinado al contenido real de cada página y solo aparece cuando este supera el espacio disponible (p. ej. la vista Traductor larga). En vistas cortas (Viajes, IA, Conversor, Perfil) no hay barra ni hueco fantasma.
- [x] **Vistas del MainLayout sin altura forzada:** `dashboard.html`, `ia.html`, `conversor.html`, `perfil.html` y `diccionario.html` cambian su raíz de **`min-h-screen` a `min-h-full`** — ocupan exactamente el alto del contenedor `flex-1` (resolución de porcentaje contra el flex item) en lugar de forzar `100vh` que sumaba con el padding y provocaba el hueco inferior/superior.
- [x] **Menú inferior intacto:** el `<nav>` sigue `fixed bottom-0` (fixed es relativo al viewport, no se ve afectado por el `overflow-hidden` del ancestro), siempre visible abajo; las vistas conservan su `pb-28` para no quedar tapadas. Los modales `fixed inset-0` (dashboard, diccionario, etc.) tampoco se ven afectados (sin `transform`/`filter` en el ancestro, no se crea containing block).
- [x] **Sin cambios de backend ni enrutamiento.** Rutas top-level fuera del MainLayout (login, registro, crear-viaje, detalle-viaje) conservan su scroll de página natural con `min-h-screen`.
- [x] **Verificación:** `ng build` compila sin errores. Commit `f495e63`: `fix(frontend): fix ghost scroll by locking main layout to 100dvh flex column with scrollable content`.

### ✅ Completado en esta sesión (Limpieza del Conversor + icono de Viajes con avión real + título "Traductor")
- [x] **Limpieza del Conversor (`conversor.html`/`conversor.ts`):** eliminado el bloque completo del **Traductor** que se había incrustado en la vista del conversor de divisas (sección `Traductor` con selector de idioma 🇯🇵/🇬🇧, `textarea` de texto, botón de micrófono de dictado, botón ✨ Traducir y bloque azul de resultado con altavoz 🔊). El conversor muestra ahora únicamente la herramienta de cambio de divisas (tarjeta azul De/A + invertir, resultado y fecha de actualización). En `conversor.ts` se retiran el `IaService` (inyección e import), las señales `textoOrigen`/`idiomaDestino`/`traduccion`/`traduciendo`/`estaGrabando`/`errorTraduccion` y los métodos `cambiarIdiomaTraduccion`/`iniciarDictado`/`traducir`/`reproducirAudio`. El tipo `TraduccionResponseDTO` deja de usarse aquí (el traductor vive solo en el Diccionario). Lazy chunk `conversor` baja de ~14.22 kB a ~7.75 kB.
- [x] **Icono de Viajes (`main-layout.html`):** el icono del botón **Viajes** de la Bottom Nav pasa del **avión de papel** (Heroicons `paper-airplane`/send) a un **avión comercial real** (path Material `flight`, `M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z`), renderizado en el mismo estilo outline del menú (`fill="none" stroke="currentColor"`).
- [x] **Coherencia "Traductor" (`diccionario.html`):** el título principal de la página (`<h1>`) pasa de **"Diccionario"** a **"Traductor"**, con el icono del libro sustituido por el **globo de idiomas** (mismo path que el menú) y el subtítulo ajustado: "Traduce al instante y consulta frases de supervivencia en inglés y japonés."
- [x] **Sin cambios de backend ni de enrutamiento:** la ruta `/conversor` y `/diccionario` y sus `routerLink` se mantienen intactos.
- [x] **Verificación:** `ng build` compila sin errores. Commit `072219e`: `fix(frontend): remove translator block from Conversor and switch Viajes icon to a real airplane`.

### ✅ Completado en esta sesión (Bottom Nav de 5 pestañas — Conversor restaurado, Viajes con avión y Diccionario→Traductor)
- [x] **`main-layout.html` — 5 pestañas:** la Bottom Navigation Bar pasa de `grid-cols-4` a **`grid-cols-5`** con el orden estricto **Viajes · IA · Conversor · Traductor · Perfil**. Se conservan las clases por botón (`flex flex-col items-center justify-center gap-1 py-2 min-h-14` → zona táctil ≥56px), de modo que el grid se auto-equilibra centrado y proporcional en móvil con los 5 elementos. No se toca CSS (`main-layout.css` sigue vacío).
- [x] **Iconos actualizados (Heroicons inline, patrón del proyecto):**
  - **Viajes** (`routerLink="/dashboard"`): icono sustituido por el **avión de papel** (`paper-airplane`, path `M6 12 3.269 3.125...`).
  - **IA** (`routerLink="/ia"`): se mantiene el icono de **chispas/sparkles** existente.
  - **Conversor** (`routerLink="/conversor"`): botón **re-añadido** (había sido eliminado en la sesión del Diccionario) con icono de **billete/moneda** (`currency-dollar`, path `M12 6v12m-3-2.818...`).
  - **Traductor** (`routerLink="/diccionario"`): **renombrado desde "Diccionario"** e icono de libro sustituido por el **globo de idiomas** (`language`, path `M10.5 21l5.25-11.25L21 21...`). El `routerLink` sigue apuntando al mismo componente (`/diccionario` → `Diccionario`).
  - **Perfil** (`routerLink="/perfil"`): sin cambios (persona + etiqueta).
- [x] **Ruta `/conversor` restaurada (`app.routes.ts`):** dentro de los `children` del `MainLayout` (protegida por `authGuard`), con `loadComponent` lazy → `Conversor` (`features/conversor/conversor`). El componente no ha cambiado (conversor de divisas multi-moneda + traductor Web Speech/Gemini); el lazy chunk `conversor` vuelve a empaquetarse.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `conversor` regenerado, ~14.22 kB). Commit `c3bf021`: `feat(frontend): restore Conversor tab and update bottom nav with airplane and translate icons`.

### ✅ Completado en esta sesión (Migración del Traductor dinámico al Diccionario + eliminación de la pestaña Conversor)
- [x] **Lógica migrada (`diccionario.ts`):** se inyecta **`IaService`** (junto a `DiccionarioService`) y se importa `FormsModule` (antes `imports: []`) para el `[(ngModel)]` del textarea. Se copian las señales del traductor evitando colisiones con el selector de idioma de las frases estáticas: **`textoOrigen`** (''), **`idiomaTraduccion`** (`'ja'` por defecto; el `idiomaDestino` existente sigue siendo el de las frases), **`traduccion`** (`TraduccionResponseDTO | null`), **`traduciendo`**, **`estaGrabando`** y **`errorTraduccion`**. Métodos copiados del Conversor y renombrados con sufijo para no pisar los existentes: **`cambiarIdiomaTraduccion(idioma: 'ja' | 'en')`**, **`iniciarDictado()`** (Web Speech API, `lang='es-ES'`, `onresult` → `textoOrigen`, `onstart/onend/onerror` → `estaGrabando`), **`traducir()`** (`POST /api/ia/traducir` con `{texto, idiomaDestino: idiomaTraduccion()}`, spinner + banner rojo) y **`reproducirAudio()`** (`speechSynthesis` con `lang='ja-JP'`/`'en-US'` según `idiomaTraduccion()`).
- [x] **UI del traductor en `diccionario.html`:** justo debajo del `<header>` de la página y **ANTES** de la cuadrícula de categorías, una tarjeta `rounded-3xl bg-white shadow-sm dark:bg-gray-800` con cabecera ("Traductor" + subtítulo + **pills** Japonés 🇯🇵 / Inglés 🇬🇧 enlazados a `idiomaTraduccion()`), **`<textarea rows="5" resize-none>`**, dos botones circulares (micrófono gris/rojo según `estaGrabando()` y ✨ Traducir con spinner mientras `traduciendo()`), banner rojo `@if (errorTraduccion())` y bloque azul de resultado `@if (traduccion(); as traduccion)` con `textoTraducido` grande (`text-2xl font-bold`), `pronunciacion` en cursiva y botón de altavoz 🔊 (esquina superior derecha). Separador visual **`<hr class="my-6 border-gray-200 dark:border-gray-800">`** entre la sección del traductor y la cuadrícula de categorías.
- [x] **Limpieza de navegación:** eliminado el botón **"Conversor"** de la Bottom Nav (`main-layout.html`, el `grid` pasa de `grid-cols-5` a `grid-cols-4` → Viajes · IA · Diccionario · Perfil) y borrada la ruta **`/conversor`** de `app.routes.ts`. Los archivos huérfanos `features/conversor/*` se dejan en el repo (fuera de la navegación y sin ruta → no se empaquetan: el lazy chunk `conversor` desaparece del build).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `diccionario` regenerado, ~16.0 kB; chunk `conversor` ya no aparece). Commit `fcaa90b`: `refactor(frontend): merge dynamic translator into dictionary view and remove conversor tab`.
- [x] **Nota (sesión siguiente): el motor del traductor pasa de Gemini a la Google Cloud Translation API (Basic v2)** — cambio 100% backend (`POST /api/ia/traducir` → GET `translation.googleapis.com/language/translate/v2`, extrae `data.translations[0].translatedText`). El contrato frontend NO cambia: `TraduccionResponseDTO {textoTraducido, pronunciacion}` con `pronunciacion` ahora **siempre `""`** (la API no devuelve Romaji). La UI del Diccionario ya estaba preparada: la sección de `pronunciacion` en cursiva solo se renderiza con `@if (traduccion.pronunciacion)` (`diccionario.html:167`), así que no queda hueco en blanco. Sin cambios de código en el frontend; solo se actualizó `frontend/AGENTS.md`.

### ✅ Completado en esta sesión (Traductor dinámico con Web Speech API + Gemini en el Conversor)
- [x] **`IaService.traducirTexto(request)`** (`core/services/ia.service.ts`): nuevo método → `POST /api/ia/traducir` con body tipado devolviendo `Observable<TraduccionResponseDTO>`. Nuevas interfaces Backend-First: **`TraduccionRequestDTO`** (`texto`, `idiomaDestino`) y **`TraduccionResponseDTO`** (`textoTraducido`, `pronunciacion`) — espejo de los DTOs de Java del endpoint de traducción con Gemini.
- [x] **`Conversor` ampliado (`conversor.ts`):** se inyecta `IaService` junto a `DivisaService`. Nuevas señales: **`textoOrigen`** (''), **`idiomaDestino`** (`'ja'` por defecto), **`traduccion`** (`TraduccionResponseDTO | null`), **`traduciendo`** (spinner), **`estaGrabando`** y **`errorTraduccion``. Métodos:
  - **`cambiarIdiomaTraduccion(idioma: 'ja' | 'en')`** fija la señal de idioma.
  - **`iniciarDictado()`** usa la **Web Speech API** (`(window as any).webkitSpeechRecognition || (window as any).SpeechRecognition`, `lang = 'es-ES'`): `onstart` → `estaGrabando = true`; `onresult` acumula los `transcripts` en `textoOrigen`; `onerror`/`onend` → `estaGrabando = false` (con mensaje inline si falla). Guard si el navegador no soporta reconocimiento.
  - **`traducir()`** valida texto no vacío, activa el spinner y llama al servicio con `{texto, idiomaDestino}`; al recibir la respuesta guarda `traduccion` y en error muestra banner rojo.
  - **`reproducirAudio()`** usa `window.speechSynthesis.speak(new SpeechSynthesisUtterance(textoTraducido))` con `lang = 'ja-JP'` o `'en-US'` según el idioma destino.
- [x] **UI del traductor (`conversor.html`):** nueva sección **"Traductor"** bajo el conversor de divisas con cabecera (título + subtítulo + **selector pills** Japonés 🇯🇵 / Inglés 🇬🇧 idéntico al del diccionario: activo `bg-blue-600 text-white`, inactivo `bg-gray-200 text-gray-700`). **`<textarea>`** grande (`rows=5`, `resize-none`, `[(ngModel)]="textoOrigen"`) y dos botones circulares grandes: **micrófono** (SVG Heroicons, fondo gris/rojo según `estaGrabando()`) → `iniciarDictado()` y **✨ Traducir** (gradiente azul/índigo con spinner `animate-spin` mientras `traduciendo()`) → `traducir()`. Debajo, `@if (traduccion(); as traduccion)` → bloque `bg-blue-50 dark:bg-blue-500/10` con el **`textoTraducido` en grande (`text-2xl font-bold`)**, la **`pronunciacion` en cursiva** (`@if` protegido) y un botón circular de **altavoz 🔊** (SVG) en la esquina superior derecha → `reproducirAudio()`. Banner rojo para `errorTraduccion()`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `conversor` regenerado, ~14.25 kB). Commit `548738c`: `feat(frontend): build dynamic translator UI with Web Speech API and Gemini integration`.

### ✅ Completado en esta sesión (Selector de idioma destino en el modal del diccionario)
- [x] **`Diccionario.idiomaDestino`** (`features/diccionario/diccionario.ts`): nueva señal `idiomaDestino = signal<'ja' | 'en'>('ja')` (por defecto japonés) y método **`setIdioma(idioma: 'ja' | 'en')`** que la actualiza.
- [x] **Selector pills en `diccionario.html`:** justo debajo del título de la categoría y antes de la lista de frases (dentro del modal bottom-sheet), un contenedor `flex rounded-xl bg-gray-100 p-1 dark:bg-gray-800` con dos botones **"Japonés 🇯🇵"** e **"Inglés 🇬🇧"** enlazados a `(click)="setIdioma('ja'|'en')"`. El activo usa `bg-blue-600 text-white` y el inactivo `bg-gray-200 text-gray-700` (vía bindings `[class.*]`).
- [x] **Renderizado condicional del bucle de frases:** el `textoEspanol` se mantiene **siempre en bold**; debajo, `@if (idiomaDestino() === 'ja')` muestra el bloque azul `bg-blue-50 dark:bg-blue-500/10` con `textoJapones` (`text-lg font-semibold`) y `textoJaponesRomaji` en cursiva; `@else` muestra el `textoIngles` en un bloque gris suave `bg-gray-100 dark:bg-gray-800` con el mismo estilo tipográfico (consistencia visual). Se elimina la línea gris fija de inglés de debajo del español.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `diccionario` regenerado, ~9.52 kB). Commit `25fad55`: `feat(frontend): add target language selector to dictionary phrase list`.

### ✅ Completado en esta sesión (UI y conexión de datos del Módulo Diccionario de supervivencia)
- [x] **`DiccionarioService`** (nuevo, `core/services/diccionario.service.ts`, `providedIn: 'root'`): expone las interfaces **`CategoriaDiccionario`** (`id: number`, `nombre`, `icono`) y **`FraseDiccionario`** (`id: number`, `textoEspanol`, `textoIngles`, `textoJapones`, `textoJaponesRomaji`) y los métodos **`getCategorias()`** → `GET /api/diccionario/categorias` devolviendo `Observable<CategoriaDiccionario[]>` y **`getFrasesPorCategoria(categoriaId)`** → `GET /api/diccionario/frases/{categoriaId}` devolviendo `Observable<FraseDiccionario[]>` (espejo del `DiccionarioController` del backend, JWT obligatorio).
- [x] **`DiccionarioComponent`** (nuevo, `features/diccionario/`, standalone, lazy ~8.45 kB): señales `categorias`, `categoriaSeleccionada` (`CategoriaDiccionario | null`), `frasesActuales`, `cargandoCategorias`, `cargandoFrases` y `error`. `ngOnInit()` → `cargarCategorias()`. **`abrirCategoria(categoria)`** fija la señal, resetea frases y llama al servicio para rellenar `frasesActuales` (con su propio spinner). **`cerrarCategoria()`** limpia la selección y las frases para volver a la cuadrícula. Helper **`iconoEmoji(icono)`** mapea el `icono` del backend (`train-outline`, `restaurant-outline`, `bag-handle-outline`, `medkit-outline`) a un emoji (🚆🍜🛍️🚑) con fallback 💬 — la UI del proyecto es Tailwind/SVG (sin librería Ionic), así que se usa el emoji estilo de la app (mismo patrón que los emojis del clima/lugares) en lugar de `<ion-icon>`.
- [x] **UI (`diccionario.html`):** cabecera con icono de libro + subtítulo; **cuadrícula `grid grid-cols-2 gap-4`** donde cada categoría es un `<button>` tarjeta con el icono gigante en chip con gradiente azul/índigo (`h-16 w-16` rounded-2xl) y el nombre debajo (`active:scale-95`). Spinner/empty-state para la carga de categorías y banner rojo de error. **Modal bottom-sheet** (`@if (categoriaSeleccionada())` + overlay `fixed inset-0 z-50 ... bg-black/50 backdrop-blur-sm`, patrón idéntico a los modales de `detalle-viaje`): `<header>` con el emoji, el nombre de la categoría y el contador de frases + botón circular "✕" para cerrar; `<div>` scrollable con la lista (`@for` sobre `frasesActuales`) donde cada frase es una tarjeta que muestra **`textoEspanol` en bold**, **`textoIngles`** en gris pequeño debajo, y un bloque resaltado `bg-blue-50 dark:bg-blue-500/10` con el **`textoJapones`** en caracteres reales (`text-lg font-semibold`) y el **`textoJaponesRomaji`** en cursiva justo debajo.
- [x] **Ruta (`app.routes.ts`):** nuevo `/diccionario` dentro del `MainLayout` (protegido por `authGuard`) con `loadComponent` → `Diccionario`. El botón "Diccionario" de la Bottom Nav (`routerLink="/diccionario"`, que no tenía ruta definida) por fin funciona.
- [x] **Verificación:** `ng build` compila sin errores (nuevo lazy chunk `diccionario`, ~8.45 kB). Commit `3c876f4`: `feat(frontend): create survival dictionary UI with categories and phrases modal`.

### ✅ Completado en esta sesión (Regeneración individual de actividades IA)
- [x] **`IaService.regenerarActividad(request)`** (`core/services/ia.service.ts`): nuevo método → `POST /api/ia/regenerar-actividad` con body tipado devolviendo `Observable<ActividadIaDTO>`. Nueva interfaz **`IaRegenerarActividadRequestDTO`** (`destino`, `tematica`, `hora`, `actividadActual` — espejo del `IaRegenerarActividadRequestDTO` de Java: `@NotBlank` + `@Size(max=5)` en `hora`, `@Size(max=255)` en destino/actividad, `@Size(max=500)` en temática). `ActividadIaDTO` se amplía para reflejar el DTO actual del backend: se añaden `latitud?`, `longitud?` y `horario?` (opcionales) además de `hora`, `titulo`, `lugar`, `rating?` y `descripcion` existentes.
- [x] **`IaComponent.regenerarPlan(diaIndex, actividadIndex, actividad)`** (`features/ia/ia.ts`): valida que exista el día/actividad, llama al servicio extrayendo `destino`/`tematica` del estado del formulario y `hora`/`actividadActual` (el `titulo` de la actividad) de la actividad clicada. Nueva señal **`regenerando`** (`string | null`) que guarda la clave `diaIndex-actividadIndex` de la actividad en curso: bloquea TODOS los botones (refresh de las demás tarjetas vía `regenerando() !== null`, "Generar Ideas" y "Guardar este viaje") mientras la petición dura. Al recibir la `ActividadIaDTO` nueva, la sustituye en la posición exacta reemplazando el día afectado de forma inmutable (`.map` sobre `itinerarioGenerado()` en vez de mutar). Errores → banner rojo inline existente.
- [x] **Botón de regeneración en `ia.html`:** en cada tarjeta de actividad, botón discreto a la derecha de la fila hora+título (`flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-gray-400 hover:bg-gray-100 hover:text-blue-600`, variantes `dark:`) con icono **refresh-outline** (SVG Heroicons `arrow-path`, consistente con la UI Tailwind del proyecto — no Ionic) y `aria-label`/`title` "Regenerar actividad". Mientras `regenerando() === diaIndex + '-' + actividadIndex`, el icono se sustituye por un `<svg>` spinner `animate-spin`. `(click)` vincula `regenerarPlan(diaIndex, actividadIndex, actividad)` (los `@for` ganan `let diaIndex = $index` y `let actividadIndex = $index`).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `ia` regenerado, ~12.23 kB). Commit `61754a1`: `feat(frontend): add regenerate button and logic for individual AI activities`.

### ✅ Completado en esta sesión (Datepicker unificado en el formulario de la IA)
- [x] **Unificación del selector de fechas (`ia.html`):** los dos `<input type="date">` nativos (con `[color-scheme:dark]`) se sustituyen por el **`<app-custom-date-picker>`** compartido (el mismo `ControlValueAccessor` que usa el formulario manual `crear-viaje` con su modal de calendario). Los enlaces de datos se conservan intactos con **`[(ngModel)]="fechaInicio"`** / **`[(ngModel)]="fechaFin"`** sobre las señales del componente (el componente expone `NG_VALUE_ACCESSOR`, por lo que funciona con template-driven igual que con `formControlName`).
- [x] **`ia.ts`:** se importa `CustomDatePicker` (`shared/components/custom-date-picker/`) y se añade a los `imports` del componente (junto a `FormsModule`). `generarItinerario()` y `guardarItinerario()` siguen leyendo `fechaInicio()`/`fechaFin()` sin cambios — las fechas se capturan y envían igual que antes.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `ia` regenerado, ~10.09 kB).

### ✅ Completado en esta sesión (Horario visible en las tarjetas IA — condición de visibilidad flexibilizada)
- [x] **Inspección del DTO:** el modelo `Actividad` (`shared/types/index.ts`) confirma el campo **`horarios: string | null`** (espejo del `ActividadResponseDTO.horarios` del backend) como el que guarda el horario corto de la tarjeta. El valor llega relleno en las actividades IA (`"Lunes a Domingo: 10:00 - 20:00"`).
- [x] **Diagnóstico (`detalle-viaje.html`, tarjeta del timeline):** en la fila inferior de la tarjeta (`mt-3 flex w-full items-end justify-between`), el chip de estrellas (a la izquierda, `@if (actividad.rating != null && actividad.googlePlaceId)`) aparecía correctamente, pero el elemento del reloj + horario (a la derecha) dependía de `@if (obtenerHorarioDia(actividad.horarios, actividad.fechaHoraInicio); as horarioDia)` — y `obtenerHorarioDia` devuelve `''` cuando la cadena de la IA no parsea con el formato por-día manual, dejando el campo en blanco.
- [x] **Fix (visibilidad flexibilizada):** la condición pasa a **`@if (actividad.horarios)`** — el elemento del reloj se muestra **SIEMPRE** que la actividad tenga el string relleno, sin depender de `rating`/`googlePlaceId` ni de que el parseo por día tenga éxito. El texto mostrado usa `{{ obtenerHorarioDia(actividad.horarios, actividad.fechaHoraInicio) || actividad.horarios }}`: para tarjetas manuales se conserva la franja corta del día parseada (ej. `6:30–23:00`), y para tarjetas IA, si el parseo falla, se muestra el string completo (ej. `Lunes a Domingo: 10:00 - 20:00`).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~162.56 kB).

### ✅ Completado en esta sesión (Asistente IA — fechas reales, contexto extra y actividades estructuradas)
- [x] **Tipos Backend-First actualizados** (`core/services/ia.service.ts`):
  - **`IaPrompt`**: se elimina `dias: number` y se añaden **`fechaInicio: string`**, **`fechaFin: string`** y **`detallesAdicionales: string`** (espejo del `IaPromptRequestDTO` de Java con `LocalDate`/`String` y sin `Integer dias`).
  - **Nuevo `ActividadIaDTO`** (`hora`, `titulo`, `lugar`, `descripcion` — espejo del nuevo `ActividadIaDTO` del backend).
  - **`DiaIaDTO`**: `actividades: string[]` → **`actividades: ActividadIaDTO[]`** (cada actividad es ahora un objeto estructurado).
  - **`IaGuardarViajeRequest`**: se añaden **`fechaInicio: string`** y **`fechaFin: string`** (el backend los exige ahora para crear el viaje con fechas reales).
- [x] **`IaComponent` reescrito (`features/ia/ia.ts`):**
  - **Eliminada la señal `dias`** (default 3) y sustituida por **`fechaInicio = signal('')`**, **`fechaFin = signal('')`** y **`detallesAdicionales = signal('')`**.
  - `generarItinerario()` envía el nuevo payload `{ destino, fechaInicio, fechaFin, tematica, detallesAdicionales }`.
  - `guardarItinerario()` añade `fechaInicio: this.fechaInicio()` y `fechaFin: this.fechaFin()` al objeto pasado a `guardarViaje()`.
  - El tipo local `DiaItinerario` se elimina: `itinerarioGenerado` se tipa ahora con el **`DiaIaDTO`** importado del servicio (mismo contrato que Gemini devuelve en `rawJson`).
- [x] **Formulario (`ia.html`):** el `<input type="number">` de "Días" se sustituye por un **grid `grid-cols-2 gap-4`** con dos **`<input type="date">`** (`fechaInicio` / `fechaFin`, labels "Fecha de inicio"/"Fecha de fin", clase `[color-scheme:dark]` para que el picker nativo renderice en oscuro sobre el degradado). Debajo de "Temática", nuevo **`<textarea rows="3" resize-none>`** con label **"Detalles adicionales (Opcional)"** y placeholder "Ej: Llego a Narita, 5 días en Tokio, luego tren a Osaka... Viajamos a ritmo tranquilo.", enlazado a `detallesAdicionales`.
- [x] **Actividades estructuradas (`ia.html`):** el `<li>` plano con número + texto se sustituye por una tarjeta `mb-4 flex flex-col rounded-lg border border-gray-100 bg-gray-50 p-3` (+ variantes `dark:`) que muestra: fila `flex items-center space-x-2 font-bold` con **hora en azul** (`text-blue-600`) + **título**; debajo `text-xs text-gray-500` con **📍 lugar**; y la **descripción** en `text-sm text-gray-600 mt-2` protegida con `@if (actividad.descripcion)`. El `<ul>` pierde el `space-y-3` (el `mb-4` de cada tarjeta asume el espaciado).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `ia` regenerado). Commit `42221ff`: `feat(frontend): add dates and extra context inputs, render structured AI locations`.

### ✅ Completado en esta sesión (Persistencia del itinerario de IA — guardar el viaje generado)
- [x] **`IaService.guardarViaje(request)`** (`core/services/ia.service.ts`): nuevo método → `POST /api/ia/guardar` con body tipado y devolviendo `Observable<Viaje>` (el backend responde 201 con el `ViajeResponseDTO` del viaje recién creado). Nuevas interfaces Backend-First exportadas desde el servicio: **`DiaIaDTO`** (`dia`, `titulo`, `actividades` — espejo del `DiaIaDTO` de Java, la misma estructura que devuelve Gemini en `rawJson`) y **`IaGuardarViajeRequest`** (`destino`, `itinerario: DiaIaDTO[]` — espejo del `IaGuardarViajeRequestDTO`: `@NotBlank` destino, `@NotEmpty` itinerario).
- [x] **`IaComponent.guardarItinerario()`** (`features/ia/ia.ts`): nueva señal **`guardando`** (boolean). El método lee `itinerarioGenerado()`, monta el payload `{ destino, itinerario }` y llama al servicio; al recibir el `Viaje` creado **navega con `Router` a `/viajes/{viaje.id}`** (el detalle del viaje "Viaje a {destino} (IA)"). Se inyecta `Router` (`@angular/router`). Errores → banner rojo inline existente ("No se pudo guardar el viaje. Inténtalo de nuevo."). No hace nada si el itinerario está vacío.
- [x] **Botón de guardado en `ia.html`:** debajo del `@for` de días del itinerario (al final de la sección), un botón a ancho completo con gradiente esmeralda `bg-gradient-to-r from-emerald-600 to-green-600`, etiqueta **"¡Guardar este viaje!"**, `[disabled]="guardando()"` y, mientras dura la petición, spinner (`animate-spin`) + texto **"Guardando en la base de datos…"** — mismo patrón visual que el botón "Generar Ideas".
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `ia` regenerado, ~8.69 kB). Commit `5d6fab9`: `feat(frontend): add save trip functionality to AI assistant`.

### ✅ Completado en esta sesión (Asistente IA — consumo de `/api/ia/generar` con Gemini)
- [x] **`IaService`** (nuevo, `core/services/ia.service.ts`, `providedIn: 'root'`): `generarItinerario(prompt)` → `POST /api/ia/generar` con body tipado `IaPrompt` (`destino`, `dias`, `tematica`) y respuesta tipada `IaItinerario` (`rawJson`), espejo del `IaPromptRequestDTO`/`IaItinerarioResponseDTO` del backend. Interfaces exportadas desde el propio servicio.
- [x] **`IaComponent`** (nuevo, `features/ia/`, standalone, lazy ~7.34 kB):
  - Señales: `destino` (''), `dias` (3), `tematica` (''), `cargando` (boolean) e `itinerarioGenerado` (`Array`, default `[]`), más `error` (mensaje inline).
  - `generarItinerario()`: llama al servicio; al recibir la respuesta envuelve `JSON.parse(res.rawJson)` en `try/catch` — parseo OK → `itinerarioGenerado.set(...)`, fallo → `console.error` + `alert('No se pudo interpretar la respuesta de la IA...')` y reset del array. Error de red → banner rojo inline.
  - UI mágica mobile-first: tarjeta superior `bg-gradient-to-br from-violet-600 via-purple-600 to-blue-600` con 3 inputs (Destino, Días, Temática) enlazados con `[(ngModel)]`, botón "Generar Ideas" con icono de chispa ✨ que muestra spinner + "Generando magia…" mientras `cargando()`; abajo, listado `@for` sobre `itinerarioGenerado()` con tarjetas por día (encabezado degradado con "Día N" + `titulo`) y `<ul>`/`<li>` con las `actividades` numeradas.
- [x] **Ruta `/ia` (`app.routes.ts`):** el `loadComponent` pasa del `Placeholder` al `IaComponent` (se elimina `data: { titulo }`). Protegida por `authGuard` dentro del `MainLayout`.
- [x] **Verificación:** `ng build` compila sin errores (nuevo lazy chunk `ia` ~7.34 kB). Commits `e7ccee3` (`fix(frontend): handle identical currency local conversion` — cierre de la sesión pendiente) y `0318293` (`feat(frontend): implement AI assistant UI and parse Gemini JSON response`).

### ✅ Completado en esta sesión (Fix overflow selectores + nombre completo de la moneda origen en la cabecera)
- [x] **Revertido el contenido de las `<option>` (`conversor.html`):** los dos `@for` de los `<select>` vuelven a mostrar **ÚNICAMENTE el código** (`{{ moneda.codigo }}`), eliminando el sufijo `- {{ moneda.nombre }}` que estiraba las pill-boxes y cortaba el texto en pantallas estrechas.
- [x] **Nombre de la moneda origen movido a la cabecera:** el `label` "Cantidad" pasa a un contenedor flex `flex justify-between items-end mb-2` con el label en `text-sm font-semibold text-white` a la izquierda y, a la derecha, `<span class="text-xs font-medium tracking-wide text-blue-200">{{ obtenerNombreMoneda(monedaOrigen()) }}</span>` con el nombre completo de la moneda origen (ej. "Euro"). El `mt-2` del input se traslada al contenedor (`mb-2`) para mantener el espaciado.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `conversor` regenerado, ~7.50 kB). Commit `33eed85`.

### ✅ Completado en esta sesión (UX del Conversor — nombres completos de las monedas en lugar de siglas)
- [x] **`obtenerNombreMoneda(codigo: string | undefined): string`** (nuevo método en `conversor.ts`): busca el código dentro del array de `monedasDisponibles()` con `.find()`; si lo encuentra devuelve su `nombre` (ej. "Japanese Yen") y si no (o recibe `undefined`) devuelve el propio código como fallback.
- [x] **Dropdowns descriptivos (`conversor.html`):** las `<option>` generadas por el `@for` pasan de mostrar solo `{{ moneda.codigo }}` a **`{{ moneda.codigo }} - {{ moneda.nombre }}`** (ej. "EUR - Euro", "JPY - Japanese Yen"). Se elimina el `[title]` (la info ya es visible en el texto de la opción).
- [x] **Resultado con nombre completo:** la tarjeta gigante de la conversión muestra ahora el código destino seguido del nombre entre paréntesis: `<span class="text-2xl font-bold text-blue-600">{{ res.monedaDestino }}</span> <span class="ml-1 text-lg font-normal text-gray-500 dark:text-gray-400">({{ obtenerNombreMoneda(res.monedaDestino) }})</span>`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `conversor` regenerado, ~7.35 kB). Commit `9b9937a`.

### ✅ Completado en esta sesión (Conversor multi-divisa — selectores dinámicos + fecha de actualización del mercado)
- [x] **`DivisaResponseDTO` ampliado** (`shared/types/index.ts`): nuevo campo opcional **`fechaActualizacion?: string`** (espejo del backend).
- [x] **`DivisaService.obtenerMonedas()`** (nuevo en `core/services/divisa.service.ts`): `GET /api/divisas/monedas` → `Observable<Record<string, string>>` con el catálogo clave-valor de Frankfurter (ej. `{"AUD":"Australian Dollar"}`).
- [x] **`ConversorComponent` reescrito (`conversor.ts`):**
  - Implementa `OnInit`: en `ngOnInit()` llama a `convertir()` (conversión inicial EUR→JPY) y a `cargarMonedas()`.
  - Nueva señal **`monedasDisponibles = signal<{ codigo: string; nombre: string }[]>([])`**: `cargarMonedas()` mapea el `Record<string,string>` con `Object.entries()` a un array de objetos `{codigo, nombre}` para poblar los `<select>` (tipo local `MonedaDisponible`). Si el catálogo falla, muestra un error inline.
  - Nuevos métodos **`cambiarMonedaOrigen(codigo)`** y **`cambiarMonedaDestino(codigo)`** que actualizan las señales `monedaOrigen`/`monedaDestino` y vuelven a llamar `convertir()`. `invertirMonedas()` y `actualizarMonto()` siguen disparando la conversión. Se añade `DatePipe` a los imports (con `DecimalPipe` y `FormsModule`).
- [x] **UI dinámica (`conversor.html`):**
  - Las pill-boxes fijos "EUR"/"JPY" se sustituyen por dos **`<select>` nativos** estilizados sin bordes (`appearance-none`, `bg-transparent`, `text-lg font-bold text-white`, chevron-down Heroicons superpuesto a la derecha vía `pointer-events-none`), enlazados con `[ngModel]` + `(ngModelChange)` a `monedaOrigen()`/`monedaDestino()`.
  - Un **`@for (moneda of monedasDisponibles(); track moneda.codigo)`** genera los `<option>` por moneda con `[value]="moneda.codigo"` y texto = código (más `[title]="moneda.nombre"` como tooltip); `class="bg-white text-gray-900"` en las opciones para legibilidad sobre el dropdown nativo.
  - Debajo de la ratio ("1 EUR = X JPY"), nueva línea **sutil**: `@if (resultado()?.fechaActualizacion)` → `text-xs text-gray-400 mt-4 text-center` con "Última actualización oficial: {{ resultado()!.fechaActualizacion | date: 'mediumDate' }}" (+ variante `dark:`).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `conversor` regenerado, ~7.10 kB). Commit `bf12d97`.

### ✅ Completado en esta sesión (Conversor de divisas — consumo de `/api/divisas/convertir`)
- [x] **`DivisaResponseDTO`** (Backend-First mirror en `shared/types/index.ts`): espejo exacto del `DivisaResponseDTO` de Java (`montoOriginal`, `monedaOriginal`, `montoConvertido`, `monedaDestino`, `ratioCambio`).
- [x] **`DivisaService`** (nuevo, `core/services/divisa.service.ts`, `providedIn: 'root'`): `convertir(amount, from, to)` → `GET /api/divisas/convertir` con `HttpParams` (`amount`, `from`, `to`); devuelve `Observable<DivisaResponseDTO>`.
- [x] **`ConversorComponent`** (nuevo, `features/conversor/`, standalone, lazy ~4.89 kB): señales `monto = signal(1)`, `monedaOrigen = signal('EUR')`, `monedaDestino = signal('JPY')`, `resultado`, `cargando` y `error`. El constructor llama `convertir()` automáticamente (muestra 1 EUR → JPY al entrar). `actualizarMonto(valor)` (input `number` grande, `step="any"`) y `invertirMonedas()` intercambian origen/destino y vuelven a llamar `convertir()`. Validación de `monto > 0` con mensaje inline; error genérico en rojo si la petición falla (p.ej. 503 de la API externa).
- [x] **UI premium mobile-first (`conversor.html`):** tarjeta `bg-gradient-to-br from-blue-600 to-indigo-700` con input grande de cantidad, dos pill-boxes "De" (origen) / "A" (destino), **botón central circular blanco con icono de flechas ⇄** (`active:scale-95`) para invertir, y tarjeta inferior blanca con el resultado en grande (`montoConvertido | number:'1.2-2'` + `monedaDestino`) y la ratio "1 ORIGEN = X DESTINO". Spinner "Convirtiendo…", estado vacío y banner rojo de error. `conversor.css` oculta las flechas nativas del input `number` (apariencia limpia).
- [x] **Ruta `/conversor` (`app.routes.ts`):** el `loadComponent` pasa del `Placeholder` al `ConversorComponent` (se elimina el `data: { titulo }`). El placeholder queda solo para `/ia`. Protegida por `authGuard` dentro del `MainLayout`.
- [x] **Verificación:** `ng build` compila sin errores (nuevo lazy chunk `conversor`, ~4.89 kB). Commit `d6fc955`.

### ✅ Completado en esta sesión (Fix total del grupo en Finanzas — se excluyen los gastos individuales)
- [x] **`totalGastadoVista` (`finanzas.ts`):** la rama GRUPO ya NO usa el valor precalculado `balance?.totalGastadoGrupo` del backend (que incluía erróneamente los gastos PERSONAL). Ahora se recalcula 100% en cliente con un `.reduce()` sobre `gastosVisibles()` (la lista ya filtrada por `tipoGasto === 'COMPARTIDO'`), garantizando que la tarjeta "Gastado (Grupo)" cuadre siempre con la lista visual de gastos grupales. La rama PERSONAL se mantiene con `balance?.totalGastadoUsuario` (ya correcto, fix backend previo).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~162.5 kB, con `finanzas` embebido). Commit `abb493b`.

### ✅ Completado en esta sesión (Sustitución del texto del clima por emojis en el selector de días)
- [x] **`obtenerEmojiClima(condicion)` (`detalle-viaje.ts`):** nuevo método `protected obtenerEmojiClima(condicion: string): string` que mapea el texto o el icono del clima a un emoji, ignorando mayúsculas/minúsculas (`toLowerCase` + `includes`): **☀️** para "despejado"/"soleado"/"sunny"/"clear", **☁️** para "nublado"/"nubes"/"cloud", **🌧️** para "lluvia"/"llovizna"/"rain", **⛈️** para "tormenta"/"truenos"/"storm"/"thunder", **❄️** para "nieve"/"snow", y **🌤️** por defecto. Cubre tanto las condiciones en español del backend como los códigos de icono Open-Meteo.
- [x] **UI del mini-calendario (`detalle-viaje.html`):** la interpolación `{{ clima.icono }}` se sustituye por `{{ obtenerEmojiClima(clima.condicion || clima.icono) }}`. La pastilla del día queda ahora exclusivamente con el **emoji + temperaturas** (ej. 🌧️ 27°/23°), ahorrando espacio y mejorando la estética. Se mantienen las temperaturas con `DecimalPipe` y los colores condicionales según el día activo.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~162.5 kB). Commit `a726561`.

### ✅ Completado en esta sesión (Fix integración de Finanzas/Gastos como pestaña anidada del detalle del viaje)
- [x] **`Finanzas` recibe el ID por `@Input` (`finanzas.ts`):** eliminados `ActivatedRoute` y `Router`. El componente expone ahora `readonly viajeId = input<UUID>()` (señal de entrada). La carga de datos (gastos, balance y participantes) se dispara con un `effect()` que reacciona a `viajeId()` — recarga automáticamente si cambia el viaje. Eliminado el método `goBack()`.
- [x] **`finanzas.html` sin cabecera propia:** eliminado el `<header>` con el botón "Atrás" y el título "Gastos" — el detalle del viaje ya tiene su propia cabecera y pestañas. El padding superior de la vista pasa de `pt-6` a `pt-2`.
- [x] **Pestaña anidada en el detalle (`detalle-viaje.ts`):** `setTab('gastos')` ya NO navega con el Router a `/viajes/:id/gastos`; simplemente fija `tabActiva.set('gastos')` como cualquier otra pestaña. `Finanzas` se importa (standalone) en los `imports` del componente.
- [x] **Renderizado como pestaña (`detalle-viaje.html`):** nuevo `@case ('gastos')` en el `@switch (tabActiva())` que renderiza `<app-finanzas [viajeId]="viaje().id" />` cuando el viaje está cargado.
- [x] **Ruta independiente eliminada (`app.routes.ts`):** borrada la ruta top-level `/viajes/:id/gastos`. El chunk `finanzas` ya no existe por separado: queda embebido en el lazy chunk `detalle-viaje`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~161.9 kB, con `finanzas` incluido).

### ✅ Completado en esta sesión (Predicción del clima en el selector de días — consumo de `/api/clima`)
- [x] **`ClimaService`** (nuevo, `core/services/clima.service.ts`, `providedIn: 'root'`): `getClima(lat, lon, fecha)` → `GET /api/clima` con query params `lat`, `lon` y `fecha` (`YYYY-MM-DD`); devuelve `Observable<Clima>`.
- [x] **Tipo `Clima`** (Backend-First mirror en `shared/types/index.ts`): espejo exacto del `ClimaResponseDTO` (`tempMax`, `tempMin`, `condicion`, `icono`, `esHistorico`).
- [x] **Detalle del viaje (`detalle-viaje.ts`):** se inyecta `ClimaService` y se añade la señal **`climasPorDia = signal<Map<string, Clima>>(new Map())`**. Un `effect()` sobre `dias()` recorre los días del viaje y, por cada día no cargado aún, llama al servicio con las coordenadas por defecto de **Tokio** (`lat: 35.6895`, `lon: 139.6917`) y la fecha del día (`dia.valor`), guardando el resultado en el mapa con la fecha como clave. La lectura previa del mapa se hace con `untracked` para no crear dependencias reactivas circulares.
- [x] **UI en los chips del mini-calendario (`detalle-viaje.html`):** dentro de cada botón de día, debajo de la fecha, se pinta `@if (climasPorDia().get(dia.valor); as clima)` → contenedor `text-[10px] leading-none` centrado con el **icono** (`clima.icono`) y las **temperaturas** `{{ clima.tempMax | number:'1.0-0' }}° / {{ clima.tempMin | number:'1.0-0' }}°` (se añade `DecimalPipe` a los imports del componente). Colores condicionales según el día activo (`text-blue-100`/`text-gray-400`). Los días sin clima (error o pendiente) simplemente no muestran nada.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~132.7 kB).

### ✅ Completado en esta sesión (Gastos dentro del contexto del viaje + barra inferior de 5 pestañas)
- [x] **Nueva ruta `/viajes/:id/gastos`** (`app.routes.ts`): ruta top-level protegida que carga `Finanzas` en pantalla completa, con el `viajeId` como parámetro de URL. Se eliminó la ruta `/gastos` de los `children` del `MainLayout`: el módulo de finanzas ya no es una vista global accesible desde la barra inferior, vive dentro del contexto del viaje.
- [x] **Pestaña "Gastos" en el detalle del viaje (`detalle-viaje.ts`):** nuevo `TabItem` con key `'gastos'`, label "Gastos" e icono de billetes (Heroicons) en la navegación superior por pestañas (junto a Itinerario, Mapa, Checklist, Galería y Ajustes). `setTab('gastos')` no cambia la pestaña interna: navega a `/viajes/{viajeId}/gastos` pasando el ID del viaje actual.
- [x] **`Finanzas` atado al contexto del viaje (`finanzas.ts/html`):**
  - Eliminado el `<select>` superior de selección de viaje (y con él las señales/computeds `viajes`, `viajeSeleccionado`, `viajeActual`, `presupuestoBase`, el `effect()` de auto-selección del primer viaje y el método `seleccionarViaje`).
  - El componente inyecta `ActivatedRoute` y obtiene `viajeId` directamente de `paramMap.get('id')`; al inicializar carga automáticamente `cargarGastos`, `cargarBalance` y `cargarParticipantes` de ese viaje (sin selección por parte del usuario).
  - `guardarGasto` y `eliminarGasto` usan `this.viajeId`; se añade `goBack()` y botón "Atrás" en la cabecera hacia `/viajes/{viajeId}`.
  - El FAB de "+ nuevo gasto" pasa de `bottom-24` a `bottom-6` (la vista ya no tiene Bottom Nav).
- [x] **Barra de navegación inferior (`main-layout.html`):** `grid-cols-5` con el orden estricto **Viajes · IA · Conversor · Diccionario · Perfil**. Se elimina el botón "Gastos" y se añaden los nuevos botones **IA** (icono sparkles, `/ia`) y **Conversor** (icono de intercambio de monedas, `/conversor`), ambos con `routerLinkActive` y estilos consistentes con el resto.
- [x] **Rutas placeholder `/ia` y `/conversor` (`app.routes.ts` + nuevo `features/placeholder/`):** componente `Placeholder` lazy dentro del `MainLayout` que muestra el título de la ruta (desde `route.data['titulo']`) y un mensaje "en camino". Ambas rutas protegidas por `authGuard`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunks `finanzas` ~30.6 kB, `detalle-viaje` ~131.4 kB y nuevo `placeholder` ~1.4 kB).

### ✅ Completado en esta sesión (Fix del InfoWindow del mapa — scrollbar y corte de contenido)
- [x] **Eliminado el margen negativo:** el contenedor principal del `map-info-window` pasa de `mt-[-8px] max-w-[200px] font-sans` a **`max-w-[200px] font-sans pr-6 pb-1`** — el `mt-[-8px]` cortaba el contenido superior y forzaba la barra de scroll nativa de Google Maps.
- [x] **Padding de seguridad:** `pr-6` evita que el texto se solape con la "X" nativa de cierre de Google Maps y `pb-1` (padding inferior leve) evita el scroll vertical.
- [x] **Interlineado compacto de la hora:** la línea de la hora pasa a `text-blue-600 font-bold text-sm leading-none mb-1` (antes `text-sm font-bold` sin `leading-none`), quedando compacta con el título.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~131 kB).

### ✅ Completado en esta sesión (Fix padding del InfoWindow del mapa + vista de Ajustes del viaje con acordeón estilo Perfil)
- [x] **Fix de espaciado del InfoWindow (`detalle-viaje.html`):** el contenedor principal del `map-info-window` pasa de `max-w-[200px] p-2 font-sans` a **`mt-[-8px] max-w-[200px] font-sans`** — se elimina el `p-2` (que se sumaba al padding nativo de Google) y se añade el margen superior negativo para compensar el hueco que deja la "X" de cierre de Google.
- [x] **Lógica del acordeón (`detalle-viaje.ts`):** nueva señal `seccionAbierta = signal<string | null>(null)` y método `toggleSeccion(seccion: string)` que la abre si estaba cerrada o la pone a `null` si estaba abierta (toggle). Nombres de sección: `general`, `participantes`, `mis-gastos`, `peligro`.
- [x] **UI de Ajustes en acordeón (`detalle-viaje.html`, pestaña `ajustes`):** las secciones **General**, **Participantes**, **Mis Gastos** y **Seguridad** pasan a ser tarjetas `overflow-hidden rounded-3xl bg-white shadow-sm dark:bg-gray-800` idénticas al patrón de `perfil.html`: fila clicable `flex w-full items-center gap-4 px-5 py-4 text-left active:bg-gray-50 dark:active:bg-gray-700/40` con icono en chip cuadrado `h-11 w-11 rounded-2xl bg-blue-100` (rojo en Seguridad) + título y subtítulo + chevron `ml-auto h-5 w-5 text-gray-300 transition-transform duration-200` con `[class.rotate-90]="seccionAbierta() === '...'"`.
  - **General:** icono de engranaje; subtítulo "Edita el nombre y las fechas del viaje". El formulario (organizador) y el bloque informativo read-only (invitado) quedan dentro de `@if (seccionAbierta() === 'general')`, con el contenido envuelto en `px-5 pb-6 pt-1`.
  - **Participantes:** icono de personas; subtítulo "Gestiona quién forma parte del viaje". El botón **"+ Invitar"** (solo organizador) se mueve al interior desplegado (cabeza de la lista); la lista y `errorExpulsar` dentro del `@if`.
  - **Mis Gastos:** icono de billetes; subtítulo "Ajusta tu presupuesto individual". Se elimina el wrapper de tarjeta interior redundante; los campos quedan directamente en el acordeón dentro de `@if (seccionAbierta() === 'mis-gastos')`.
  - **Zona de peligro → "Seguridad":** icono de sol/escudo con chip **rojo** (`bg-red-100 text-red-600`); subtítulo "Eliminar o abandonar el viaje". Botones de eliminar (organizador) / abandonar (invitado) y `errorAbandonar` dentro de `@if (seccionAbierta() === 'peligro')`.
  - La tarjeta "Portada del viaje" se mantiene como estaba (fuera del acordeón, al inicio de la pestaña).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~131 kB).

### ✅ Completado en esta sesión (Marcadores numerados en el mapa + InfoWindow rico en HTML)
- [x] **Marcadores numerados (AdvancedMarkerElement):** el `@for` de la pestaña Mapa usa `track actividad.id; let i = $index` y cada `<map-advanced-marker>` recibe `[content]="crearMarcadorNumerado(actividad.id, i)"`. El nuevo método `crearMarcadorNumerado(actividadId, index)` genera un **círculo azul con el número** (`flex h-7 w-7 items-center justify-center rounded-full bg-blue-600 text-[11px] font-bold text-white ring-2 ring-white shadow-md`) y lo **cachea por actividad** en un `Map<string, HTMLElement>` (referencia estable entre ciclos de detección de cambios; solo se actualiza `textContent = index + 1`). Nota: `AdvancedMarkerElement` no soporta la propiedad `label` de los `Marker` clásicos, por eso se usa `content` (mismo patrón que el "punto azul" de geolocalización).
- [x] **Orden cronológico de los marcadores:** `actividadesMapa()` ahora hace `.slice().sort((a, b) => a.fechaHoraInicio.localeCompare(b.fechaHoraInicio))` sobre `actividadesConCoordenadas()` ANTES de aplicar los filtros (DAY/ACTIVITY), de modo que los números del 1..N se corresponden con el orden real del viaje.
- [x] **InfoWindow rico en HTML (template-based dentro de `<map-info-window>`):** el bloque nuevo es `<div class="max-w-[200px] p-2 font-sans">` con:
  - Hora (azul, `text-sm font-bold text-blue-600`) vía pipe `date: 'HH:mm'` sobre `fechaHoraInicio`.
  - Título (`text-base font-bold leading-tight text-gray-900`).
  - Rating opcional protegido con `@if (actividad.rating != null)` → `⭐ X.X` en ámbar (`text-yellow-600`).
  - Dirección opcional con `@if (actividad.direccion)` → `truncate text-xs text-gray-500`.
  - **Sin texto "undefined":** todos los campos opcionales quedan envueltos en `@if`, heredando el manejo de nulos ya existente (antes solo mostraba hora + título en `min-w-40 text-center`).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~126 kB).

### ✅ Completado en esta sesión (Fix desbordamiento de las tarjetas del itinerario + fotos de Google en el modal de detalles)
- [x] **Fix CSS de la tarjeta del plan (`itinerario.html`):** el `<article>` usa ahora estrictamente `w-full overflow-hidden` (evita que el contenido interno desborde la pantalla). La sección inferior de la tarjeta (estrellas + horario dinámico) pasa a ser un flexbox unificado `<div class="flex justify-between items-end w-full mt-3">`: el **chip de estrellas** queda a la izquierda y el **horario dinámico** a la derecha, eliminando el `ml-auto` previo del horario (`justify-between` empuja ambos extremos sin romper el ancho). El horario pierde `justify-end`/`mt-1.5` internos.
- [x] **Fotos de Google movidas al modal de detalle (`actividad-detalle`):**
  - `GoogleMapsService.obtenerResenasGoogle` vuelve a devolver **solo** `Promise<PlaceReview[]>` (fields `['reviews']`). Nuevo método `obtenerFotosLugar(placeId): Promise<string[]>` que llama a `getDetails({ placeId, fields: ['photos'] })` y devuelve hasta 3 URLs con `foto.getUrl({ maxWidth: 400, maxHeight: 300 })` (array vacío si falla o no hay fotos).
  - Nueva señal **`fotosLugar = signal<string[]>([])`** en `detalle-viaje.ts` (sustituye a la antigua `fotosGoogle`, eliminada junto a todas sus referencias). `abrirDetalleActividad(actividad)` resetea `fotosLugar` y, si `actividad.googlePlaceId` existe, dispara una llamada rápida `obtenerFotosLugar` al abrir (sin bloquear el modal). `cerrarDetalleActividad()` vacía la señal.
  - **Cuadrícula de fotos en el detalle del plan,** justo **encima de la sección "Archivos"**: `@if (fotosLugar().length > 0)` → `<div class="grid grid-cols-3 gap-2 mb-4">` con `<img class="rounded-lg object-cover h-24 w-full shadow-sm" loading="lazy">` por foto.
  - **Cuadrícula eliminada del modal de reseñas** (el modal "Últimas reseñas" vuelve a mostrar solo las reseñas + enlace a Google Maps).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~125 kB).

### ✅ Completado en esta sesión (Horarios dinámicos del día + descripción editorial de Google + fotos en el modal de reseñas)
- [x] **Horario dinámico del día (`obtenerHorarioDia`):** nuevo método en `detalle-viaje.ts` con firma `(horariosStr: string | null | undefined, fechaActividad: Date | string): string`. Convierte la fecha a `Date` local (timezone-safe), extrae `getDay()` (0=domingo) y busca en la cadena `horarios` (formato `"Lunes: 6:30–23:00 · Martes: ..."`) la parte que empieza por el día en **español**, con **fallback al inglés** (es/ `domingo`/`sunday`, `lunes`/`monday`, ...). Devuelve solo la franja horaria (texto tras los dos puntos, ej. `6:30–23:00`) o `''` si no encuentra el día o si `horariosStr` es `null`/fecha inválida.
- [x] **Tarjeta del itinerario (`itinerario.html`):** contenedor en la esquina **inferior derecha** de cada tarjeta usando `ml-auto flex items-center justify-end gap-1`, visible solo cuando la franja existe (`@if (obtenerHorarioDia(actividad.horarios, actividad.fechaHoraInicio); as horarioDia)`). Texto pequeño `text-xs text-gray-500 dark:text-gray-400` con **icono SVG de reloj** (`M12 6v6h4.5m4.5 0a9 9 0 1 1-18 0...`) + `{{ horarioDia }}` (ej. `🕒 6:30–23:00`).
- [x] **`descripcionGoogle` en el formulario (`modal-actividad.ts`):** nuevo control `descripcionGoogle: ['']` en `formActividad`. `abrirEdicion(actividad)` lo rehidrata desde `actividad.descripcionGoogle ?? ''` y `guardarActividad()` lo envía al backend como `descripcionGoogle` (omitiendo si vacío). Reseteado con `abrirModal()`.
- [x] **Editorial summary en el autocompletado:** en la ruta **Nueva API** las `fetchFields` ahora incluyen `'editorialSummary'` y, al seleccionar el lugar, se guarda `(detalle.editorialSummary ?? '').slice(0, 2000)` en el control `descripcionGoogle` (el campo ya estaba limitado a 2000 por backend). En la ruta **legacy** (`aplicarLugarLegacy`), el `PlacesService.getDetails` ahora pide el field `editorial_summary` y se extrae `place.editorial_summary?.overview` mediante un cast tipado a `{ editorial_summary?: { overview?: string } }` (el `@types/google.maps` actual no tipa el campo legacy), guardándolo también (máx 2000).
- [x] **Tipos Backend-First (`shared/types/index.ts`):** `Actividad` añade `descripcionGoogle: string | null` y `ActividadRequest` añade `descripcionGoogle?: string` (espejo del `ActividadResponseDTO`/`ActividadRequestDTO` con el campo nuevo del backend).
- [x] **Descripción en el modal de detalle (`actividad-detalle.html`):** se pinta `actividad.descripcionGoogle` **justo debajo de la dirección** en el bloque "Ubicación" del bottom-sheet usando `text-sm italic text-gray-600 dark:text-gray-400`. El `@if` contenedor del bloque ahora incluye `|| actividad.descripcionGoogle`.
- [x] **Fotos de Google en el modal de reseñas:**
  - `GoogleMapsService.obtenerResenasGoogle(placeId)` ahora pide `fields: ['reviews', 'photos']` y devuelve `Promise<{ resenas: PlaceReview[]; fotos: string[] }>` (hasta 3 fotos con `photo.getUrl({ maxWidth: 400, maxHeight: 300 })`).
  - Nueva señal `fotosGoogle = signal<string[]>([])` en `detalle-viaje.ts`; `abrirResenas()` la puebla junto a las reseñas y `cerrarResenas()` la vacía (con `resenasGoogle`).
  - En el modal de reseñas, **debajo del listado y encima del botón "Leer más en Google Maps"**, una cuadrícula `grid grid-cols-3 gap-2 mt-4` con `<img class="h-24 w-full rounded-lg object-cover" loading="lazy">` por foto.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~125 kB).

### ✅ Completado en esta sesión (Restauración del selector de días del itinerario — botón "Todos" basado en `diaSeleccionado`)
- [x] **Verificación de integridad:** los selectores de días NO habían desaparecido del código — los chips del mini-calendario (container horizontal `scrollbar-hide -mx-4 mb-8 flex snap-x snap-mandatory gap-3 overflow-x-auto` iterando `@for (dia of dias())`) y el `<select id="dia">` del modal "Añadir plan" (con su `@for (dia of dias())` generando `<option>Día N — etiqueta</option>`) siguen presentes. La señal `dias` (computed basado en `fechaInicio`/`fechaFin` vía `generarDias`, timezone-safe) se pasa tanto a la vista como al modal.
- [x] **Botón "Todos" añadido al selector del itinerario:** nuevo primer chip del mini-calendario con la etiqueta **"Todos"** (subtexto **"N días"** con `dias().length`). Usa `seleccionarDia(null)`.
- [x] **Lógica de `diaSeleccionado` reforzada:** `seleccionarDia(valor)` ahora acepta `string | null` (corresponde a la señal `diaSeleccionado = signal<string | null>(null)`). El resaltado activo de los chips (fondo azul, texto blanco) y el `class` condicional pasan de basarse en `diaActivo()` a basarse en **`diaSeleccionado()`** (el chip "Todos" se ilumina cuando `diaSeleccionado() === null`, cada día cuando coincide con su `.valor`).
- [x] **Timeline con filtro null:** `actividadesDelDia` cuando `diaSeleccionado()` es `null` devuelve **todas las actividades del viaje** (visión completa); con un día concreto filtra por `fechaHoraInicio.startsWith(dia)` como antes. `diaActivo()` se conserva intacto como fallback al primer día (lo usa el modal para preseleccionar el `dia` al abrir "Añadir plan").
- [x] **Empty-state dinámico:** el mensaje "Este día aún no tiene planes / Añade tu primera actividad para este día" se vuelve condicional — con "Todos" activo se muestra "Este viaje aún no tiene planes / Añade tu primera actividad al viaje".
- [x] **Sin tocar Google Places:** el autocompletado, la captura de metadatos y el modal de reseñas quedan intactos.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~123 kB).

### ✅ Completado en esta sesión (Magia de UX — título inteligente + modal de reseñas de Google)
- [x] **Título Inteligente (autocompletado de Google Places):** en `seleccionarLugar()` (Nueva API) se pide ahora el campo `displayName` además de `id`/`formattedAddress`/`location`/`rating`/`regularOpeningHours`; en el fallback legado (`aplicarLugarLegacy`) se usa `lugar.name`. El helper nuevo `autorellenarTitulo(nombreLugar)`: si el control `titulo` del formulario está **vacío o aún `pristine`** (no tocado por el usuario), hace `patchValue` inyectando el nombre del lugar (truncado a 200). El usuario siempre puede sobreescribirlo tecleando (pasa a `dirty` y deja de auto-rellenarse).
- [x] **`GoogleMapsService.obtenerResenasGoogle(placeId): Promise<PlaceReview[]>`:** nuevo método que, tras `cargar()`, crea un `google.maps.places.PlacesService` con un elemento HTML dummy y llama a `getDetails({ placeId, fields: ['reviews'] })`. Devuelve el array de `reviews` o rechaza si `getDetails` no devuelve `OK`. Tipado estricto con `google.maps.places.PlaceReview` (evitando el self-reference TS7022 con un cast a `typeof google`).
- [x] **Modal de reseñas (`detalle-viaje.html/ts`):**
  - Señales nuevas: `resenasGoogle` (`PlaceReview[]`), `mostrarResenas`, `cargandoResenas`, `errorResenas` y `actividadResenas` (la actividad de contexto). Métodos `abrirResenas(actividad)` (guarda `googlePlaceId`, abre el modal, recarga máximo 5 reseñas con fallback a mensaje de error) y `cerrarResenas()`.
  - **Rating clicable en la tarjeta del itinerario:** chip ámbar `★ X.X` con `cursor-pointer hover:bg-gray-100`, visible si `actividad.rating != null && actividad.googlePlaceId`, con `$event.stopPropagation()` y `(click)="abrirResenas(actividad)"`.
  - **Rating clicable en el bottom-sheet de detalle:** mismo chip convertido en `<button>` clicable (añade el hint "· reseñas" si hay `googlePlaceId`).
  - **Diseño del modal (bottom-sheet `z-[60]`, `max-h-[85vh]` scroll):** título **"Últimas reseñas"** + nombre de la actividad; spinner "Cargando reseñas..." mientras dura la petición; `@for` sobre `resenasGoogle` mostrando avatar-inicial del autor (`author_name`), fila de estrellas `★`/`☆` (helper `estrellasResena`) y el extracto `text`; `@empty` "Este lugar aún no tiene reseñas en Google"; banner rojo si falla. Botón inferior **"Leer más en Google Maps"** (`target="_blank"` apuntando a `actividad.urlGoogleMaps`) + botón "Cerrar".
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~122 kB).

### ✅ Completado en esta sesión (Integración de Google Places en Actividad — metadatos del lugar y eliminación de la "Ubicación" libre)
- [x] **Tipos (Backend-First mirror):** `Actividad` pierde `descripcion` (el texto libre "Ubicación") y gana `googlePlaceId`, `rating`, `horarios` y `urlGoogleMaps` (todos `string | null` / `number | null`, espejo exacto del `ActividadResponseDTO`). `ActividadRequest` idem: `descripcion?: string` eliminada, añadidos los cuatro campos opcionales.
- [x] **Campo "Ubicación" eliminado del modal "Añadir plan":** el control `ubicacion` de `formActividad` (y su `<input>` del HTML) se borró por completo. La dirección real del lugar sigue capturándose con el **autocompletado Google Places** vía el campo `direccion` + `latitud`/`longitud`.
- [x] **Captura de metadatos con el autocompletado:** nueva señal `metadatosGoogle` (`{googlePlaceId, rating, horarios, urlGoogleMaps}`) que se rellena al seleccionar un lugar del buscador:
  - **Nueva API (`Place.fetchFields`):** se piden `id`, `rating` y `regularOpeningHours` además de `formattedAddress`/`location`; `horarios` = `weekdayDescriptions.join(' · ')` (máx 500).
  - **Fallback legado (`getDetails`):** se capturan `place_id`, `lugar.rating` y `lugar.opening_hours.weekday_text`.
  - **`urlGoogleMaps`** siempre se construye de forma determinista (`https://www.google.com/maps/search/?api=1&query=lat,lng&query_place_id=...` con 5 decimales en las coordenadas) en el helper `urlGoogleMapsPara`.
  - **Edición conservada:** `abrirEdicion(actividad)` rehidrata la señal desde la actividad (sobrevive al PUT sin re-seleccionar el lugar) y `abrirModal()` la resetea.
- [x] **`guardarActividad`:** el request ya no envía `descripcion`; ahora incluye los cuatro campos Google desde la señal (solo si no son `null`).
- [x] **UI de mostrar:** la tarjeta del timeline y el bottom-sheet "Detalle del Plan" sustituyen `.descripcion` por `.direccion`. El detalle añade un chip `★ rating`, los `horarios` (múltiples líneas) y un botón **"Ver en Google Maps"** (`target="_blank"`) cuando existen. `hasError` ya no trata `('ubicacion')`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~115 kB).

### ✅ Completado en esta sesión (Perfil y Ajustes del usuario — cambio de contraseña, toggles de notificaciones y centralización del modo oscuro)
- [x] **`UsuarioService`** (nuevo, `core/services/usuario.service.ts`, `providedIn: 'root'`): consume los endpoints del perfil del backend (mirror del `UsuarioController`):
  - `obtenerPerfil()` → `GET /api/usuarios/me` devuelve `UsuarioPerfil`.
  - `cambiarPassword(datos)` → `PUT /api/usuarios/me/password` (body `CambiarPasswordRequest`; 204).
  - `actualizarPreferencias(prefs)` → `PUT /api/usuarios/me/preferencias` (body `PreferenciasNotificacionRequest`) devuelve el `UsuarioPerfil` actualizado.
- [x] **Tipos Backend-First** en `shared/types/index.ts`: `UsuarioPerfil` (`id`, `email`, `nombre`, `avatarUrl`, `notificacionesGastos`, `notificacionesPlanes` — espejo del `UsuarioResponseDTO`), `CambiarPasswordRequest` (`passwordActual`, `nuevaPassword` — espejo del `CambiarPasswordRequestDTO`) y `PreferenciasNotificacionRequest` (`notificacionesGastos`, `notificacionesPlanes` — espejo del `PreferenciasNotificacionRequestDTO`).
- [x] **Tarjeta principal del Perfil (`perfil.ts`/`perfil.html`):** al inicializar el componente se llama `obtenerPerfil()` y se guarda en la señal `perfil`. El nombre y el email reales del usuario se pintan desde `perfil()` (fallback a la sesión localStorage vía `AuthService.usuario` y "Invitado"). El avatar muestra `perfil()?.avatarUrl ?? usuario()?.avatarUrl`; tras subir una foto nueva se refresca el perfil con `refrescarPerfil()`.
- [x] **Sección/panel "Cuenta":** la fila del menú ahora es un `<button>` que despliega un panel (acordeón, flecha rotada). Formulario reactive con `passwordActual` y `nuevaPassword` (min 6, max 255). `cambiarPassword()` valida, llama a `cambiarPassword` del servicio y distingue éxito ("Contraseña actualizada correctamente." en verde) de error (mensaje del backend extraído con `extraerMensajeError`, p.ej. "La contrasena actual no es correcta" → 400). Estados `guardandoPassword` (spinner + "Guardando..."), `errorPassword` y `passwordOk`.
- [x] **Sección/panel "Notificaciones":** la fila despliega dos **toggles estilo iOS/Tailwind** (`role="switch"`, pista `h-7 w-12 rounded-full` + perilla `h-5 w-5` con `translate-x-5`) enlazados a las señales `notificacionesGastos` y `notificacionesPlanes`. Un `effect()` sincroniza ambas señales desde `perfil()` al cargar. Cada cambio llama a `actualizarPreferencias()` para persistir en el backend al instante (estados `guardandoPreferencias` "Guardando…" y `errorPreferencias`).
- [x] **Centralización del modo oscuro:** eliminado el botón Sol/Luna (icono `aria-label="Cambiar tema"`) de las cabeceras de **Dashboard** (`dashboard.html`/`dashboard.ts`: se retiró `ThemeService`, `theme` y `toggleTheme()`; queda solo el botón "Unirse") y de **Finanzas/Gastos** (`finanzas.html`/`finanzas.ts`: mismo limpiado). El toggle del tema vive ahora ÚNICAMENTE en la cabecera superior derecha de `perfil.html` (no existe vista `diccionario` — solo enlace de la bottom nav — por lo que no requería cambios).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `perfil` regenerado ~19.5 kB, `finanzas` ~31.1 kB y `dashboard` ~15.3 kB).

### ✅ Completado en esta sesión (Fix contraste modo claro — previsualización del reparto)
- [x] **`finanzas.html`:** la tarjeta de "Previsualización del reparto" del modal de gastos ya no usa el fondo azul oscuro opaco `bg-blue-900/20 text-blue-200` (ilegible en modo claro). Ahora aplica variantes `dark:`:
  - Contenedor: `bg-blue-50 border border-blue-100 p-3 rounded-lg` + `dark:bg-blue-900/20 dark:text-blue-200 dark:border-none`.
  - Título: `text-blue-800` + `dark:text-blue-200`.
  - Línea del pagador: `text-emerald-700` + `dark:text-emerald-400` (se retiró la clase `text-white` de los importes, sustituida por herencia con `font-semibold`).
  - Línea de los deudores: `text-gray-700` + `dark:text-gray-300` (se eliminó el `text-white` de los importes).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado).

### ✅ Completado en esta sesión (Login con acceso rápido de desarrollo)
- [x] **`login.ts`:** eliminado el método antiguo `autofillDev()` (demo `demo@viajes.dev`/`demo123`). Nuevo método **`accesoRapido(usuario: 'javi' | 'cynthia')`** que rellena el formulario con `patchValue` (Javi → `javi@viajes.com` / `password`; Cynthia → `cynthia@viajes.com` / `password`) e invoca inmediatamente `onSubmit()` para un **login de un solo clic** sin interacción adicional.
- [x] **`login.html`:** reemplazado el botón "Rellenar demo" por un contenedor flex (`flex flex-wrap gap-4 justify-center`) con dos botones compactos: **"Entrar como Javi"** (azul destacado `bg-blue-600`) y **"Entrar como Cynthia"** (gris neutro), ambos `type="button"` y conectados a `accesoRapido('javi')` / `accesoRapido('cynthia')`.
- [x] **Alineación con el backend:** las credenciales coinciden con el `DatabaseSeeder` de Spring Boot (`javi@viajes.com` y `cynthia@viajes.com`, contraseña `password` hasheada con BCrypt), por lo que el acceso rápido funciona en cuanto arranca el backend con la BD vacía.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `login` regenerado).

### ✅ Completado en esta sesión (UX rename "Finanzas" → "Gastos" + limpieza de código)
- [x] **Renombrado solo de UI:** todo el texto visible "Finanzas" se sustituyó por **"Gastos"**: título `<h1>` de la cabecera de la vista (con su subtítulo "Controla tus gastos del viaje", que ya existía), la etiqueta de la **Bottom Navigation Bar** (`main-layout.html`) y la sección "Mis Finanzas" del presupuesto individual en Ajustes del viaje → **"Mis Gastos"**. Se respetaron íntegramente nombres técnicos: rutas (`/gastos` ya existía), selector `app-finanzas`, ficheros `finanzas.*`, clase `Finanzas` y la clave de localStorage `finanzas_pagadores`.
- [x] **Limpieza de código muerto:** revisado a fondo `finanzas.ts` y `finanzas.html` — **cero importaciones sin uso** (todos los símbolos: `toSignal`, `CurrencyPipe`, `startWith`, `DeudaResumen`, `DetalleDivision`, `TipoDivision`, `Viaje`, etc. están referenciados) y **cero rastros de `DEUDA_PAGADOR`** (grep global sin resultados). No fue necesario borrar nada.
- [x] **Verificación de la tarjeta azul ("Mis Gastos"):** el computed `totalGastadoVista()` sigue delegando en `balance().totalGastadoUsuario` para la pestaña PERSONAL; como el backend ahora envía exclusivamente la suma de gastos `PERSONAL` del usuario (fix `fix(backend): implement cascade delete for expenses and fix personal total calculation`), la interfaz cuadra automáticamente sin cambios de lógica.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado, ~32.2 kB).

### ✅ Completado en esta sesión (Refactor de deudas — limpieza `DEUDA_PAGADOR`, fix matemático de porcentajes y previsualización estructurada)
- [x] **Backend-consistente (Tipo `TipoDivision`):** eliminado el valor `DEUDA_PAGADOR` de `shared/types/index.ts`. El tipo queda `'PARTES_IGUALES' | 'PORCENTAJE' | 'DEUDA_TERCERO'` (espejo del enum Java ya simplificado).
- [x] **Selector "Tipo de división" (`finanzas.html`):** eliminada la `<option>` de `DEUDA_PAGADOR`. El texto de la opción `DEUDA_TERCERO` ahora dice **"Se debe la cantidad total"** (más claro que "Deuda a un tercero").
- [x] **Lógica condicional simplificada (`finanzas.ts` + `finanzas.html`):** todas las comprobaciones que combinaban `DEUDA_PAGADOR || DEUDA_TERCERO` (`previewDeudas`, `puedeGuardar`, `abrirEdicion`, `construirDetalles` y el bloque `<select>` del deudor del HTML) ahora comprueban solo `DEUDA_TERCERO`. La etiqueta del selector de deudor queda fija como "Quién asume el 100% de la deuda".
- [x] **`previewDeudas` reestructurada (computed en `finanzas.ts`):** ahora devuelve `{ pagador: { nombre, importe }, deudores: { nombre, importe }[] } | null` (null si no hay datos válidos). El nombre del pagador se extrae del formulario (`pagadorId`) y su `importe` es el importe total del gasto. **Fix matemático de PORCENTAJES:** la parte de cada deudor se calcula con la fórmula estricta `(importeTotal * porcentajeAsignado) / 100` (sin el redondeo doble anidado anterior). Se elimina el computed `pagadorPreview` (absorbido por el `pagador` del objeto).
- [x] **Previsualización del reparto (`finanzas.html`):** el cuadro azul `bg-blue-900/20` ahora muestra primero la línea verde "[Pagador] paga [Importe Total]€" (`text-emerald-300`) y, debajo, el bucle `@for` sobre `reparto.deudores` con "[Deudor] deberá [Importe calculado]€".
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado, ~32.2 kB).

### ✅ Completado en esta sesión (Liquidación simple de deudas — gasto inverso reutilizando el modal)
- [x] **Botón "Liquidar" en `finanzas.html`:** en cada fila de la tarjeta "Resumen de Deudas", justo al lado del texto "[deudor] debe [importe] a [acreedor]", se añadió un botón pill muy simple — `<button (click)="prepararLiquidacion(deuda)" class="ml-auto text-xs bg-emerald-500/20 text-emerald-400 px-3 py-1 rounded-full hover:bg-emerald-500/30 transition-colors">Liquidar</button>`. No se tocó ni ocultó la lógica `@if` del resto del formulario (los selectores de tipo/división siguen visibles y pre-rellenados).
- [x] **`prepararLiquidacion(deuda: DeudaResumen)` en `finanzas.ts`:** la forma más simple posible — (a) `gastoEnEdicion.set(null)` (gasto nuevo, nunca edición), (b) `formGasto.patchValue({ concepto: 'Liquidación / Bizum', importe: deuda.importe, pagadorId: deuda.deudorId, tipoGasto: 'COMPARTIDO', tipoDivision: 'DEUDA_TERCERO' })` (quien debía paga ahora → `pagadorId = deudorId`), (c) `deudorSeleccionado.set(deuda.acreedorId)` (el 100% se asigna al acreedor), (d) `modalAbierto.set(true)`.
- [x] **Cómo se salda la deuda en el backend:** al guardar, `crearGasto` (POST) registra el gasto inverso con DEUDA_TERCERO → el balance compensa las deudas cruzadas entre la pareja y queda a cero. La sesión anterior (no commitada) que ocultaba selectores con `esLiquidacion` quedó **revertida** para mantener este enfoque directo.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado, ~32.3 kB). Commit `6199ff6`.

### ✅ Completado en esta sesión (Edición y borrado de gastos + previsualización dinámica del reparto)
- [x] **`GastoService`:** nuevos métodos `actualizarGasto(viajeId, gastoId, gasto)` → `PUT /api/viajes/{viajeId}/gastos/{gastoId}` (devuelve la `Gasto` actualizada con el backend recalculando las deudas) y `eliminarGasto(viajeId, gastoId)` → `DELETE /api/viajes/{viajeId}/gastos/{gastoId}` (204). El backend ya soportaba ambos endpoints (`feat(backend): implement edit and delete logic for expenses with debt recalculation`).
- [x] **UI de Borrado:** en cada tarjeta de gasto hay ahora un botón de **papelera** en la esquina superior derecha (rojo, `absolute right-3 top-3`, `$event.stopPropagation()`). Al pulsarlo lanza `confirm('¿Seguro que quieres borrar este gasto? Esta acción recalculará las deudas.')` y, si acepta, llama a `gastoService.eliminarGasto` → tras el éxito recarga `cargarGastos` **y** `cargarBalance`. Errores en la nueva señal `errorBorrar` (banner rojo sobre la lista).
- [x] **Modal Reutilizable de Edición:**
  - Nueva señal `gastoEnEdicion = signal<Gasto | null>(null)`. Hacer clic en **cualquier tarjeta de gasto** abre el modal en modo edición (`abrirEdicion(gasto)`): `patchValue`/`reset` del `formGasto` con `concepto`, `importe`, `tipoGasto`, `tipoDivision` y `pagadorId` reales.
  - **PORCENTAJE:** en edición se reconstruyen los porcentajes desde las `deudas` actuales del gasto (importe deuda / importe total, el pagador a 0) y se ajusta el último para que sumen exactamente 100; sin deudas se aplica el reparto equitativo. **DEUDA_PAGADOR/DEUDA_TERCERO:** se preselecciona el deudor actual (`deudas?.[0].deudorId`).
  - Título y subtítulo del modal dinámicos: "Nuevo gasto"/"Registra un gasto…" vs "Editar gasto"/"Modifica los datos del gasto y el reparto se recalculará". Botón de submit: "Guardar gasto" vs **"Guardar cambios"** (con "Guardando..." mientras dura la petición).
  - `guardarGasto()` bifurca: con `gastoEnEdicion()` → `actualizarGasto` (PUT), sin él → `crearGasto` (POST). Al terminar cierra el modal, limpia `gastoEnEdicion` y recarga gastos + balances. El mensaje de error distingue "actualizar" de "registrar".
- [x] **Previsualización de Deudas en tiempo real:**
  - Señal `estadoFormulario = toSignal(formGasto.valueChanges.pipe(startWith(getRawValue())))` (`@angular/core/rxjs-interop`) como fuente reactiva del formulario.
  - `previewDeudas` (computed): replica la lógica de división del backend para gastos COMPARTIDO — `PARTES_IGUALES` (reparto con remanente de céntimos al último deudor), `PORCENTAJE` (% por participante) y `DEUDA_PAGADOR`/`DEUDA_TERCERO` (100% al único deudor). Aplica la **REGLA DE ORO**: el pagador nunca aparece como deudor. `pagadorPreview` (computed) resuelve el nombre del pagador del formulario.
  - En `finanzas.html`, justo encima del botón de guardar y solo cuando `tipoGasto === 'COMPARTIDO'`, el formulario es válido y hay deudas calculadas, se muestra el cuadro `bg-blue-900/20 text-blue-200 p-3 rounded-lg text-sm` con la lista "[Deudor] deberá [Importe €] a [Pagador]".
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado, ~31.7 kB).

### ✅ Completado en esta sesión (Etiquetas estilo Splitwise en la lista de gastos)
- [x] **Tipo `DetalleDeuda`** en `shared/types/index.ts` (`deudorId`, `deudorNombre`, `importe`) como espejo del `DetalleDeudaDTO` del backend. La interfaz `Gasto` añade `pagadorId?: UUID` (opcional, el backend ya envía el UUID real) y `deudas?: DetalleDeuda[]`.
- [x] **`getResumenGasto(gasto)` (método en `finanzas.ts`):** analiza cada gasto desde la perspectiva del usuario logueado y devuelve `{ texto, importe, color }`:
  - **Prestador (`soyPagador`):** suma todas las `deudas` del gasto → `totalPrestado`. Si `> 0` → `{ 'prestaste', total, 'text-emerald-500' }` (verde Splitwise). Si `=== 0` → `{ 'pagaste', gasto.importe, 'text-gray-400' }`.
  - **Deudor (`miDeuda` existe):** → `{ 'pediste', miDeuda.importe, 'text-orange-500' }` (naranja Splitwise).
  - **Ajeno (ni pagador ni deudor):** → `{ 'no participas', 0, 'text-gray-500' }`.
  - `miUsuarioId` (nuevo computed) se resuelve cruzando `participantes()` por email con `AuthService.usuario`, igual que `usuarioIdPropio` en el detalle del viaje.
  - `nombrePagador`/`inicialPagador` aceptan ahora `UUID | undefined` al volverse `pagadorId` opcional (misma firma en `GastoService.nombrePagador`).
- [x] **UI Splitwise en `finanzas.html`:** en cada tarjeta del `@for` de gastos se usa `@let resumen = getResumenGasto(gasto)`. El importe total pasa a `text-sm font-semibold text-gray-400 dark:text-gray-500` (menos protagonismo) y debajo se pinta la etiqueta `text-xs font-bold` con `[class]="resumen.color"`: "prestaste X€" en verde, "pediste Y€" en naranja, "pagaste Z€" o "no participas" en gris (sin importe cuando es 0).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado, ~25.6 kB). Con la corrección del backend (el pagador ya no aparece como deudor de su propio gasto), el resumen queda consistente con la tarjeta de "Resumen de Deudas".

### ✅ Completado en esta sesión (Resumen de Deudas + consolidación de cambios pendientes)
- [x] **`deudasVisibles` (computed en `finanzas.ts`):** devuelve `balance()?.deudas ?? []` SOLO cuando `vistaActiva() === 'GRUPO'` (si no, lista vacía). Se importa el tipo `DeudaResumen` en el componente.
- [x] **Nueva tarjeta "Resumen de Deudas" en `finanzas.html`:** renderizado condicional (únicamente en vista GRUPO y con ≥1 deuda) justo debajo del toggle "Gastos del Grupo / Mis Gastos". Tarjeta `rounded-3xl bg-white dark:bg-gray-800 shadow-lg` con título uppercase. Cada deuda (`@for` con `track deuda.deudorId + '|' + deuda.acreedorId`) se pinta en una fila `rounded-2xl` con **icono de billete** (Heroicons en círculo índigo) y el texto "[Deudor] debe [Importe € en rojo] a [Acreedor]" usando `CurrencyPipe`. Ladrillo limpio, táctil y mobile-first.
- [x] **Consolidación de la sesión anterior** (pendiente sin commitear): el arreglo del `pagadorId` real (`participante.usuarioId` en los selectores del modal de nuevo gasto) y la **UI del presupuesto individual** en la pestaña Ajustes (`ViajeService.actualizarPresupuestoIndividual` → `PUT /api/viajes/{id}/presupuesto-individual`, sección "Mis Finanzas" con input + botón y estados `guardandoPresupuesto`/`errorPresupuesto`/`presupuestoOk`). El backend ya devuelve correctamente `presupuestoBase` en balances, por lo que la tarjeta azul se actualiza sola.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado, ~24.7 kB). Commit `592bd87`: `feat(frontend): render debts summary ui, fix expense UUID mapping and finalize individual budget`.

### ✅ Completado en esta sesión (Fase 4: Modal avanzado de creación de gastos con reglas de división)
- [x] **Formulario reactivo ampliado (`finanzas.ts`):** `formGasto` pasa a 5 controles con tipos Backend-First — `concepto` (requerido, máx 200), `importe` (numérico ≥0, sustituye a `cantidad`), `tipoGasto` (`'COMPARTIDO' | 'PERSONAL'`, requerido), `tipoDivision` (`TipoDivision`) y `pagadorId` (requerido, ahora un **UUID real de participante** cargado desde `GET /api/viajes/{id}/participantes`, no el email). Se elimina el uso de `cantidad`/`pagadorAutenticado`.
- [x] **Participantes disponibles:** `Finanzas` expone ahora `participantes = this.viajeService.participantes`; se dispara `cargarParticipantes(viajeId)` en el `effect()` de auto-selección del primer viaje y en `seleccionarViaje(id)`. El selector "Pagado por" itera sobre `participantes()` con el `id` real.
- [x] **Toggle Tipo de gasto (Compartido/Personal):** por defecto arranca en la **vista activa** del usuario (`vistaActiva() === 'PERSONAL'` → PERSONAL, si no COMPARTIDO). `cambiarTipoGasto(tipo)` resetea división/deudor al pasar a PERSONAL (el backend fuerza `tipoDivision=null` en personales).
- [x] **Lógica condicional de división (COMPARTIDO):**
  - Selector `Tipo de división`: PARTES_IGUALES, PORCENTAJE, DEUDA_TERCERO, DEUDA_PAGADOR. `cambiarTipoDivision(division)` limpia el deudor seleccionado.
  - **PORCENTAJE:** bucle `@for` sobre `participantes()` con avatar (imagen si `avatarUrl`, si no inicial) + input numérico por participante. Señal `porcentajes` (`Partial<Record<UUID, number>>`, inicializado a reparto equitativo con remanente en el último para sumar 100). Computeds `sumaPorcentajes` (reduce<number> + `valor ?? 0`) y `porcentajesValidos` (≈100, tolerancia 0.001). UI: contador "Suma {X} / 100" verde/rojo y aviso rojo si no suma 100.
  - **DEUDA_TERCERO / DEUDA_PAGADOR:** `<select>` nativo con participantes; `deudorSeleccionado` (signal) + método `seleccionarDeudor(id)`. Aviso rojo si no hay deudor.
- [x] **`puedeGuardar` (computed):** desactiva el botón Guardar si el form es inválido, si guardando, si PORCENTAJE no suma 100 o si DEUDA_* no tiene deudor seleccionado. El botón usa `[disabled]="!puedeGuardar()"`.
- [x] **Lógica de guardado (`guardarGasto`):** construye el payload `GastoRequest` con `importe`, `pagadorId` real, `tipoGasto` y `tipoDivision` (omite `tipoDivision` en PERSONAL). `construirDetalles()` genera `detalles: DetalleDivision[]` solo cuando aplica: PORCENTAJE → `{usuarioId, valorAsignado()}` por cada participante con % > 0; DEUDA_PAGADOR/TERCERO → `[{usuarioId: deudor, valorAsignado: 100}]`; PARTES_IGUALES y PERSONAL → `undefined` (backend reparte solo).
- [x] **Reactividad tras el POST:** al recibir éxito se llama `cargarGastos(viajeId)` **y** `cargarBalance(viajeId)` para que la tarjeta azul y la lista se actualicen al instante; se cierra el modal y se resetea el form. Modal con `max-h-[90vh] overflow-y-auto` (scroll en pantallas pequeñas).
- [x] **Verificación:** `ng build` compila sin errores ni warnings (lazy chunk `finanzas` ~22.9 kB). Commit `d684cce`: `feat(frontend): Implement dynamic expense creation modal with splitting rules`. Backend ya listo (`4723816`).

### ✅ Completado en esta sesión (Fase 3: Finanzas reactivas — selector de contexto Grupo/Personal con balances)
- [x] **Tipos (Backend-First mirror):** `Gasto` refleja ahora el `GastoResponseDTO` real (`importe` sustituye a `cantidad`, con `fecha`, `tipoGasto`, `tipoDivision` nullable y `pagadorNombre`). Nuevos enums `TipoGasto` (`PERSONAL`/`COMPARTIDO`) y `TipoDivision` (`PARTES_IGUALES`, `PORCENTAJE`, `DEUDA_PAGADOR`, `DEUDA_TERCERO`). `GastoRequest` ampliado con `importe`, `tipoGasto`, `tipoDivision` y `detalles: DetalleDivision[]` (`usuarioId`, `valorAsignado`). Nuevo `BalanceResponse` (espejo del `BalanceResponseDTO` backend): `presupuestoGlobal`, `totalGastadoGrupo`, `presupuestoIndividualUsuario`, `totalGastadoUsuario` y `deudas: DeudaResumen[]` (`deudorId`, `deudorNombre`, `acreedorId`, `acreedorNombre`, `importe`).
- [x] **`GastoService`:** nueva señal `balance = signal<BalanceResponse | null>(null)`, método `getBalances(viajeId)` → `GET /api/viajes/{viajeId}/balances` y `cargarBalance(viajeId)` (limpia la señal con `null` en error).
- [x] **`Finanzas` reactivo:**
  - Señal `vistaActiva = signal<'GRUPO' | 'PERSONAL'>('GRUPO')` + método `cambiarVista(vista)` para el selector.
  - Computeds por vista: `gastosVisibles` (GRUPO → `tipoGasto === 'COMPARTIDO'`, PERSONAL → `tipoGasto === 'PERSONAL'`), `totalGastadoVista` (del balance o 0), `presupuestoVista` (GRUPO → `presupuestoGlobal` del balance con fallback a `presupuestoBase`; PERSONAL → `presupuestoIndividualUsuario`), `porcentajeVista` (0-100 con tope) y `totalGastosFiltrados`.
  - `cargarBalance` se dispara en el `effect()` de auto-selección del primer viaje, en `seleccionarViaje(id)` y tras guardar un gasto (junto a `cargarGastos`).
  - `guardarGasto` migra al contrato backend: `importe` (desde `cantidad`), `tipoGasto: 'COMPARTIDO'` y `tipoDivision: 'PARTES_IGUALES'` (con el pagador autenticado vía `pagadorAutenticado`).
- [x] **Template `finanzas.html`:** la tarjeta azul es dinámica ("Gastado (Grupo)"/"Gastado (Tuyo)") con los valores y la barra de progreso del balance según `vistaActiva()`; el contador inferior usa `totalGastosFiltrados()`. Nuevo **toggle pills** (contenedor `bg-gray-100 dark:bg-gray-800`, botón activo `bg-white text-blue-600 shadow-md dark:bg-gray-900 dark:text-blue-400`) "Gastos del Grupo" / "Mis Gastos" bajo la tarjeta. El listado itera `gastosVisibles()` y muestra `importe`; el pagador prioriza `gasto.pagadorNombre` con fallback a `nombrePagador()`.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `finanzas` regenerado, ~15.6 kB). Commit `cec3fb3`: `feat(frontend): Implement reactive finance dashboard with context toggle`. Backend ya listo (`4723816`).

### ✅ Completado en esta sesión (Avatares reales en cabecera, participantes y galería)
- [x] **Tipos:** `Participante` añade `avatarUrl?: string` y `Foto` añade `usuarioAvatarUrl?: string` (espejo de `ParticipanteResponseDTO` y `FotoResponseDTO` del backend, que ya propagan el `avatarUrl`/`usuarioAvatarUrl`).
- [x] **Cabecera de `detalle-viaje`:** el círculo del usuario logueado ahora tiene renderizado condicional con `@if (usuarioSesion()?.avatarUrl)` → `<img class="h-full w-full object-cover">` dentro del círculo `overflow-hidden rounded-full`; si no hay avatar mantiene la inicial. Mantiene `aria-label`/`title` con el nombre.
- [x] **Lista de Participantes (pestaña Ajustes):** mismo patrón por participante — si `participante.avatarUrl` → `<img>` (`h-full w-full object-cover` + círculo `overflow-hidden`); si no, la inicial. `alt` con "Foto de {nombre}".
- [x] **Galería de fotos:** el badge de autor del muro (`bottom-1.5 right-1.5`) pinta ahora `foto.usuarioAvatarUrl` como mini-avatar (`h-6 w-6`, `bg-black/55` como fallback visual) o la inicial si no hay avatar.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~110.5 KB).

### ✅ Completado en esta sesión (Avatar de usuario interactivo en Perfil — subida a Cloudflare R2)
- [x] **`AuthService.uploadAvatar(file)`** → `POST /api/usuarios/me/avatar` con `FormData` (campo `file`). Al recibir el `UsuarioResponseDTO` actualizado, `actualizarAvatar(avatarUrl)` **actualiza la Signal `_usuario`** (`{...usuario, avatarUrl}`) y persiste en `localStorage` (`auth_usuario`), de modo que la nueva foto se propaga por toda la app sin recargar. Nuevo método privado + interfaz `UsuarioSesion` ampliada con `avatarUrl: string | null`.
- [x] **Persistencia del avatar completa:** `saveAuth` (login/registro) guarda `avatarUrl` desde el `AuthResponse` (backend ya lo devuelve) y `cargarUsuarioSesion` normaliza `avatarUrl ?? null` para sesiones antiguas sin el campo. Tipo `AuthResponse` ampliado con `avatarUrl?: string | null`.
- [x] **`Perfil.onFileSelected(event)`:** recolecta el archivo de un `input type="file"` oculto, resetea `input.value` y llama a `uploadAvatar`. Estados `subiendoAvatar` (spinner) y `errorAvatar` (mensaje inline en rojo).
- [x] **Avatar interactivo en `perfil.html`:** el círculo central pasa a ser un `<button>` clickeable (`cursor-pointer`, `group-hover:scale-105`, `ring-4 ring-white dark:ring-gray-800`, `active:scale-95`) que al pulsarlo dispara el input oculto `#avatarInput` (`accept="image/*"`). Renderizado condicional: si `usuario()?.avatarUrl` existe → `<img class="h-full w-full object-cover rounded-full">` dentro del círculo (`overflow-hidden`); si no, mantiene el diseño con la inicial. Overlay de subida (`bg-black/40` + spinner) mientras dura la petición.
- [x] **Icono de cámara superpuesto** (Heroicons, `pointer-events-none`) en la esquina inferior-derecha del avatar (`h-9 w-9 rounded-full bg-blue-600`, anillo blanco/oscuro, hover azul claro) para dar *feedback* visual de que es editable.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `perfil` regenerado, ~10.3 kB).

### ✅ Completado en esta sesión (Pulido UI — cabecera, portada en ajustes y tarjetas del dashboard con foto)
- [x] **Cabecera de `detalle-viaje`:** se retiraron los botones flotantes de "Compartir/Invitar" y "Eliminar" de la derecha (el flujo de invitar vive ahora en la pestaña Ajustes → "+ Invitar", y el de eliminar en Ajustes → Zona de peligro). En su lugar hay un **avatar de usuario**: círculo `h-10 w-10 rounded-full` con gradiente azul oscuro (`from-blue-800 to-indigo-950`), la **inicial del usuario autenticado** centrada (vía `usuarioSesion()` de `AuthService` + helper `inicial`) y `aria-label`/`title` con el nombre. Accesible y coherente con el Perfil.
- [x] **Portada en la pestaña Ajustes:** el `<img>` de `fotoPortada` usa ahora las clases `h-32 w-full object-cover` (contenedor `relative w-full overflow-hidden rounded-xl`) para ocupar todo el ancho sin bandas blancas laterales y recortar el exceso proporcionalmente; el placeholder "Sin portada" adopta la misma altura `h-32`.
- [x] **Tarjeta del Dashboard con portada de fondo:**
  - El `<article>` pasa a ser `relative overflow-hidden rounded-3xl` con **dos capas absolutadas** cuando `viaje.fotoPortada`: fondo `bg-cover bg-center` con `[style.background-image]="'url(' + viaje.fotoPortada + ')'"` y, encima, un **degradado oscuro de legibilidad** (`bg-gradient-to-t from-black/90 via-black/50 to-transparent`).
  - Accesibilidad: con portada el texto pasa a blanco (título, fechas, presupuesto), el badge de rol y el presupuesto usan fondos translúcidos con `backdrop-blur-sm` (`bg-white/20`, `bg-black/30 ring-1 ring-white/15`) y el botón de eliminar/abandonar usa `bg-white/20` con iconos rojos claros; todo legible sobre la imagen.
  - Sin portada: se conserva el diseño oscuro/claro original (`bg-white`/`dark:bg-gray-800` con su borde y sombra grises).
  - Se añadió `NgClass` a los imports de `Dashboard` para el binding condicional de clases.
- [x] **Verificación:** `ng build` compila sin errores (chunks `detalle-viaje` ~110 KB y `dashboard` regenerados).

### ✅ Completado en esta sesión (Panel de Ajustes del viaje — portada, datos, participantes y danger zone)
- [x] **Backend ya listo:** el panel consume el endpoint Settings del backend (`feat(backend): Add trip settings endpoints`): `PUT /api/viajes/{id}` (titulo/fechas), `POST /api/viajes/{id}/portada` (multipart `file`) y `DELETE /api/viajes/{id}/participantes/{usuarioId}` (expulsar). Ambos PUT/POST devuelven `ViajeResponseDTO` que ahora incluye `fotoPortada` y `participantes[]`.
- [x] **Tipos:** `Viaje` ampliado con `fotoPortada: string | null` y `participantes: Participante[]` (espejo del `ViajeResponseDTO`). Nuevo tipo `ViajeUpdateRequest` (`titulo`, `fechaInicio`, `fechaFin`) espejo del `ViajeUpdateDTO`.
- [x] **`ViajeService`:** nuevos métodos `updateViaje(id, ViajeUpdateRequest)` → `PUT`, `uploadPortada(id, File)` → `POST multipart` con campo `file`, y `kickParticipante(viajeId, usuarioId)` → `DELETE`.
- [x] **Nueva pestaña "Ajustes"** en el detalle del viaje: tipo `Tab` ampliado con `'ajustes'` y quinto `TabItem` con icono de engranaje (Heroicons). Solo el organizador ve las secciones editables.
- [x] **`esOrganizador` reforzado:** computed que devuelve `true` si `viaje.rolEnViaje === 'PROPIETARIO'` (fuente del servidor) o si el `usuarioIdPropio` (UUID cruzando `participantes()` por email) coincide con un participante de rol PROPIETARIO.
- [x] **Portada del viaje:** tarjeta con la `fotoPortada` actual (o placeholder "Sin portada"); botón "Cambiar" (input `type="file" accept="image/*"` oculto) que sube al momento con `uploadPortada`, spinner (estado `subiendoPortada`) y error inline (`errorPortada`). Al terminar actualiza `viajeDetalle` y la lista del dashboard.
- [x] **Edición de datos (formAjustes):** FormGroup con `titulo` (máx 200, requerido) y `fechaInicio`/`fechaFin` (`type="date"`, requeridos), precargado desde el viaje mediante un `effect()` sobre `viaje()`. Solo se muestra el formulario al organizador (si no, tarjeta informativa read-only "Solo el organizador puede modificar estos datos"). `guardarAjustes()` → `updateViaje` con estados `guardandoAjustes` y `errorAjustes`, y sincroniza `viajeDetalle` + lista del dashboard vía `reactualizarEnLista`.
- [x] **Participantes:** lista con avatar (inicial), nombre, email, badge rol (`rolLabel`: "Organizador"/"Invitado"). El organizador ve el botón **"+ Invitar"** (abre modal con el código del viaje y "Copiar Código", señal `copiadoInvitacion`) y, en cada invitado distinto de sí mismo, la **papelera de expulsión** (`expulsarParticipante` con `confirm()` nativo, estado `expulsandoUsuario` y spinner). Al expulsar se filtra la señal `participantes`. Errores en `errorExpulsar`.
- [x] **Zona de peligro:** organizador → botón rojo "Eliminar viaje" (reutiliza `abrirModalEliminar()`/`modalEliminarAbierto` ya existentes); invitado → botón rojo "Abandonar viaje" (`confirmarAbandonarViaje`, `confirm()` + `abandonarViaje`, estados `abandonandoAjustes` y `errorAbandonar`) que navega a `/dashboard` tras salir.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~110 KB).

### ✅ Completado en esta sesión (Galería multimedia — álbumes y muro de fotos)
- [x] **Tipos Backend-First:** nuevas interfaces `Album` (`id`, `viajeId`, `nombre`, `fechaCreacion`) y `Foto` (`id`, `viajeId`, `albumId?`, `url`, `fechaSubida`, `usuarioId`, `usuarioNombre`) y `AlbumRequest` (`nombre`) en `shared/types/index.ts`, espejo exacto de `AlbumResponseDTO`/`FotoResponseDTO`/`AlbumRequestDTO` del backend.
- [x] **`GaleriaService`** (`core/services/galeria.service.ts`, `providedIn: 'root'`): señales `albumes` y `fotos` + métodos CRUD contra la API real:
  - `getAlbumes/cargarAlbumes` → `GET /api/viajes/{viajeId}/albumes`. `crearAlbum` → `POST` (body `AlbumRequest`). `eliminarAlbum` → `DELETE .../albumes/{albumId}`.
  - `getFotos/cargarFotos(viajeId, albumId?)` → `GET /api/viajes/{viajeId}/fotos?albumId=` (filtro opcional del servidor, usado en el alta cliente). `subirFoto(viajeId, file, albumId?)` → `POST multipart/form-data` con campos `file` + `albumId` opcional (201). `eliminarFoto` → `DELETE .../fotos/{fotoId}`.
- [x] **Nueva pestaña "Galería"** en el detalle del viaje: tipo `Tab` ampliado con `'galeria'` y cuarto `TabItem` con icono de cámara Heroicons.
- [x] **Carga al abrir** el detalle: en el constructor se llama a `cargarAlbumes` + `cargarFotos` (además de los cargos existentes).
- [x] **Fila de álbumes horizontal** (`scrollbar-hide overflow-x-auto`): chip fijo **"Todas"** (muestra el total de fotos) + un chip-álbum por `albumes()` con **portada automática** (primera foto del álbum con gradiente inferior, o icono de cámara si está vacío), contador de fotos y **overlay de borrado** con papelera (sin spinner, `confirm()` nativo: "Las fotos se conservarán en la galería"). Cierra el loop con el botón **"+ Álbum"** (dashed). El chip activo se resalta en azul. `seleccionarAlbum(null)` vuelve a "Todas".
- [x] **`fotosVisibles` (computed):** filtra `fotos()` por `albumSeleccionado` (sin llamada extra al backend; la señal `fotos` guarda todas y el grid aplica el filtro en cliente).
- [x] **Muro de fotos** (`grid grid-cols-3 gap-2`): cada foto en un tile `aspect-square` con `object-cover` que abre la URL en pestaña nueva (`target="_blank"`), **avatar/autor** (badge abajo-derecha con la inicial de `usuarioNombre` vía helper `inicial`) y **papelera** arriba-izquierda (`bg-black/55 text-red-300`, con spinner mientras `borrandoFotoId` coincide) **solo si `puedeBorrarFoto(foto)`**.
- [x] **Permisos de borrado:** `esOrganizador` (`viaje.rolEnViaje === 'PROPIETARIO'`) o `usuarioIdPropio` (UUID del usuario actual resuelto cruzando `participantes()` por email con `AuthService.usuario`). Confirmación nativa `confirm()`. `@empty` del grid con ilustración de cámara "No hay fotos todavía".
- [x] **Modal "Nuevo álbum"** (bottom-sheet): input nombrado (máx 200, Enter guarda) con estados `creandoAlbum` y `errorAlbum`. Al crear recarga álbumes.
- [x] **Modal "Subir foto"** (bottom-sheet): `<select>` de álbum (preseleccionado con el álbum activo o "Sin álbum"), botón dashed que dispara un `input type="file" accept="image/*"` oculto, muestra el nombre truncado con `truncarNombre`, estados `subiendoFoto` y `errorFoto`. Al subir, `fotos.update` **prepend** la nueva `Foto` (sin recargar). `albumFoto` se resetea a `albumSeleccionado` al abrir.
- [x] **Borrado de álbum:** al eliminarlo se recargan álbumes **y** fotos (las fotos desasociadas pasan a "Todas") y si el álbum borrado era el filtro activo se vuelve a "Todas".
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~92 kB). Backend ya listo (`36bc9f4`).

### ✅ Completado en esta sesión (Filtros dinámicos del mapa + auto-centrado)
- [x] **Estado de filtros del mapa:** nuevo tipo `MapFilterType` (`'ALL' | 'DAY' | 'ACTIVITY'`) y dos señales `mapFilterType` (default `'ALL'`) y `mapFilterValue` (`string | null`). Métodos `setFiltroMapa(tipo, valor?)` para cambiar el filtro. Nuevo tipo `Dia` reutilizado.
- [x] **`actividadesMapa` (computed):** devuelve el array de actividades filtrado según el estado (`DAY` → por `fechaHoraInicio.startsWith(valor)`, `ACTIVITY` → por `id`), filtrando previamente solo las que tienen latitud y longitud. `centroMapa` (posición inicial del `<google-map>`) y los marcadores del mapa ahora se alimentan de este computed.
- [x] **`diasMapa` (computed):** lista dinámica de días del viaje con al menos una actividad geolocalizada; se usa para generar los chips. Derivada de `dias()` cruzando con las actividades que tengan coordenadas.
- [x] **Chips de filtrado en la pestaña Mapa:** contenedor horizontal `-mx-4 flex gap-2 overflow-x-auto px-4 pb-1` con clase `scrollbar-hide` justo encima del `<google-map>`. Chip estático **"Todos"** + un chip **"Día N"** por cada `diasMapa()`. El activo se pinta con fondo azul (`bg-blue-600 text-white shadow-lg`) y el inactivo blanco/oscuro; `mapFilterType()/mapFilterValue()` determinan la clase activa.
- [x] **Salto desde el itinerario al mapa (📍):** nuevo botón con icono de pin en cada tarjeta del timeline (junto a editar/borrar, con `$event.stopPropagation()`). `irAlMapaDe(actividad)` fija el filtro a `'ACTIVITY'` con el `id` de la actividad y cambia la pestaña a `mapa`, dejando solo su marcador y centrando la cámara en ella.
- [x] **Auto-centrado dinámico (`autoCentrarMapa`):** nuevo `effect()` en el constructor observa `actividadesMapa()` y recalcula la cámara en cada cambio: con **1 actividad** usa `mapa.panTo({lat,lng})` + `mapa.setZoom(15)`; con **más de 1** construye `google.maps.LatLngBounds` y llama `mapa.fitBounds(bounds, 56)`. Reemplaza al `ajustarBounds` anterior (que solo hacía `fitBounds`). `onMapaInicializado` lo invoca mediante `setTimeout(...,0)` para esperar el render tras el cambio de filtro. El `effect` anterior que observaba `actividadesConCoordenadas` se sustituyó por este.
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~74 kB).

### ✅ Completado en esta sesión (CRUD completo de planes del itinerario — editar y borrar)
- [x] **`ActividadService.updateActividad(viajeId, actividadId, actividad)`:** nuevo método → `PUT /api/viajes/{viajeId}/actividades/{actividadId}` con body `ActividadRequest`; devuelve la `Actividad` actualizada. Nuevo método `deleteActividad(viajeId, actividadId)` → `DELETE .../actividades/{actividadId}` (204).
- [x] **Iconos de acción en las tarjetas del timeline:** cada tarjeta de actividad muestra ahora dos botones circulares a la derecha del título: **lápiz (editar)** y **papelera (eliminar)**. Ambos hacen `$event.stopPropagation()` para no disparar la apertura del modal de detalle. Estados hover azul / rojo con soporte `dark:`.
- [x] **Edición con modal precargado (`PUT`):** `abrirEdicion(actividad)` rellena el `formActividad` (desglosando `fechaHoraInicio` en `dia` `YYYY-MM-DD` y `hora` `HH:mm`, más `titulo`, `ubicacion→descripcion`, `direccion`, `latitud`, `longitud`), fija el signal `editandoId` y abre el modal "Añadir plan" reconvertido en **"Editar plan"** (título y subtítulo condicionales). `guardarActividad()` ahora bifurca: si `editandoId()` hay → `updateActividad`, si no → `crearActividad`, y tras el éxito recarga el listado y resetea `editandoId`.
- [x] **Confirmación de borrado:** botón papelera → modal de confirmación (`modalBorrarActividadAbierto`, z-`60`) con chip rojo, título "Eliminar plan", mensaje con el nombre de la actividad y advertencia "Esta acción no se puede deshacer", botón rojo "Eliminar plan" + "Cancelar". Estados `borrandoActividad` (deshabilita con "Eliminando...") y `errorBorrarActividad` inline. Al confirmar llama a `deleteActividad` y actualiza la señal `actividades` filtrando la actividad (sin recargar lista).
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado, ~72 kB).

### ✅ Completado en esta sesión (Clean maps warnings + geolocalización HTML5 nativa)
- [x] **Fix script loading (performance):** `GoogleMapsService.cargar()` añade `&loading=async` (carga no bloqueante del loader) y sube las librerías a `libraries=places,marker` (la librería `marker` es requerida por `AdvancedMarkerElement`; `places` para autocomplete). Sigue cacheando la promesa para no duplicar el `<script>`.
- [x] **AdvancedMarkerElement (sin deprecation warning):** `<google-map>` recibe `[mapId]="'DEMO_MAP_ID'"` (map ID requerido por los advanced markers; debe sustituirse por un Map ID real de la consola para producción). Todos los `<map-marker #marker="mapMarker">` se reemplazaron por `<map-advanced-marker [position]="{lat,lng}" [title]="..." (mapClick)="abrirInfoWindow(marker, actividad)" #marker="mapAdvancedMarker">`. `abrirInfoWindow` cambia su firma a `MapAdvancedMarker`. El `map-info-window` sigue abriéndose con `open(marker)` (MapAdvancedMarker implementa `MapAnchorPoint`).
- [x] **AutocompleteService → Places API (New):** `consultarAutocomplete()` ahora intenta primero `google.maps.places.AutocompleteSuggestion.fetchAutocompleteSuggestions({ input })` (Promise con `suggestions[].placePrediction`), y `seleccionarLugar()` resuelve el detalle con `PlacePrediction.toPlace().fetchFields({ fields: ['formattedAddress','location'] })`. **Límite documentado:** la Nueva API exige habilitar **"Places API (New)"** en Google Cloud Console (factura aparte); si `AutocompleteSuggestion` no está disponible en runtime, hay **fallback automático al legado** `AutocompleteService.getPlacePredictions` + `PlacesService.getDetails` (métodos `aplicarLugarLegacy`/`truncarDireccion`), manteniendo siempre el `debounceTime(500)` + `distinctUntilChanged()` + `switchMap()` sobre el `Subject`. Nuevo tipo `ResultadoBusqueda` (`placeId`, `descripcion`, `sugerencia?`, `prediccionLegacy?`); la plantilla usa `resultado.descripcion`/`placeId`.
- [x] **Geolocalización HTML5 ("Punto Azul"):** botón **"📍 Mi Ubicación"** flotante sobre el mapa (posición `absolute right-3 top-3 z-10`, por encima del `z-0` del contenedor). Al pulsarlo, `localizar()` usa `navigator.geolocation.getCurrentPosition()` (con `enableHighAccuracy`, `timeout: 10000`, `maximumAge: 30000`), guarda `userLocation = signal<LatLngLiteral|null>` y `panTo()` + `setZoom(16)` sobre esa posición. Se dibuja un `<map-advanced-marker>` especial con `[content]="userMarkerContent()"` (SVG personalizado con dos círculos azules y punto central, generado en `crearPuntoAzul()`) y `[zIndex]="1000"`. Si se localiza antes de que el mapa exista, `onMapaInicializado()` centra en `userLocation` en vez de `fitBounds`. Estados `localizando` (texto "Localizando…") y `errorGeo` (chip rojo abajo-izquierda).
- [x] **Verificación:** `ng build` compila sin errores ni warnings (lazy chunk `detalle-viaje` ~67 kB).

### ✅ Completado en esta sesión (Migración de Leaflet a Google Maps Platform con environments)
- [x] **Limpieza Leaflet:** `npm uninstall leaflet @types/leaflet`. Eliminado `node_modules/leaflet/dist/leaflet.css` del array `styles` de `angular.json` y `allowedCommonJsDependencies: ["leaflet"]`. Borrada la carpeta `public/markers/` (iconos `marker-icon*.png` / `marker-shadow.png`). Eliminado el tipo `NominatimResult` de `shared/types/index.ts`.
- [x] **Dependencias Google:** `npm install @angular/google-maps@21` (compatible con Angular 21; la v22 exige Angular 22+) + `npm install -D @types/google.maps`. En `tsconfig.app.json` `"types": ["google.maps"]` (el proyecto usaba `"types": []` y ocultaba los tipos globales). Añadido `fileReplacements` en `angular.json` (config `development`) para intercambiar `environment.ts` ↔ `environment.development.ts`.
- [x] **Environments:** creados `src/environments/environment.ts` y `src/environments/environment.development.ts` con `googleMapsApiKey: 'TU_API_KEY_AQUI'` (producción/desarrollo). ⚠️ **El usuario debe reemplazar `TU_API_KEY_AQUI` por su clave real en AMBOS archivos.**
- [x] **Carga dinámica del script:** nuevo `GoogleMapsService` (`core/services/google-maps.service.ts`, `providedIn: 'root'`) que inyecta `<script>` a `https://maps.googleapis.com/maps/api/js?key=...&libraries=places&callback=__viajesGoogleMapsCallback` con promesa cacheada (no duplica el script). Se invoca en el constructor de `App` (componente raíz) y en `DetalleViaje`.
- [x] **Buscador Places Autocomplete con debounce:** en el modal "Añadir plan" se eliminó Nominatim. El input `direccion` dispara `(input)="onBuscarInput()"` → `Subject<string>` encadenado con `debounceTime(500)` + `distinctUntilChanged()` + `switchMap()` (RxJS) → `google.maps.places.AutocompleteService.getPlacePredictions` (una petición cada 500 ms de pausa, ahorra cuota). `seleccionarLugar(prediccion)` usa `PlacesService.getDetails({ placeId })` para extraer `formatted_address` (truncado ≤250), `geometry.location.lat()/lng()` y rellena `direccion`, `latitud`, `longitud`. Estados `buscandoLugar` ("Buscando lugares…"), `errorBusqueda` y hint verde. Se quitó el botón "Buscar" (autocompletado en vivo con icono lupa).
- [x] **Visor del mapa (`google-map`):** la pestaña `mapa` ya no usa Leaflet. `<google-map width="100%" height="500px" [center]="centroMapa()" [zoom]="zoomMapa" [options]="opcionesMapa" (mapInitialized)="onMapaInicializado($event)">`. `@for` sobre `actividadesConCoordenadas()` renderiza `<map-marker [position]="{lat,lng}" [title]="..." (mapClick)="abrirInfoWindow(marker, actividad)">`. Un `<map-info-window #infoWindow>` muestra título + hora (`HH:mm`) al pulsar el pin. `fitBounds` (padding 56) se aplica en `onMapaInicializado` y en un `effect()` que observa `actividadesConCoordenadas()` (auto-centrado al añadir planes). `scrollwheel: false` (mobile-first). El contenedor mantiene `relative z-0` para stacking context sobre los modales.
- [x] **Verificación:** `ng build` compila sin errores ni warnings. El lazy chunk `detalle-viaje` baja de ~200 kB (Leaflet) a ~65 kB (el script de Google Maps se carga desde su CDN).

### ✅ Completado en esta sesión (Mapa Leaflet + geocodificación gratis con Nominatim)
- [x] **Dependencias:** `npm install leaflet` + `npm install -D @types/leaflet`. `leaflet.css` añadido al array `styles` de `angular.json` (`node_modules/leaflet/dist/leaflet.css`) y `leaflet` declarado en `allowedCommonJsDependencies` (evita el warning de optimización por CJS en el build).
- [x] **Fix icono del marcador:** copiadas las imágenes `marker-icon.png`, `marker-icon-2x.png` y `marker-shadow.png` de Leaflet a `public/markers/`. En `montarMapa()` se hace `L.Icon.Default.mergeOptions({ iconUrl, iconRetinaUrl, shadowUrl })` para que el bundler de Angular resuelva los assets por su cuenta (fallo clásico del empaquetado).
- [x] **Tipos (Backend-First mirror):** `Actividad` añade `direccion: string | null` y `latitud`/`longitud` pasan a `number | null`; `ActividadRequest` añade `direccion?: string`. Nuevo tipo `NominatimResult` (`place_id`, `display_name`, `lat`, `lon`).
- [x] **Buscador en el modal "Añadir plan":** campo `direccion` (control del FormGroup) + botón "Buscar". `buscarLugar()` hace `GET https://nominatim.openstreetmap.org/search?format=json&limit=5&q=...` y muestra los resultados en una lista (`resultadosBusqueda` `: NominatimResult[]`). Al pulsar un resultado, `seleccionarLugar()` guarda `display_name` (truncado a ≤250) como `direccion` y `Number(lat)`/`Number(lon)` en `latitud`/`longitud` del FormGroup. Estados `buscandoLugar` (spinner), `errorBusqueda` y hint verde "Ubicación guardada en el mapa del viaje".
- [x] **`formActividad` ahora es `FormBuilder.group`** (permite valores `null`): se añaden `direccion`, `latitud`, `longitud`. En `guardarActividad()` se envían al backend (todos opcionales) además de `descripcion` (campo "Ubicación" libre).
- [x] **Pestaña Mapa (`DetalleViajeComponent`):** al activar la tab `mapa`, `setTab()` destruye el mapa anterior y programa `montarMapa()`. Inicializa `L.map` centrado en el primer punto con coordenadas (como primer marcador) o en Madrid por defecto; `tileLayer` gratuito de OpenStreetMap; por cada actividad con `latitud`/`longitud` no nulos crea un `L.marker` con `bindPopup` (título + hora `HH:mm`). `fitBounds` (con padding) cuando hay varios puntos, `L.featureGroup`, `scrollWheelZoom: false` (mobile-first) y `invalidateSize()` tras montar. La hoja de mapa es `#map-itinerario` con `h-[420px]` y `relative z-0` (crea stacking context para que los modales queden por encima). Se destruye en `ngOnDestroy`.
- [x] **Verificación:** `ng build` compila sin errores ni warnings (lazy chunk `detalle-viaje` ~200 kB con Leaflet embebido).

### ✅ Completado en esta sesión (Abandonar vs Eliminar viaje — borrado lógico en el Dashboard)
- [x] **`ViajeService.abandonarViaje(id)`:** nuevo método → `DELETE /api/viajes/{id}/participantes/me` (borrado lógico del invitado, 204).
- [x] **Botón de acción por card en el Dashboard:** en la fila de "Presupuesto base" de cada tarjeta. Si `viaje.rolEnViaje === 'PROPIETARIO'` (el creador) muestra la **papelera roja** (elimina todo el viaje); si es invitado muestra el **icono de salida** (abandonar el viaje). El botón hace `$event.stopPropagation()` para no navegar al detalle. helper `esEliminar(viaje)`.
- [x] **Modal de confirmación unificado (`modalAccion`):** al pulsar el botón se abre un bottom-sheet con chip rojo (Eliminar) o azul (Abandonar), título y mensaje específico. Confirmación roja `"No se puede deshacer"` para eliminar o azul "podrás volver a unirte con el código" para abandonar. Estados `procesandoAccion` (deshabilita y muestra "Eliminando…") y `errorAccion` inline. Al confirmar llama a `eliminarViaje` o `abandonarViaje` y **recarga el listado** (`cargarMisViajes()`).
- [x] **Unirse a un viaje:** al recibir el 200 OK ahora cierra el modal y **dispara la recarga de la lista** (`cargarMisViajes`).
- [x] **Verificación:** `ng build` compila sin errores (chunk `dashboard` regenerado).

### ✅ Completado en esta sesión (UI multi-documento 1:N — ver y borrar documentos por actividad)
- [x] **Tipo `Documento`** en `shared/types/index.ts` (`id`, `nombreOriginal`, `urlArchivo`). `Actividad` reemplaza `urlDocumento: string` por `documentos: Documento[]`; `ActividadRequest` pierde `urlDocumento` (espejo exacto del `ActividadResponseDTO`/`ActividadRequestDTO` del backend).
- [x] **`ActividadService.subirDocumento(viajeId, actividadId, file)`:** ahora apunta a `POST /api/viajes/{viajeId}/actividades/{actividadId}/documentos` (plural) y devuelve la `Actividad` actualizada con su `documentos[]`. Nuevo método `eliminarDocumento(viajeId, actividadId, documentoId)` → `DELETE .../documentos/{documentoId}` (204).
- [x] **Bottom-sheet de detalle de actividad (`detalle-viaje`):**
  - El botón "Adjuntar archivo (PDF/Imagen)" queda **siempre visible** para permitir múltiples subidas (input `type="file"` oculto + estado `subiendoArchivo`).
  - Debajo, `@for` sobre `actividad.documentos` con `track documento.id`: cada documento muestra `nombreOriginal`, un enlace **"Ver"** (anchor con `target="_blank"` + `rel="noopener noreferrer"` abriendo `urlArchivo`) y un **botón papelera** (rojo, discreto, 40px) que llama a `eliminarDocumentoDeActividad(documento.id)`.
  - **Estado reactivo:** al subir se actualizan `actividadSeleccionada` y la señal `actividades` con la `Actividad` devuelta; al borrar, `borrandoDocumento` (signal `<UUID|null>`) deshabilita el botón con spinner (`animate-spin`) mientras dura la petición y, al completar, se filtra el documento de `actividadSeleccionada` y de `actividades` inmutablemente (sin recargar la lista). `@empty` con "Aún no hay documentos adjuntos a esta actividad."
- [x] **Verificación:** `ng build` compila sin errores (lazy chunk `detalle-viaje` regenerado).

### ✅ Completado en esta sesión (Subida de documentos real + asignación de checklist)
- [x] **ActividadService.subirDocumento(viajeId, actividadId, file):** ya NO es un mock. Hace `POST multipart/form-data` a `/api/viajes/{viajeId}/actividades/{actividadId}/documentos` con el campo `file` y devuelve el `Actividad` actualizado (con `documentos[]` reales de Cloudflare R2, relación 1:N).
- [x] **Adjuntar archivo en el modal de detalle de actividad:** el botón "Adjuntar archivo (PDF/Imagen)" (con input `type="file"` oculto) vive ahora en el bottom-sheet de detalle del plan (donde ya existe `actividadId`). Al seleccionar un archivo llama a `subirDocumento`, muestra estado `subiendoArchivo` ("Subiendo documento...") y al terminar actualiza `actividadSeleccionada` + la señal `actividades` para mostrar el botón "Ver documento adjunto" con la `urlDocumento` devuelta.
  - **Eliminado el adjunto del modal "Añadir plan":** el backend necesita un `actividadId` existente para subir el documento, así que ya no se adjunta en la creación; se hace tras crear la actividad (se retiró el bloque `archivo`/`archivoInput`/`quitarArchivo` del modal de crear y el campo `urlDocumento` del request).
- [x] **Checklist — selector de asignación restaurado:** junto a cada tarea hay un `<select>` "Sin asignar + participantes del viaje". Los participantes llegan de `ViajeService.getParticipantes(viajeId)` → `GET /api/viajes/{id}/participantes` (señal `participantes` + `cargarParticipantes`). Al cambiar el valor se llama a `TareaChecklistService.asignarUsuario(viajeId, tareaId, usuarioId)` (PUT `.../asignar?asignadoId=`) y se recargan las tareas. El badge con `nombreAsignado` se conserva; el select preselecciona la asignación actual vía `idAsignadoDe(tarea)` (match por nombre).
- [x] **Tipo `Participante`** en `shared/types/index.ts` (`id`, `nombre`, `email`, `rolEnViaje`).
- [x] **Verificación:** `ng build` compila sin errores (chunk `detalle-viaje` regenerado).

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