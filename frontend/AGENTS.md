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

- **Última actualización:** 10/08/2026
- **Hito actual:** FASE 1 + B1-P2 + B2-P1 + B2-P2 + B3-P1 (Dashboard) + B3-P2 (Crear Viaje + Invitaciones) + B4-P1 (Detalle Viaje) + B4-P2 (Itinerario + Mini-Calendario) + B5-P1 (Finanzas) + Dark Mode + Proxy + HOTFIX Tailwind v3 + BUGFIX Y PULIDO + ITERACIÓN UI/UX + HOTFIX ddl-auto + Interceptor 401/403 + Módulo Perfil + USABILIDAD UI/UX + INTEGRACIÓN CHECKLIST CON BACKEND + SUBIDA DE DOCUMENTOS REAL (R2) + SELECTOR DE ASIGNACIÓN CHECKLIST + **UI MULTI-DOCUMENTO (1:N) CON BORRADO + ABANDONAR VIAJE (BORRADO LÓGICO) + MAPA LEAFLET + GEOCODIFICACIÓN NOMINATIM + MIGRACIÓN A GOOGLE MAPS PLATFORM + CLEAN MAPS WARNINGS + GEOLOCALIZACIÓN HTML5 + CRUD COMPLETO DE ACTIVIDADES (EDITAR + BORRAR) + FILTROS DINÁMICOS Y AUTO-CENTRADO DEL MAPA + GALERÍA MULTIMEDIA (ÁLBUMES Y MURO DE FOTOS) + PANEL DE AJUSTES DEL VIAJE (PORTADA, EDICIÓN DE DATOS, PARTICIPANTES, INVITAR Y ZONA DE PELIGRO) + PULIDO UI (AVATAR EN CABECERA, PORTADA AJUSTES Y TARJETAS CON PORTADA) + AVATAR INTERACTIVO EN PERFIL (SUBIDA A R2) + AVATARES DE USUARIO RENDERIZADOS (CABECERA, PARTICIPANTES Y GALERÍA) + FINANZAS REACTIVAS (SELECTO GRUPO/PERSONAL CON BALANCES) + MODAL AVANZADO DE CREACIÓN DE GASTOS (DIVISIÓN DINÁMICA) + PRESUPUESTO INDIVIDUAL (AJUSTES) + FIX UUID PAGADOR EN GASTOS + RESUMEN DE DEUDAS (FINANZAS) + ETIQUETAS SPLITWISE EN LISTA DE GASTOS + EDICIÓN Y BORRADO DE GASTOS CON RECÁLCULO DE DEUDAS + PREVISUALIZACIÓN DINÁMICA DEL REPARTO + BOTÓN DE LIQUIDACIÓN DE DEUDA (GASTO INVERSO) + REFACTOR FRONTEND DE DEUDAS (ELIMINACIÓN DEUDA_PAGADOR, FIX MATEMÁTICO DE PORCENTAJES Y PREVISUALIZACIÓN ESTRUCTURADA)** COMPLETADOS.

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