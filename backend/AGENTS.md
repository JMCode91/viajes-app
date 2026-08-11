# 🌍 Proyecto: Travel Planner "All-in-One" - Documentación Core (Backend)

## 🎯 Visión General
Desarrollo de una aplicación web de gestión integral de viajes enfocada estrictamente en un diseño Mobile-First. El sistema permite la creación de viajes, sistema de invitaciones colaborativas, gestión logística mediante un calendario interactivo geolocalizado, control de presupuesto, listas de tareas y almacenamiento seguro de documentos (billetes, reservas) y galerías multimedia.
- **Objetivo MVP:** Despliegue en septiembre para pruebas en entorno real.

## 🔒 Arquitectura de Seguridad (Critical)
- **Autenticación:** JSON Web Tokens (JWT) sin estado.
- **Autorización:** Control de acceso basado en roles (RBAC) y propiedad de recursos. Un usuario solo puede acceder a la información y archivos de un viaje si su `Usuario_ID` está vinculado a dicho `Viaje_ID`.
- **Archivos Sensibles:** Los documentos no se sirven estáticamente. Se descargan a través de un endpoint protegido que devuelve el flujo de bytes previa validación del JWT y pertenencia al viaje.
- **Prevención DoS y Agotamiento de Recursos:** Implementación obligatoria de límites de tamaño de archivo (Max 5MB) en Spring Boot y previsión de Rate Limiting en el proxy inverso (Nginx) para evitar ataques que saturen la RAM o el disco.

---

## 🗺️ Roadmap Backend Inicial (Plan de Acción)

### Fase 1: Infraestructura Base y Base de Datos (Completada)
- [x] Crear `docker-compose.yml` para levantar PostgreSQL + PostGIS (incluyendo volumen para persistencia).
- [x] Inicializar proyecto Spring Boot con las dependencias base (Web, JPA, PostgreSQL, Validation, Lombok).
- [x] Configurar `application.properties` (o `.yml`) con credenciales de base de datos y propiedades de Hibernate (update/validate).

### Fase 2: Entidades Core y Seguridad (Completada)
- [x] Generar Entidad `Usuario` (id, email, password, nombre, rol).
- [x] Generar Entidad `Viaje` y la tabla intermedia de invitaciones/participantes.
- [x] Implementar Spring Security: Configuración de SecurityFilterChain, PasswordEncoder (BCrypt).
- [x] Implementar generación y validación de JWT (JwtUtil, JwtFilter).
- [x] Desarrollar endpoints públicos: `/api/auth/register` y `/api/auth/login`.

### Fase 3: Gestión de Viajes e Invitaciones (Completada)
- [x] Desarrollar CRUD de Viajes (solo lectura/escritura para usuarios pertenecientes al viaje).
- [x] Implementar lógica de negocio: Enviar invitación a usuario por email/id y aceptar invitación.

### Fase 4: Logística, Calendario y Mapa (PostGIS) (Completada)
- [x] Crear entidad `Actividad` (tipo: vuelo, hotel, excursión) vinculada a `Viaje`.
- [x] Implementar geolocalización: Guardar coordenadas (latitud/longitud) adaptadas para futura compatibilidad con PostGIS.
- [x] Desarrollar endpoints para listar actividades de un viaje ordenadas por fecha (Timeline).

### Fase 5: Módulos Secundarios y Archivos Seguros (Completada)
- [x] Entidades y CRUD para `Gastos` (Presupuesto) y `Items` (Checklist).
- [x] Entidad y CRUD para `Diccionario` (Vocabulario de supervivencia), multi-idioma + traducción con DeepL.
- [x] **Módulo de Archivos Seguros:** Implementar servicio de subida/bajada de archivos (Local o S3) con validación estricta de pertenencia al viaje.

---

## 📊 Estado Actual del Proyecto
*Esta sección será actualizada automáticamente por el agente OpenCode al finalizar cada sesión.*

- **Última actualización:** 11/08/2026
- **Hito actual:** BACKEND COMPLETADO (Fases 1-5) + **LOGGING DETALLADO DE ERRORES DE LA API DE GEMINI (STATUS HTTP + BODY RAW + API KEY ENMASCARADA)** + **FIX DIVISAS (CONVERSIÓN MONEDA IGUAL SIN LLAMADA HTTP + PREVENCIÓN DE ClassCastException EN LOS RATES NUMÉRICOS)** + **MÓDULO DIVISAS ESCALADO (CATÁLOGO DE MONEDAS VÍA `/CURRENCIES` + FECHA DE ACTUALIZACIÓN DEL MERCADO EN LA CONVERSIÓN)** + **HOTFIX DELETE viaje + Arquitectura Checklist (TareaChecklist) + INTEGRACIÓN CLOUDFLARE R2 (S3) + REFACTOR MULTI-DOCUMENTO 1:N CON BORRADO INTELIGENTE EN CASCADA + BORRADO LÓGICO (ABANDONAR VIAJE) + GEOLOCALIZACIÓN EN ACTIVIDAD + CRUD COMPLETO DE ACTIVIDADES (PUT) + MÓDULO GALERÍA MULTIMEDIA (ÁLBUMES Y FOTOS) + PANEL DE AJUSTES DEL VIAJE (PORTADA, EDITAR DATOS, EXPULSAR PARTICIPANTE) + AVATAR DE USUARIO (SUBIDA DE PERFIL A R2) + PROPAGACIÓN DE AVATAR A DTOs (PARTICIPANTES Y FOTOS) + MÓDULO DE FINANZAS Y GASTOS COMPARTIDOS (FASE ESTRUCTURAL) + MÓDULO DE FINANZAS (FASE 2: ALGORITMOS DE DIVISIÓN Y BALANCES) + ENDPOINT PRESUPUESTO INDIVIDUAL POR PARTICIPANTE + FIX MAPEO PRESUPUESTO EN BALANCES + EDICIÓN Y BORRADO DE GASTOS CON RECÁLCULO DE DEUDAS + REFACTOR: ELIMINACIÓN DEL ENUM DEUDA_PAGADOR + BUGFIX BORRADO EN CASCADA DE GASTOS Y RECÁLCULO DE MIS GASTOS + SEEDER DE USUARIOS DE DESARROLLO + PERFIL Y AJUSTES DEL USUARIO (CAMBIO DE CONTRASEÑA Y PREFERENCIAS DE NOTIFICACIONES) + INTEGRACIÓN GOOGLE PLACES EN ACTIVIDAD (REFACTOR: ELIMINACIÓN DEL CAMPO UBICACIÓN/DESCRIPCION) + CAMPO `descripcionGoogle` (RESUMEN EDITORIAL DE GOOGLE PLACES) + MÓDULO CLIMA (PRONÓSTICO OPEN-METEO CON FALLBACK HISTÓRICO) + MÓDULO DIVISAS (CONVERSOR DE MONEDAS CON FRANKFURTER, SIN API KEY) + MÓDULO IA (GENERADOR DE ITINERARIOS CON GOOGLE GEMINI, API KEY `gemini.api.key`)** COMPLETADOS.
- **Completado en esta sesión (Logging detallado de errores de la API de Gemini):**
  - **`IaServiceImpl.generarItinerario` — API key enmascarada antes de la petición:** justo antes de la llamada HTTP se registra `log.info("Llamando a la API de Gemini con la API key enmascarada: {}", enmascararApiKey(apiKey))`. El nuevo helper **`enmascararApiKey(String)`** devuelve `(vacia o null)` si no hay clave, `primerCaracter + "..."` si mide menos de 8 caracteres y `apiKey.substring(0, 4) + "..."` en el resto. Permite confirmar que Spring Boot está leyendo `gemini.api.key` desde el entorno (`.env`) sin exponer el secreto en los logs.
  - **`IaServiceImpl.generarItinerario` — body raw de error HTTP:** nuevo `catch (HttpStatusCodeException ex)` (antes el error caía en el genérico) que registra `log.error("Gemini devolvio un error HTTP {} ({}) al llamar a la API: {}", ex.getStatusCode().value(), ex.getStatusCode(), ex.getResponseBodyAsString())`. **Imprime el cuerpo de la respuesta de error que devuelve Google** (ej. el JSON con `error.message` y `error.status`), no solo el mensaje genérico de la excepción.
  - **`IaServiceImpl.generarItinerario` — mensaje exacto en el catch genérico:** el `catch (Exception ex)` pasa de `log.error("Error al llamar a la API de Gemini", ex)` a **`log.error("Error al llamar a la API de Gemini. Mensaje exacto: {}", ex.getMessage(), ex)`**, imprimiendo el mensaje real de la excepción junto al stack trace.
  - **`IaServiceImpl.generarItinerario` — body recibido en respuestas mal formadas:** cada rama de validación de la respuesta (`respuesta == null`, sin `candidates`, sin `content`, sin `parts`, sin `text`) añade ahora un `log.error` previo que incluye **el cuerpo completo recibido** (`... Cuerpo recibido: {}", respuesta`) antes de lanzar la `IaException`. Facilita depurar cuándo Gemini responde 200 con un JSON inesperado.
  - Se añadió el import `org.springframework.web.client.HttpStatusCodeException`. Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 124 fuentes). Commit `6145cb8`.
- **Completado en esta sesión (Módulo IA — generación de itinerarios con Google Gemini):**
  - **Nuevo módulo independiente del dominio de viajes:** generación de itinerarios de viaje con la **API de Google Gemini** (modelo `gemini-1.5-flash`, endpoint `POST https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key={apiKey}`), vía `RestClient` con body estricto `{"contents":[{"parts":[{"text":"PROMPT"}]}]}`.
  - **DTOs:** nuevo **`IaPromptRequestDTO`** en `dto/request` (`destino` @NotBlank + @Size(max=255), `dias` @NotNull + @Min(1), `tematica` @NotBlank + @Size(max=500)) y nuevo **`IaItinerarioResponseDTO`** en `dto/response` (campo único **`rawJson`**: itinerario JSON crudo que el frontend parsea).
  - **`IaService`/`IaServiceImpl`:** `generarItinerario(IaPromptRequestDTO)`. La clave se inyecta con `@Value("${gemini.api.key:}")` (vacía → `IaException` 503 "El servicio de IA no esta configurado"). Construye el prompt en español exigiendo **ÚNICAMENTE un JSON válido** sin markdown ni texto adicional, con la estructura exacta `[{"dia": 1, "titulo": "...", "actividades": ["..."]}]` (un elemento por día, mínimo 3 actividades). Extrae el texto de `candidates[0].content.parts[0].text` y aplica **limpieza defensiva** (`limpiarJson`): recorta espacios y elimina los fences ```json ``` si el modelo los incluye. Fallos de red o respuestas mal formadas → `IaException` (503, log con SLF4J).
  - **`IaException`** (nueva, `RuntimeException`) → HTTP **503**, registrada en `GlobalExceptionHandler` junto a `TraduccionException`, `ClimaException` y `DivisaException`.
  - **`IaController`** (nuevo) en **`POST /api/ia/generar`** con `@Valid @RequestBody IaPromptRequestDTO` → 200 con `IaItinerarioResponseDTO`. Requiere JWT (cae bajo `anyRequest().authenticated()`).
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 124 fuentes). Commit `56cf055`.
- **Completado en esta sesión (Fix divisas — conversión de la misma moneda y cast numérico seguro en `convertir`):**
  - **`DivisaServiceImpl.convertir` — caso same-currency:** si `monedaOrigen.equals(monedaDestino)` (códigos normalizados a mayúsculas), devuelve **inmediatamente** un `DivisaResponseDTO` con `montoConvertido = monto`, `ratioCambio = 1.0` y `fechaActualizacion = LocalDate.now().toString()` (fecha de hoy), **sin realizar la llamada HTTP a Frankfurter**. Elimina el error 503 que la API externa devolvía al convertir una moneda consigo misma.
  - **`DivisaServiceImpl.convertir` — cast numérico seguro:** la extracción de `rates` pasa de `Map<String, Number>` a **`Map<String, Object>`**, y cada valor se casta explícitamente a la clase genérica `Number` (`Number rateNum = (Number) rates.get(monedaDestino)` → `rateNum.doubleValue()`). Evita la `ClassCastException` que lanzaba Jackson al deserializar enteros como `Integer` en lugar de `Double` (ej. ratios que valen 1.0). Se añade el import `java.time.LocalDate`.
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 118 fuentes). Commit `f89b1b1`.
- **Completado en esta sesión (Módulo Divisas escalado — catálogo de monedas + fecha de actualización del mercado):**
  - **`DivisaResponseDTO` ampliado:** nuevo campo **`fechaActualizacion`** (`String`, fecha oficial del tipo de cambio en formato `YYYY-MM-DD`) al final del record.
  - **`DivisaServiceImpl.convertir`:** extrae el campo de primer nivel **`date`** del JSON de `/latest` de Frankfurter (`respuesta.get("date")`) y lo mapea al nuevo atributo `fechaActualizacion` del DTO.
  - **`DivisaService.obtenerMonedas()` (nuevo en interfaz + impl):** llama a `https://api.frankfurter.app/currencies` con el `RestClient` ya inyectado y devuelve el `Map<String, String>` directo del JSON clave-valor (ej. `{"AUD":"Australian Dollar","BGN":"Bulgarian Lev"}`). Respuesta `null` o error de red → `DivisaException` (503, log con SLF4J).
  - **`DivisaController`:** nuevo endpoint **`GET /api/divisas/monedas`** → 200 con el mapa de monedas. Requiere JWT (cae bajo `anyRequest().authenticated()`).
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 118 fuentes). Commit `36a5345`.
- **Completado en esta sesión (Módulo Divisas — conversión de moneda con la API pública de Frankfurter):**
  - **Nuevo módulo independiente del dominio de viajes:** conversión de divisas entre dos monedas ISO 4217. Usa la **API pública de Frankfurter** (`https://api.frankfurter.app`), sin necesidad de API key, vía `RestClient`.
  - **DTO:** nuevo **`DivisaResponseDTO`** (`montoOriginal`, `monedaOriginal`, `montoConvertido`, `monedaDestino`, `ratioCambio`) en `dto/response`, mismo patrón que `ClimaResponseDTO`.
  - **`DivisaService`/`DivisaServiceImpl`:** `convertir(Double monto, String from, String to)`. Normaliza los códigos a mayúsculas, valida que `monto > 0` y llama a `/latest` con `amount`, `from` y `to`. Del JSON de respuesta extrae el **ratio** y el **valor convertido** del objeto `rates` (la clave es la moneda destino): `montoConvertido = rates.get(to)`, `ratioCambio = montoConvertido / monto`. Códigos normalizados a mayúsculas (`from.toUpperCase()`/`to.toUpperCase()`).
  - **`DivisaException`** (nueva, `RuntimeException`) → HTTP **503**, registrada en `GlobalExceptionHandler` junto a `TraduccionException` y `ClimaException`. Cubre montos inválidos, respuestas vacías, moneda destino ausente en `rates` y fallos de red (log con SLF4J).
  - **`DivisaController`** (nuevo) en **`GET /api/divisas/convertir`** con `@RequestParam Double amount`, `@RequestParam(defaultValue = "EUR") String from` y `@RequestParam(defaultValue = "JPY") String to` → 200 con `DivisaResponseDTO`. Requiere JWT (cae bajo `anyRequest().authenticated()`).
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 118 fuentes).
- **Completado en esta sesión (Módulo Clima — pronóstico meteorológico con Open-Meteo):**
  - **Nuevo módulo independiente del dominio de viajes:** pronóstico del clima para una ubicación (latitud/longitud) y una fecha. Usa la **API pública de Open-Meteo** (`https://api.open-meteo.com`), sin necesidad de API key, vía `RestClient`.
  - **DTO:** nuevo **`ClimaResponseDTO`** (`tempMax`, `tempMin`, `condicion`, `icono`, `esHistorico`) en el nuevo paquete `dto/response`.
  - **`ClimaService`/`ClimaServiceImpl`:** `obtenerClima(Double latitud, Double longitud, LocalDate fecha)`. **Regla de los 14 días:** si la fecha cae entre hoy y los próximos 14 días → llama a `/v1/forecast` (`daily=weathercode,temperature_2m_max,temperature_2m_min`, `timezone=auto`, ventana `start_date=end_date=fecha`) y traduce los códigos WMO a texto amigable (0=Despejado/sol, 1-3=Nublado/nube, 45-48=Niebla, 51-57=Llovizna, 61-67=Lluvia, 71-77=Nieve, 80-82=Chubascos, 85-86=Nieve, 95-99=Tormenta/tormenta). Si la fecha es pasada o supera los 14 días → **fallback histórico** genérico para Japón en ese mes (`tempMax=28`, `tempMin=20`, condicion="Historico Mensual", `esHistorico=true`). Fechas dentro del plazo sin datos → también fallback histórico.
  - **`ClimaException`** (nueva, `RuntimeException`) → HTTP **503**, registrada en `GlobalExceptionHandler` junto a `TraduccionException`.
  - **`ClimaController`** (nuevo) en **`GET /api/clima`** con `@RequestParam lat`, `lon` y `fecha` (`@DateTimeFormat(iso = ISO.DATE)`, formato `YYYY-MM-DD`) → 200 con `ClimaResponseDTO`. Requiere JWT (cae bajo `anyRequest().authenticated()`).
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 113 fuentes).
- **Completado en esta sesión (Campo `descripcionGoogle` — resumen editorial de Google Places):**
  - **`Actividad` ampliada** (tabla `actividades`): nuevo campo **`descripcionGoogle`** (`String`, `columnDefinition = "TEXT"`, nullable) → columna **`descripcion_google`** (resumen editorial del lugar). El esquema se actualiza solo (`ddl-auto=update`).
  - **DTOs:** `ActividadRequestDTO` gana `descripcionGoogle` (opcional, `@Size(max = 2000)`); `ActividadResponseDTO` lo expone entre `urlGoogleMaps` y `tipoActividad`.
  - **`ActividadServiceImpl`:** `crearActividad` y `actualizarActividad` mapean `dto.descripcionGoogle() → actividad.setDescripcionGoogle(...)`; `toResponseDTO` lo devuelve vía `actividad.getDescripcionGoogle()`. `/api/viajes/{viajeId}/actividades`, `/timeline` y documentos heredan el campo automáticamente.
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 108 fuentes).
- **Completado en esta sesión (Integración de Google Places en Actividad — metadatos y eliminación del campo descripción/ubicación):**
  - **`Actividad` refactorizada** (tabla `actividades`): **ELIMINADO físicamente el campo `descripcion`** (columna `descripcion`, el antiguo texto libre de "Ubicación"). Nuevos campos opcionales para Google Places:
    - `googlePlaceId` (String, `length=255`) → columna `google_place_id` (ID del lugar en Google Places).
    - `rating` (Double, nullable) → columna `rating` (puntuación de estrellas 0-5 del lugar).
    - `horarios` (String, `length=500`) → columna `horarios` (resumen horario del lugar).
    - `urlGoogleMaps` (String, `length=500`) → columna `url_google_maps` (enlace directo al lugar en Google Maps).
  - **DTOs:** `ActividadRequestDTO` y `ActividadResponseDTO` pierden `descripcion` y ganan `googlePlaceId`, `rating`, `horarios` y `urlGoogleMaps` (opcionales; con `@Size` máx en requests). El resto del contrato (titulo, tipo, fechas, lat/lng, `direccion`, `notas`, documentos) se conserva intacto.
  - **`ActividadServiceImpl`:** `crearActividad` y `actualizarActividad` mapean los cuatro campos nuevos desde el Request a la entidad (`setGooglePlaceId`, `setRating`, `setHorarios`, `setUrlGoogleMaps`); `toResponseDTO` los expone vía getters. **Cero referencias residuales a `descripcion`/`ubicacion`** (grep global sin resultados). `/api/viajes/{viajeId}/actividades` y `/timeline` heredan los campos automáticamente vía `toResponseDTO`.
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 108 fuentes). El esquema se actualiza solo (`ddl-auto=update`).
- **Completado en esta sesión (Perfil y Ajustes del usuario — cambio de contraseña y preferencias de notificaciones):**
  - **`Usuario.notificacionesGastos` y `Usuario.notificacionesPlanes`** (`boolean`, `nullable=false`, `columnDefinition="boolean default true"`): nuevas columnas `notificaciones_gastos` y `notificaciones_planes`, inicializadas a `true` en la entidad. El esquema las crea automáticamente vía Hibernate (`update`).
  - **`UsuarioResponseDTO` ampliado:** ahora devuelve también `notificacionesGastos` y `notificacionesPlanes` además de `id`, `email`, `nombre` y `avatarUrl`.
  - **DTOs de petición** en el nuevo paquete `dto/request`: **`CambiarPasswordRequest`** (`passwordActual` @NotBlank, `nuevaPassword` @NotBlank + @Size(min=6,max=255)) y **`PreferenciasNotificacionRequest`** (`notificacionesGastos`, `notificacionesPlanes` — ambos @NotNull `Boolean`).
  - **`UsuarioService`/`UsuarioServiceImpl`:** `obtenerPerfil()` (usuario autenticado desde el JWT → `UsuarioResponseDTO`), `cambiarPassword(CambiarPasswordRequest)` (verifica `passwordEncoder.matches(passwordActual, contraseña BD)`; si falla lanza `DatosInvalidosException` → 400 con "La contrasena actual no es correcta"; si acierta codifica y persiste la nueva con BCrypt) y `actualizarPreferencias(PreferenciasNotificacionRequest)` (actualiza **exclusivamente** los dos booleanos de notificaciones y devuelve el perfil actualizado). Se inyectó `PasswordEncoder` por constructor.
  - **`UsuarioController` en `/api/usuarios` (todos bajo JWT):** **`GET /me`** → 200 con `UsuarioResponseDTO`; **`PUT /me/password`** → 204 (`CambiarPasswordRequest`); **`PUT /me/preferencias`** → 200 con `UsuarioResponseDTO` actualizado. Conviven con el existente `POST /me/avatar`.
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 108 fuentes).
- **Completado en esta sesión (Seeder de usuarios de desarrollo):**
  - **`DatabaseSeeder`** (nuevo, en el paquete `config`): `@Component` que implementa `CommandLineRunner` e inyecta `UsuarioRepository` + `PasswordEncoder` por constructor.
  - **Lógica `run()`:** helper `crearUsuarioSiNoExiste(nombre, email)` — comprueba `usuarioRepository.existsByEmail(email)` y, si el correo no existe, crea y guarda el `Usuario` con la contraseña hasheada (`passwordEncoder.encode("password")`). Usuarios inicializados: **Javi** (`javi@viajes.com`) y **Cynthia** (`cynthia@viajes.com`). El rol queda en el default `USER` de la entidad.
  - **Logging profesional:** señales de info con SLF4J (`registrar usuario...`/`Usuarios de desarrollo inicializados correctamente`), sin `System.out.println`, conforme al estilo del proyecto. Se ejecuta solo cuando la BD carece de esos correos (idempotente).
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS, 106 fuentes).
- **Completado en esta sesión (Bugfixes críticos — cascada de gastos y total "Mis Gastos"):**
  - **Cascada de gastos en `ViajeServiceImpl.eliminarViaje`:** eliminada la purga masiva (`deleteByGastoViajeId` + `deleteByViajeId`). Ahora se recuperan todos los `Gasto` del viaje (`gastoRepository.findByViajeIdOrderByFechaDesc`) y, **por cada gasto**, se eliminan primero sus `DeudaGasto` (`deudaGastoRepository.deleteByGastoId`) y después el propio `Gasto` (`gastoRepository.delete`). Se añadió el import `Gasto` en `ViajeServiceImpl`. Resultado: sin violaciones de FK ni registros fantasma/huérfanos al borrar un viaje.
  - **Fix `totalGastadoUsuario` en `GastoServiceImpl.obtenerBalance`:** el campo "Mis Gastos" ahora suma **EXCLUSIVAMENTE** los gastos con `tipoGasto == TipoGasto.PERSONAL` cuyo pagador sea el usuario autenticado. Las cuotas de gastos COMPARTIDOS ya NO se suman a este total (el consumo grupal se refleja en el resumen de deudas, no en el total individual), cuadrando con la interfaz (vista PERSONAL del frontend).
  - **Limpieza de código (refactor):** revisado `GastoServiceImpl` al completo — **no hay importaciones sin uso ni métodos privados obsoletos** tras las refactorizaciones previas de deudas (todas las referencias del archivo están en uso; el import `TipoGasto` se reutiliza ahora también en `obtenerBalance`). No se eliminó nada adicional.
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS).
- **Completado en esta sesión (Refactor: eliminación del enum `DEUDA_PAGADOR`):**
  - **`TipoDivision` simplificado:** borrado físicamente el valor `DEUDA_PAGADOR`. El enum queda con `PARTES_IGUALES`, `PORCENTAJE` y `DEUDA_TERCERO` (Javadoc actualizado). `DEUDA_TERCERO` cubre el escenario de deuda íntegra contra un único participante.
  - **`GastoServiceImpl.generarDeudas`:** el `switch(tipoDivision)` ya no contempla el `case DEUDA_PAGADOR`; `deudaCompleta` (100% del importe al usuario del detalle) sigue intacta y solo responde a `DEUDA_TERCERO`. Javadocs de `deudaCompleta`, `DetalleDivisionDTO` y `GastoRequestDTO` actualizados (los detalles obligatorios solo para `PORCENTAJE`/`DEUDA_TERCERO`).
  - Compilación verificada con éxito: `.\mvnw.cmd clean compile` (BUILD SUCCESS).
- **Completado en esta sesión (Edición y borrado de gastos con recálculo de deudas):**
  - **`DELETE /api/viajes/{viajeId}/gastos/{gastoId}`:** ya existía; verificado — purga primero las `DeudaGasto` asociadas (`deudaGastoRepository.deleteByGastoId`) antes de borrar el `Gasto` para evitar violaciones de FK y deudas huérfanas. Devuelve 204.
  - **`PUT /api/viajes/{viajeId}/gastos/{gastoId}`** (nuevo) → `GastoService.actualizarGasto(viajeId, gastoId, GastoRequestDTO)` → 200 con `GastoResponseDTO`. Lógica estricta en `GastoServiceImpl`:
    - a) Busca el gasto original y valida que pertenezca al viaje del path.
    - b) Elimina **físicamente** todos los `DeudaGasto` actuales del gasto (`deleteByGastoId`).
    - c) Actualiza `concepto`, `importe`, `tipoGasto`, `tipoDivision` y reasigna el **pagador respetando estrictamente `dto.pagadorId()`** (valida que ese usuario exista y pertenezca al viaje).
    - d) Si es COMPARTIDO, re-ejecuta `generarDeudas` (switch PARTES_IGUALES, PORCENTAJE, DEUDA_TERCERO) generando las nuevas `DeudaGasto`. La REGLA DE ORO se mantiene: el pagador nunca genera una deuda hacia sí mismo. Si es PERSONAL se fuerza `tipoDivision=null` y no se generan deudas.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile` (BUILD SUCCESS).
- **Pendiente (próxima sesión del Módulo Finanzas):** liquidación de deudas ("marcar como pagada" / saldar deudas parciales), endpoints de detalle de deudas por gasto, y conexión del frontend a los gastos avanzados y a `/api/viajes/{id}/balances` (el frontend actual envía `cantidad`/`pagadorId` y deberá migrar al contrato `importe`/`tipoGasto`/`tipoDivision`/`detalles`).
- **Últimos commits:** `fix(backend): add detailed error logging for Gemini API calls`, `feat(backend): integrate Gemini API for automated itinerary generation`, `fix(backend): handle same currency conversion and prevent ClassCastException on numeric rates`, `feat(backend): add currencies endpoint and append update date to conversion response`, `feat(backend): implement DivisaService using Frankfurter API for currency conversion`, `feat(backend): add descripcionGoogle field for Places editorial summary`, `refactor(backend): integrate Google Places metadata fields and remove legacy location field`, `feat(backend): add profile settings, password change and notification preferences`, `feat(backend): implement edit and delete logic for expenses with debt recalculation`, `fix(backend): map presupuestoBase correctly into BalanceResponseDTO`, `feat(backend): add endpoint to update individual budget`, `feat(backend): Implement expense splitting algorithms and balance calculations`, `feat(backend): Create financial entities, budgets, enums and relationships`, `feat(backend): Add trip settings endpoints (cover photo, update details, kick participant)`, `feat(backend): Create Album and Foto entities, S3 integration and endpoints for Gallery`, `feat: Borrado en cascada R2 y UI multi-documento en el frontend`, `refactor: Soporte multi-documento 1:N y borrado inteligente en R2`, `feat: Endpoint eliminar viaje y arquitectura backend para Checklist`, `feat: Integración de Cloudflare R2 (S3) y endpoint para subida de documentos`.
- **Completado en esta sesión (Fix mapeo de presupuesto en balances):**
  - **`GastoServiceImpl.obtenerBalance`:** el campo `presupuestoGlobal` del `BalanceResponseDTO` ahora se rellena con `viaje.getPresupuestoBase()` (el techo economico configurado en la creacion del viaje) en lugar de `viaje.getPresupuestoGlobal()`. El `presupuesto_global` es un campo nuevo con default 0.0 que aún no se sincroniza desde el frontend; el presupuesto real del grupo es el histórico `presupuesto_base`.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile` (BUILD SUCCESS).
- **Pendiente (próxima sesión del Módulo Finanzas):** liquidación de deudas ("marcar como pagada" / saldar deudas parciales), endpoints de detalle de deudas por gasto, y conexión del frontend a los gastos avanzados y a `/api/viajes/{id}/balances` (el frontend actual envía `cantidad`/`pagadorId` y deberá migrar al contrato `importe`/`tipoGasto`/`tipoDivision`/`detalles`).
- **Completado en esta sesión (Módulo Finanzas — Fase 2: algoritmos de división y balances):**
  - **DTOs:** nuevo `DetalleDivisionDTO` (`usuarioId`, `valorAsignado` — % o importe), `DeudaResumenDTO` (`deudorId`, `deudorNombre`, `acreedorId`, `acreedorNombre`, `importe`) y `BalanceResponseDTO` (`presupuestoGlobal`, `totalGastadoGrupo`, `presupuestoIndividualUsuario`, `totalGastadoUsuario`, `List<DeudaResumenDTO> deudas`). `GastoRequestDTO` ampliado con `List<DetalleDivisionDTO> detalles` (opcional; obligatorio para PORCENTAJE/DEUDA_*, ignorado en PARTES_IGUALES y PERSONAL).
  - **Excepción `DatosInvalidosException`** (nueva) → HTTP 400, registrada en `GlobalExceptionHandler` para validaciones de negocio (ej. porcentajes que no suman 100).
  - **`GastoServiceImpl.crearGasto` refactorizado:** REGLA 1 — gasto `PERSONAL` se guarda asignado al pagador y NO genera `DeudaGasto` (`tipoDivision=null`). REGLA 2 — gasto `COMPARTIDO` resuelve la división con `switch(tipoDivision)`:
    - `PARTES_IGUALES`: reparte el importe entre los participantes **activos** del viaje (query nueva `findByViajeIdAndEstadoInvitacionAndActivoTrue`), con reparto de centimos (el último absorbe el remanente para que la suma coincida con el importe).
    - `PORCENTAJE`: calcula el importe por participante desde `detalles` y valida que los porcentajes sumen 100 (`DatosInvalidosException`); persiste `porcentajeAsignado`.
    - `DEUDA_TERCERO`: exige exactamente 1 detalle, asigna el 100% del importe al participante indicado y crea un único `DeudaGasto` hacia el pagador. (El valor `DEUDA_PAGADOR` se eliminó en una sesión posterior por redundante.)
    - Todos los usuarios de la división deben pertenecer al viaje (`AccesoDenegadoException` en caso contrario).
  - **`obtenerBalance(viajeId)` (nuevo en `GastoService`/`GastoServiceImpl`):** calcula `totalGastadoGrupo` (suma de los importes de todos los gastos) y `totalGastadoUsuario` (suma de los importes que pagó el usuario autenticado, incluyendo sus gastos personales), lee el `presupuestoGlobal` del viaje y el `presupuestoIndividual` de la pertenencia del usuario. **Simplificación de deudas:** construye una matriz dirigida deudor→acreedor a partir de los `DeudaGasto` del viaje (el acreedor es siempre el pagador del gasto), compensa las deudas cruzadas entre cada pareja (si A debe B y B debe A, queda solo la deuda neta en un sentido) y devuelve la lista ordenada por importe desc.
  - **`BalanceController`** (nuevo) en `/api/viajes/{viajeId}/balances`: `GET` → 200 con `BalanceResponseDTO`. Requiere JWT + pertenencia (el servicio valida). El `POST /api/viajes/{viajeId}/gastos` ya usa el nuevo algoritmo de división de forma transparente (sin cambios en el controlador).
  - Compilación verificada con éxito: `./mvnw.cmd clean compile` (BUILD SUCCESS).
- **Pendiente (próxima sesión del Módulo Finanzas):** liquidación de deudas ("marcar como pagada" / saldar deudas parciales), endpoints de detalle de deudas por gasto, y conexión del frontend a los gastos avanzados y a `/api/viajes/{id}/balances` (el frontend actual envía `cantidad`/`pagadorId` y deberá migrar al contrato `importe`/`tipoGasto`/`tipoDivision`/`detalles`).
- **Completado en esta sesión (Módulo Finanzas — Fase Estructural: presupuestos, enums, Gasto avanzado y DeudaGasto):**
  - **Presupuestos:** `Viaje.presupuestoGlobal` (BigDecimal, `numeric(12,2) default 0.0`, inicializado a `BigDecimal.ZERO`) amplía el presupuesto base existente. `ViajeResponseDTO` y `ViajeUpdateDTO` lo exponen (en Update es opcional; si llega `null` el valor actual se conserva). `UsuarioViaje.presupuestoIndividual` (BigDecimal, mismo default) se añade a `ParticipanteResponseDTO`.
  - **Enums nuevos** en `model/enums`: `TipoGasto` (PERSONAL, COMPARTIDO) y `TipoDivision` (PARTES_IGUALES, PORCENTAJE, DEUDA_PAGADOR, DEUDA_TERCERO).
  - **Entidad `Gasto` refactorizada** (tabla `gastos`): id UUID auto, `concepto` (200), `importe` (BigDecimal 12,2, sustituye a `cantidad`), `fecha` (LocalDateTime, por defecto ahora), `tipoGasto` (Enum STRING no nulo), `tipoDivision` (Enum STRING nullable: null para gastos personales). Relaciones: `@ManyToOne Viaje` y `@ManyToOne Usuario pagador` (sustituye al antiguo `pagadorId` UUID).
  - **Entidad `DeudaGasto`** (tabla `deudas_gastos`): id UUID auto, `@ManyToOne Gasto` (no nulo), `@ManyToOne Usuario deudor` (no nulo), `importeDeuda` (BigDecimal 12,2, cantidad exacta que debe), `porcentajeAsignado` (BigDecimal 5,2, nullable, útil en divisiones PORCENTAJE).
  - **Repositorios:** `GastoRepository` actualizado (`findByViajeIdOrderByFechaDesc`, `deleteByViajeId` por el cambio de campo) y nuevo `DeudaGastoRepository` (`findByGastoId`, `findByGastoViajeId`, `deleteByGastoId`, `deleteByGastoViajeId`).
  - **`GastoServiceImpl` adaptado:** valida que el **pagador pertenezca al viaje** (seguridad por propiedad de recursos), aplica defaults (`tipoGasto=COMPARTIDO`, `tipoDivision=PARTES_IGUALES` si son null), fuerza `tipoDivision=null` en gastos PERSONAL y el `DELETE` purga primero las deudas (`deleteByGastoId`) para no violar la FK. `GastoResponseDTO` ahora devuelve `importe`, `fecha`, `tipoGasto`, `tipoDivision`, `pagadorId` y `pagadorNombre`.
  - **`ViajeServiceImpl`:**
    - `crearViaje` y ambos `toResponseDTO` (de pertenencia y de viaje+rol) rellenan el nuevo campo `presupuestoGlobal`.
    - `obtenerParticipantes` y `obtenerParticipantesDTO` difunden `pertenencia.getPresupuestoIndividual()` en cada `ParticipanteResponseDTO`.
    - `actualizarViaje` (PUT) aplica `request.presupuestoGlobal()` solo si no es null.
    - `eliminarViaje` ahora purga las deudas **antes** que los gastos (`deudaGastoRepository.deleteByGastoViajeId`) dentro del borrado en cascada manual para no violar la FK.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile` (BUILD SUCCESS).
- **Pendiente (próxima sesión del Módulo Finanzas):** lógica de cálculo de división de gastos (reparto a partes iguales / porcentajes), endpoints de deudas y liquidaciones entre participantes, y conexión del frontend a los gastos avanzados (el frontend actual envía `cantidad`/`pagadorId` y deberá migrar al nuevo contrato `importe`/`tipoGasto`/`tipoDivision`).
- **Completado en esta sesión (Propagación de `avatarUrl` a participantes y fotos de la galería):**
  - **`ParticipanteResponseDTO`** ampliado con el campo **`avatarUrl`** (URL R2 del perfil del usuario; `null` si aún no tiene avatar).
  - **`ViajeServiceImpl`:** los dos mapeos de participantes (`obtenerParticipantes` y `obtenerParticipantesDTO` del `ViajeResponseDTO`) pasan ahora `pertenencia.getUsuario().getAvatarUrl()`. Así el frontend ya puede pintar la foto de perfil tanto en la lista de participantes de un viaje como dentro de cada `ViajeResponseDTO`.
  - **`FotoResponseDTO`** ampliado con el campo **`usuarioAvatarUrl`** (avatar del autor de la foto).
  - **`FotoServiceImpl.toResponseDTO`:** asigna `foto.getAutor().getAvatarUrl()` al nuevo campo.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Avatar de usuario — endpoint de subida con Cloudflare R2):**
  - **`Usuario.avatarUrl` (String, `length=500`, nullable):** nueva columna `avatar_url` en la entidad `Usuario` para guardar la URL pública (Cloudflare R2) de la foto de perfil.
  - **DTOs:** `AuthResponseDTO` ampliado con el campo **`avatarUrl`** (login/register devuelven la URL; `null` si aún no hay avatar). Nuevo **`UsuarioResponseDTO`** (`id`, `email`, `nombre`, `avatarUrl`) como respuesta del perfil.
  - **`AuthServiceImpl`:** `registrar` y `login` construyen `AuthResponseDTO` con `usuario.getAvatarUrl()`.
  - **`UsuarioService`/`UsuarioServiceImpl`** (nuevos): `actualizarAvatar(MultipartFile)` recupera el usuario autenticado desde el JWT (`SecurityContextHolder` → `findByEmail`), hace **borrado inteligente** (purga el `avatarUrl` anterior de R2 vía `StorageService.eliminarDocumento`, no-op si es null), sube la nueva imagen con `StorageService.subirDocumento` (misma whitelist de imágenes) y persiste la URL en el usuario. Devuelve `UsuarioResponseDTO`.
  - **`UsuarioController`** (nuevo) en `/api/usuarios`: **`POST /me/avatar`** (multipart `file`) → 200 con `UsuarioResponseDTO`. Exige JWT (cae bajo `anyRequest().authenticated()` de `SecurityConfig`; se creó un controlador nuevo fuera de `/api/auth/**` para no exponerlo públicamente).
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Panel de Ajustes del Viaje — portada, edición de datos y expulsar participante):**
  - **`Viaje.fotoPortada` (String, `length=500`, nullable):** nueva columna `foto_portada` para guardar la URL pública (Cloudflare R2) de la foto de portada del viaje.
  - **`ViajeResponseDTO` ampliado:** ahora devuelve `fotoPortada` y **`participantes` (`List<ParticipanteResponseDTO>` con id, nombre, email, rolEnViaje)**. Se añadió el helper `obtenerParticipantesDTO(Viaje)` (filtra ACEPTADAS) y un segundo `toResponseDTO(viaje, rolEnViaje)`; `crearViaje` y el `toResponseDTO` de pertenencia se actualizaron a la nueva firma.
  - **`ViajeUpdateDTO`** (nuevo): `titulo` (@NotBlank, máx 200), `fechaInicio` y `fechaFin` (@NotNull) para editar los datos base (el nombre del viaje se modela como `titulo`, igual que en `ViajeRequestDTO`).
  - **`PUT /api/viajes/{id}`** en `ViajeController` → `actualizarViaje(viajeId, ViajeUpdateDTO)`: valida ORGANIZADOR (`verificarOrganizador`, helper nuevo que exige `ROL_PROPIETARIO`), actualiza titulo/fechas y devuelve el `ViajeResponseDTO` actualizado.
  - **`POST /api/viajes/{id}/portada`** (multipart `file`) → `actualizarFotoPortada`: valida ORGANIZADOR, hace **borrado inteligente** (purga la portada anterior de R2 vía `storageService.eliminarDocumento`, no-op si es null), sube la nueva con `StorageService.subirDocumento` (whitelist incluye jpg/jpeg/png/gif/webp), persiste la URL y devuelve el viaje actualizado.
  - **`DELETE /api/viajes/{viajeId}/participantes/{usuarioId}`** → `expulsarParticipante`: **solo ORGANIZADOR**, rechaza auto-expulsión ("El organizador no puede expulsarse a si mismo"), localiza la pertenencia usuario-viaje y **elimina físicamente el registro** (abandono forzado). El usuario expulsado puede re-unirse luego vía UUID (`responderInvitacion` recrea la pertenencia) o volver a ser invitado.
  - **Nota de enrutado:** la ruta literal `DELETE /api/viajes/{id}/participantes/me` (abandonar) coexiste con `/{usuarioId}`; Spring resuelve la literal `/me` con mayor prioridad, sin conflicto.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Módulo Galería Multimedia — Álbumes y Fotos con autor):**
  - **Nueva entidad `Album`** (tabla `albumes`): `id` UUID auto, `nombre` (String 200), `fechaCreacion` (LocalDateTime) y `@ManyToOne` hacia `Viaje`. Nuevo `AlbumRepository` con `findByViajeIdOrderByFechaCreacionDesc` + `deleteByViajeId`.
  - **Nueva entidad `Foto`** (tabla `fotos`): `id` UUID auto, `url` (String 500, ruta Cloudflare R2), `fechaSubida` (LocalDateTime), y `@ManyToOne` hacia `Viaje` (no nulo), `Album` (nullable: fotos sin álbum) y `Usuario` como **autor** (no nulo, el que subió la foto para futuros filtros en el frontend). Nuevo `FotoRepository` con `findByViajeIdOrderByFechaSubidaDesc`, `findByViajeIdAndAlbumIdOrderByFechaSubidaDesc`, `findByAlbumId(OrderByFechaSubidaDesc)` + `deleteByViajeId`.
  - **DTOs:** `AlbumRequestDTO` (`nombre` con `@NotBlank` + `@Size(max=200)`) y `AlbumResponseDTO` (`id`, `viajeId`, `nombre`, `fechaCreacion`). **`FotoResponseDTO` incluye la información básica del autor** (`usuarioId`, `usuarioNombre`) además de `albumId` (opcional), `url`, `fechaSubida` y `viajeId` — preparado para pintar el avatar del autor en el frontend.
  - **`AlbumService`/`AlbumServiceImpl`:** `obtenerAlbumes`, `crearAlbum` y `eliminarAlbum`. Al eliminar un álbum las fotos NO se borran: se desasocian (`foto.setAlbum(null)`) y pasan a la galería "sin álbum". Todo con verificación de pertenencia.
  - **`FotoService`/`FotoServiceImpl`:** `obtenerFotos(viajeId, albumId)` (filtro opcional por álbum, validando que pertenezca al viaje), `subirFoto(viajeId, albumId, archivo)` y `eliminarFoto`. **La subida reutiliza `StorageService.subirDocumento`** (Cloudflare R2: whitelist PDF/imagen, UUID como nombre, devuelve URL pública; la whitelist ya incluye jpg/jpeg/png/gif/webp) y **el autor se extrae del JWT** (`obtenerUsuarioAutenticado` → `UsuarioRepository.findByEmail`). El `DELETE` hace borrado inteligente: primero `StorageService.eliminarDocumento(url)` (purga física en R2) y luego borra el registro de la BD.
  - **Controladores:** `AlbumController` en `/api/viajes/{viajeId}/albumes` (GET, POST 201, DELETE 204) y `FotoController` en `/api/viajes/{viajeId}/fotos` (GET con `?albumId=` opcional, POST multipart `file` + `albumId` opcional → 201, DELETE 204). Todos con JWT + pertenencia.
  - **`ViajeServiceImpl.eliminarViaje` actualizado:** el borrado en cascada manual ahora purga también las fotos de Cloudflare R2 (`FotoRepository.findByViajeId` → `eliminarDocumento`) y elimina `fotos` **antes** que `albumes` para no violar la FK.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (CRUD completo de Actividades — endpoint PUT):**
  - **`ActividadService.actualizarActividad(UUID viajeId, UUID actividadId, ActividadRequestDTO dto)`** (nuevo método en interfaz + implementación): valida pertenencia del usuario al viaje, comprueba que la actividad pertenece al viaje indicado y actualiza `titulo`, `descripcion`, `tipoActividad`, `fechaHoraInicio`, `fechaHoraFin`, `latitud`, `longitud` y `direccion`. Devuelve el `ActividadResponseDTO` actualizado (manteniendo los documentos existentes).
  - **Nuevo endpoint `PUT /api/viajes/{viajeId}/actividades/{actividadId}`** en `ActividadController` (200 OK con body). El `DELETE /api/viajes/{viajeId}/actividades/{actividadId}` (204) ya existía (con purga física de documentos de Cloudflare R2).
  - **CORS:** verificado en `SecurityConfig` — `allowedMethods` ya incluye `PUT` y `DELETE` (y `allowedOriginPatterns="*"`), sin cambios necesarios.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Geolocalización — coordenadas y dirección en Actividad):**
  - **`Actividad` ahora soporta geolocalización completa:** `latitud` y `longitud` (Double, nullable) ya existían; se añade **`direccion`** (`String`, nullable, `length = 255`). El modelo queda preparado para el mapa Leaflet + geocodificación Nominatim del frontend.
  - **DTOs:** `ActividadRequestDTO` y `ActividadResponseDTO` exponen los tres campos (`latitud`, `longitud`, `direccion`), todos opcionales. `direccion` valida `@Size(max = 255)`.
  - **`ActividadServiceImpl`:** `crearActividad` mapea `dto.direccion() → actividad.setDireccion(...)` y `toResponseDTO` devuelve el campo en la respuesta. El resto de endpoints (lista, timeline, documentos) heredan el campo automáticamente vía `toResponseDTO`.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Borrado lógico — abandonar viaje invitado + filtro listado):**
  - **`UsuarioViaje.activo` (boolean, default true, `columnDefinition = "boolean default true"`):** la pertenencia ahora se puede desactivar sin borrado físico, preservando la integridad histórica de gastos (pagadorId) y listas compartidas.
  - **`ViajeServiceImpl.obtenerMisViajes`:** añadido `.filter(UsuarioViaje::isActivo)` → `GET /api/viajes` solo devuelve viajes donde el usuario es el Creador (PROPIETARIO, siempre `activo=true`) o invitado con `activo==true`.
  - **`ViajeServiceImpl.responderInvitacion`:** si una pertenencia ACEPTADA estaba `activo=false` (abandonada), volver a unirse con el código la reactiva (`activo=true`).
  - **`ViajeService.abandonarViaje(UUID viajeId)` / `ViajeServiceImpl`:** valida pertenencia del usuario autenticado, rechaza con `AccesoDenegadoException` si el usuario es PROPIETARIO ("El propietario no puede abandonar el viaje") y marca la pertenencia como `activo=false`. Nuevo endpoint `DELETE /api/viajes/{id}/participantes/me` (204).
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Borrado físico en cascada — purga de R2 al borrar Actividad/Viaje):**
  - **`ActividadServiceImpl.eliminarActividad`:** antes de borrar la actividad de la BD, itera sobre `actividad.getDocumentos()` y llama a `StorageService.eliminarDocumento(url)` por cada uno (purga física en Cloudflare R2). El borrado en cascada JPA (`cascade=ALL` + `orphanRemoval`) elimina después las filas de `documentos`.
  - **`ViajeServiceImpl.eliminarViaje`:** ahora inyecta `StorageService`. Antes del borrado de filas, obtiene todos los documentos del viaje con el nuevo método del repositorio `DocumentoRepository.findByActividadViajeId(viajeId)` y llama a `eliminarDocumento(url)` por cada uno (purga física). Después sigue con el borrado en cascada manual (`documentos` → `actividades` → `usuarios_viajes` → viaje).
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Refactor multi-documento 1:N — borrado inteligente en R2):**
  - **Nueva entidad `Documento`** (tabla `documentos`): `id` (UUID auto), `nombreOriginal`, `urlArchivo` y `@ManyToOne(fetch = LAZY)` hacia `Actividad`. Nuevo `DocumentoRepository`.
  - **`Actividad` refactorizada:** eliminado el campo `urlDocumento` (era 1:1); ahora tiene `@OneToMany(mappedBy="actividad", cascade=ALL, orphanRemoval=true) List<Documento> documentos`.
  - **DTOs:** nuevo `DocumentoDTO` (`id`, `nombreOriginal`, `urlArchivo`). `ActividadRequestDTO` pierde `urlDocumento`. `ActividadResponseDTO` ahora devuelve `List<DocumentoDTO> documentos`. El `DocumentoDTO` se construye dentro de la transacción (colección LAZY).
  - **`StorageService.eliminarDocumento(String urlArchivo)`: extrae la clave del objeto cortando por el último "/" de la URL pública y ejecuta `DeleteObjectRequest` con el `S3Client` de Cloudflare R2 (borrado físico). URL vacía → no-op; fallos → `StorageException` (500).** Reemplaza al intento 1:1 anterior.
  - **Endpoints de actividad (documentos):**
    - `POST /api/viajes/{viajeId}/actividades/{actividadId}/documentos` (multipart `file`) → sube a R2, crea el registro `Documento` (nombre original + URL R2) asociado a la Actividad y devuelve el `ActividadResponseDTO` actualizado.
    - `DELETE /api/viajes/{viajeId}/actividades/{actividadId}/documentos/{documentoId}` (204) → borrado inteligente: busca el `Documento`, valida pertenencia al viaje/actividad, llama a `StorageService.eliminarDocumento(raw)` para borrar de R2 y finalmente elimina la entidad de la BD.
  - **`ViajeService.eliminarViaje`:** el borrado en cascada manual ahora elimina `documentos` (vía `DocumentoRepository.deleteByViajeId`) **antes** que `actividades` para no violar la FK.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Fix 403 invitados + participantes):**
  - **Fix 403 "unirse por UUID":** el 403 NO venía de `SecurityConfig` (solo exige JWT salvo `/api/auth/**`) sino de `ViajeServiceImpl.responderInvitacion`, que exigía un registro `UsuarioViaje` PENDIENTE previo. Ahora, si el usuario autenticado no tiene pertenencia y `aceptar=true` (el flujo del frontend `unirseViaje(uuid)` → `PUT /api/viajes/{id}/responder-invitacion?aceptar=true`), se le crea automáticamente la pertenencia como **INVITADO/ACEPTADA** usando el UUID del viaje como código de invitación. También se hace idempotente: si ya es miembro ACEPTADA, retorna 200 sin error.
  - **Endpoint `GET /api/viajes/{id}/participantes`:** devuelve `List<ParticipanteResponseDTO>` (`id`, `nombre`, `email`, `rolEnViaje`) de los usuarios con pertenencia **ACEPTADA**. Nuevo DTO `ParticipanteResponseDTO`. Verifica la pertenencia del usuario autenticado antes de listar. Lo consume el frontend para el selector de asignación de la checklist.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Integración Cloudflare R2 — subida de documentos de actividades):**
  - **Dependencias:** AWS SDK for Java v2 (`software.amazon.awssdk:bom:2.51.3` importado en `dependencyManagement` + módulo `software.amazon.awssdk:s3`).
  - **Propiedades (`application.properties`):** `cloudflare.r2.access-key`, `cloudflare.r2.secret-key`, `cloudflare.r2.endpoint`, `cloudflare.r2.bucket`, `cloudflare.r2.public-url` con placeholders (`TU_CLAVE`, etc.) que el usuario debe rellenar.
  - **`CloudflareR2Config`** (nuevo paquete `config`): bean `S3Client` con `endpointOverride` apuntando al endpoint personalizado de la cuenta R2, región `auto` y credenciales estáticas inyectadas desde propiedades.
  - **`StorageService`/`StorageServiceImpl`:** `subirDocumento(MultipartFile)` genera un UUID como nombre preservando la extensión, valida whitelist de extensiones (pdf, jpg, png, docx...), sube al bucket con `PutObjectRequest` y devuelve `public-url + "/" + nombreGenerado`. Fallos de subida → `StorageException` (500).
  - **Endpoint `POST /api/viajes/{viajeId}/actividades/{actividadId}/documento`:** consume `multipart/form-data` con `@RequestParam("file")`, llama a `StorageService`, persiste la URL en `Actividad.urlDocumento` (via `ActividadService.guardarUrlDocumento`, que valida pertenencia) y devuelve el `ActividadResponseDTO` actualizado.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Completado en esta sesión (Checklist v2 + HOTFIX eliminación de viaje):**
  - **HOTFIX `DELETE /api/viajes/{id}`:** `ViajeController` expone `DELETE /api/viajes/{id}` (204 No Content). `ViajeService.eliminarViaje(id)` verifica que el usuario autenticado sea **PROPIETARIO** del viaje y realiza un **borrado en cascada manual** (las entidades hijas mapean `@ManyToOne → Viaje` sin colecciones `@OneToMany`, no hay `OrphanRemoval`): elimina `tareas_checklist`, `gastos`, `actividades` y `usuarios_viajes` (vía `deleteByViajeId` en cada repositorio) antes de borrar el viaje, evitando violaciones de Foreign Key. Se añadieron `deleteByViajeId` a `ActividadRepository`, `GastoRepository`, `UsuarioViajeRepository` y `TareaChecklistRepository`.
  - **Checklist v2 — `TareaChecklist` (sustituye a `ItemChecklist`):**
    - **Entidad `TareaChecklist`** (tabla `tareas_checklist`): `id` Long auto-incremental (`IDENTITY`), `viaje` (ManyToOne), `creador` (ManyToOne Usuario, no nulo), `asignado` (ManyToOne Usuario, nullable), `contenido` (String 200), `completada` (Boolean), `esPrivada` (Boolean). Modela las listas **común** (`esPrivada=false`) y **privada** (`esPrivada=true`) del frontend, con creador y asignado.
    - **DTOs:** `TareaChecklistRequestDTO` (`contenido`, `esPrivada`, `asignadoId`) y `TareaChecklistResponseDTO` (`id`, `contenido`, `completada`, `esPrivada`, `nombreCreador`, `nombreAsignado`). El `creador` nunca viaja en el request: se obtiene del JWT.
    - **`TareaChecklistRepository`:** query personalizada `findVisiblesPorViajeYUsuario(viajeId, usuarioId)` → devuelve tareas públicas **o** creadas por el usuario autenticado (protege las listas privadas de otros) + `deleteByViajeId`.
    - **`TareaChecklistService`/`TareaChecklistServiceImpl`:** `obtenerTareas` (filtro visibilidad), `crearTarea`, `cambiarEstado(completada)`, `asignarUsuario` (valida que el asignado pertenezca al viaje) y `eliminarTarea`. Todas verifican pertenencia del usuario autenticado.
    - **`TareaChecklistController`** en `/api/viajes/{viajeId}/checklist` (JWT): `POST` (201), `GET`, `PUT /{tareaId}/estado?completada=`, `PUT /{tareaId}/asignar?asignadoId=` y `DELETE /{tareaId}` (204).
    - **ELIMINADO el módulo previo `ItemChecklist`** (`ItemChecklist`, `ItemChecklistRepository`, DTOs `ItemChecklist*`, `ChecklistService`/`ChecklistServiceImpl`, `ChecklistController`) por colisión de ruta y por no soportar esPrivada/asignado/creador. Confirmado por el usuario.
  - Compilación verificada con éxito: `./mvnw.cmd clean compile`.
- **Pendiente (fuera del backend):**
  - Conectar el checklist del frontend Angular a estos nuevos endpoints (sustituir el `localStorage` por llamadas a la API).
  - Conectar la Galería Multimedia del frontend a `/api/viajes/{id}/albumes` y `/api/viajes/{id}/fotos`.
  - Rate Limiting en proxy inverso Nginx (previsión DoS a nivel de infraestructura).
  - Revisar el cifrado/entorno de claves JWT y DeepL en producción.
- **Completado en esta sesión (Fase 5: Módulos Secundarios, Archivos Seguros y multi-idioma):**
  - **Checklist (Items):** Entidad `ItemChecklist` (id, viaje, nombre, completado), `ItemChecklistRepository`, `ChecklistService`/`ChecklistServiceImpl` (verificación de pertenencia), `ChecklistController` en `/api/viajes/{viajeId}/checklist`.
  - **Gastos (Presupuesto):** Entidad `Gasto` (id, viaje, concepto, cantidad BigDecimal, pagadorId), `GastoRepository`, `GastoService`/`GastoServiceImpl`, `GastoController` en `/api/viajes/{viajeId}/gastos`.
  - **Diccionario multi-idioma:** Entidad `Diccionario` con nuevo campo `idioma` (ISO: JA/EN/FR), `DiccionarioRepository` con `findByIdioma` y `findByIdiomaAndCategoria`, DTOs con campo `idioma`.
  - **TraductorService (DeepL):** Endpoint `POST /api/diccionario/traducir` que recibe `TraduccionRequestDTO` (texto, idiomaDestino) y llama a DeepL vía `RestClient`. API key configurable con `deepl.api.key` (placeholder; 503 si no está configurada). Excepción `TraduccionException` → 503.
  - **Archivos Seguros:** `ArchivoService`/`ArchivoServiceImpl` guardan archivos físicamente en `uploads/{viajeId}/` aislado por viaje, con whitelist de extensiones seguras, protección contra path traversal y validación de pertenencia. `ArchivoController` en `/api/viajes/{viajeId}/archivos` (`POST` subida y `GET /{nombreArchivo}` descarga como flujo de bytes).
  - **Prevención DoS (Fase 5):** `spring.servlet.multipart.max-file-size=5MB` y `max-request-size=5MB`.
  - **datos iniciales:** `data.sql` con frases de vocabulario JA + `spring.jpa.defer-datasource-initialization=true`.
  - Compilación verificada con éxito: `mvn clean compile`.
- **Backend finalizado:** Las 5 fases del roadmap backend están completadas.
- **Pendiente (fuera del backend):**
  - Frontend Angular (aún no iniciado).
  - Rate Limiting en proxy inverso Nginx (previsión DoS a nivel de infraestructura).
  - Revisar el cifrado/entorno de claves JWT y DeepL en producción.
- **Notas técnicas:**
  - La clave JWT del `application.properties` es de desarrollo; debe sobrescribirse vía variables de entorno en producción.
  - `deepl.api.key` es un placeholder vacío; definirla para activar la traducción.
  - El rol se mapea como `ROLE_USER` / `ROLE_ADMIN` (SimpleGrantedAuthority).
  - Endpoints `/api/auth/**` son públicos (`permitAll`); el resto de rutas requieren JWT.
  - La autorización de viajes se basa en la tabla `usuarios_viajes` (pertenencia/propiedad de recursos). Todos los módulos de viaje (actividades, checklist, gastos, archivos) validan la pertenencia en cada operación.
- **Últimos commits:** `feat(backend): implement edit and delete logic for expenses with debt recalculation`, `fix(backend): map presupuestoBase correctly into BalanceResponseDTO`, `feat(backend): add endpoint to update individual budget`, `feat(backend): Implement expense splitting algorithms and balance calculations`, `feat(backend): Create financial entities, budgets, enums and relationships`, `feat(backend): Add trip settings endpoints (cover photo, update details, kick participant)`, `feat(backend): Create Album and Foto entities, S3 integration and endpoints for Gallery`, `feat: Borrado en cascada R2 y UI multi-documento en el frontend`, `refactor: Soporte multi-documento 1:N y borrado inteligente en R2`, `feat: Endpoint eliminar viaje y arquitectura backend para Checklist`, `feat: Integración de Cloudflare R2 (S3) y endpoint para subida de documentos`.

---

## 🔌 Registro de Endpoints de la API
*El agente documentará aquí cada nuevo endpoint funcional que se vaya integrando.*

| Método | Ruta | Descripción | Seguridad (JWT / Roles) |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/register` | Registro de nuevo usuario. Devuelve 201 Created con token JWT | Público |
| POST | `/api/auth/login` | Inicio de sesión. Devuelve 200 OK con token JWT | Público |
| GET | `/api/usuarios/me` | Perfil del usuario autenticado (`id`, `email`, `nombre`, `avatarUrl`, `notificacionesGastos`, `notificacionesPlanes`) | JWT |
| PUT | `/api/usuarios/me/password` | Cambiar contraseña (`passwordActual`, `nuevaPassword`). 204 si es correcta; 400 si la actual no coincide (BCrypt) | JWT |
| PUT | `/api/usuarios/me/preferencias` | Actualizar preferencias de notificaciones (`notificacionesGastos`, `notificacionesPlanes`). Devuelve `UsuarioResponseDTO` actualizado | JWT |
| POST | `/api/usuarios/me/avatar` | Subir/actualizar avatar (multipart `file`, máx 5MB). Borra el avatar anterior de R2 antes de guardar el nuevo. Devuelve `UsuarioResponseDTO` | JWT |
| POST | `/api/viajes` | Crear un viaje (creador queda como PROPIETARIO) | JWT |
| GET | `/api/viajes` | Listar los viajes del usuario autenticado (ACEPTADOS) | JWT |
| POST | `/api/viajes/{id}/invitar` | Invitar a un usuario (solo PROPIETARIO) | JWT |
| PUT | `/api/viajes/{id}/responder-invitacion` | Aceptar/Rechazar invitación pendiente (o unirse por UUID) | JWT |
| GET | `/api/viajes/{id}/participantes` | Listar participantes con pertenencia ACEPTADA | JWT |
| DELETE | `/api/viajes/{id}/participantes/me` | Abandonar el viaje en el que eres invitado (borrado lógico: `activo=false`) | JWT |
| DELETE | `/api/viajes/{id}/participantes/{usuarioId}` | Expulsar participante (abandono forzado: elimina el vínculo usuario-viaje). Solo ORGANIZADOR, no puede expulsarse a sí mismo | JWT (ORGANIZADOR) |
| DELETE | `/api/viajes/{id}` | Eliminar un viaje (solo PROPIETARIO, borrado en cascada manual) | JWT |
| PUT | `/api/viajes/{id}` | Actualizar datos base del viaje (`titulo`, `fechaInicio`, `fechaFin`). Devuelve `ViajeResponseDTO` con portada y participantes. Solo ORGANIZADOR | JWT (ORGANIZADOR) |
| PUT | `/api/viajes/{id}/presupuesto-individual` | Actualizar el presupuesto individual del participante autenticado (body `{"presupuesto": número}`). Devuelve 200 con el `ParticipanteResponseDTO` actualizado | JWT |
| POST | `/api/viajes/{id}/portada` | Subir foto de portada a Cloudflare R2 (multipart `file`, máx 5MB). Purga la anterior de R2. Solo ORGANIZADOR | JWT (ORGANIZADOR) |
| POST | `/api/viajes/{viajeId}/actividades` | Crear una actividad en el viaje (`googlePlaceId`, `rating`, `horarios`, `urlGoogleMaps`, `descripcionGoogle`, `direccion`, `latitud`, `longitud`, `notas` — todos opcionales) | JWT |
| GET | `/api/viajes/{viajeId}/actividades` | Listar actividades del viaje ordenadas por fecha | JWT |
| GET | `/api/viajes/{viajeId}/actividades/timeline?fecha=YYYY-MM-DD` | Actividades de un día concreto (minicalendario) | JWT |
| PUT | `/api/viajes/{viajeId}/actividades/{actividadId}` | Actualizar una actividad del viaje (título, fechas, metadatos Google Places incluido `descripcionGoogle`, ubicación, coordenadas). Devuelve el `ActividadResponseDTO` actualizado | JWT |
| DELETE | `/api/viajes/{viajeId}/actividades/{actividadId}` | Eliminar una actividad del viaje | JWT |
| POST | `/api/viajes/{viajeId}/actividades/{actividadId}/documentos` | Subir documento de una actividad a Cloudflare R2 (multipart, `file`, máx 5MB). Crea el registro `Documento` y lo asocia a la actividad | JWT |
| DELETE | `/api/viajes/{viajeId}/actividades/{actividadId}/documentos/{documentoId}` | Borrar documento (204). Borra físicamente de R2 (`StorageService.eliminarDocumento`) y elimina el registro de la BD | JWT |
| POST | `/api/viajes/{viajeId}/checklist` | Crear una tarea de la checklist | JWT |
| GET | `/api/viajes/{viajeId}/checklist` | Listar tareas visibles (públicas o del usuario) | JWT |
| PUT | `/api/viajes/{viajeId}/checklist/{tareaId}/estado?completada=true` | Marcar/desmarcar tarea como completada | JWT |
| PUT | `/api/viajes/{viajeId}/checklist/{tareaId}/asignar?asignadoId=` | Asignar/desasignar usuario a una tarea | JWT |
| DELETE | `/api/viajes/{viajeId}/checklist/{tareaId}` | Eliminar una tarea de la checklist | JWT |
| POST | `/api/viajes/{viajeId}/gastos` | Registrar un gasto del viaje (`concepto`, `importe`, `pagadorId`, `tipoGasto`, `tipoDivision` y `detalles`). Aplica el algoritmo de división (PERSONAL, PARTES_IGUALES, PORCENTAJE, DEUDA_TERCERO) | JWT |
| GET | `/api/viajes/{viajeId}/gastos` | Listar gastos del viaje ordenados por fecha desc | JWT |
| DELETE | `/api/viajes/{viajeId}/gastos/{gastoId}` | Eliminar un gasto (purga previa de sus deudas) | JWT |
| PUT | `/api/viajes/{viajeId}/gastos/{gastoId}` | Actualizar un gasto (`GastoRequestDTO`): elimina las deudas actuales, reasigna pagador por UUID y re-ejecuta el algoritmo de división para regenerar las `DeudaGasto`. Devuelve 200 con `GastoResponseDTO` | JWT |
| GET | `/api/viajes/{viajeId}/balances` | Balance financiero para el usuario autenticado: presupuestos, totales gastados y deudas simplificadas (`BalanceResponseDTO`) | JWT |
| POST | `/api/viajes/{viajeId}/albumes` | Crear un álbum de fotos de la galería (`nombre`). Devuelve 201 con `AlbumResponseDTO` | JWT |
| GET | `/api/viajes/{viajeId}/albumes` | Listar álbumes del viaje ordenados por fecha de creación | JWT |
| DELETE | `/api/viajes/{viajeId}/albumes/{albumId}` | Eliminar un álbum (204). Las fotos se desasocian (pasan a "sin álbum"), no se borran | JWT |
| GET | `/api/viajes/{viajeId}/fotos` | Listar fotos del viaje (opcional `?albumId=` para filtrar por álbum). `FotoResponseDTO` incluye el autor | JWT |
| POST | `/api/viajes/{viajeId}/fotos` | Subir foto a Cloudflare R2 (multipart `file`, `albumId` opcional). El autor se extrae del JWT. Devuelve 201 con `FotoResponseDTO` | JWT |
| DELETE | `/api/viajes/{viajeId}/fotos/{fotoId}` | Borrar foto (204). Borra físicamente de R2 (`StorageService.eliminarDocumento`) y elimina el registro de la BD | JWT |
| POST | `/api/viajes/{viajeId}/archivos` | Subir un archivo (máx 5MB, whitelist de extensiones) | JWT |
| GET | `/api/viajes/{viajeId}/archivos/{nombreArchivo}` | Descargar un archivo como flujo de bytes | JWT |
| GET | `/api/diccionario` | Diccionario (opcional `?idioma=JA` y `?categoria=`) | JWT |
| POST | `/api/diccionario/traducir` | Traducir texto con DeepL (`texto`, `idiomaDestino`) | JWT |
| GET | `/api/clima` | Pronóstico del clima para `lat`, `lon` y `fecha` (YYYY-MM-DD). Dentro de los próximos 14 días usa Open-Meteo; fuera del plazo devuelve fallback histórico (`esHistorico=true`). Devuelve `ClimaResponseDTO` | JWT |
| GET | `/api/divisas/convertir` | Convertir un `amount` entre dos monedas ISO 4217 (`from` default `EUR`, `to` default `JPY`). Usa la API pública de Frankfurter. Devuelve `DivisaResponseDTO` (`montoOriginal`, `monedaOriginal`, `montoConvertido`, `monedaDestino`, `ratioCambio`, `fechaActualizacion` con la fecha oficial del mercado) | JWT |
| GET | `/api/divisas/monedas` | Catálogo de monedas soportadas por Frankfurter (`Map<String,String>` clave-valor, ej. `{"AUD":"Australian Dollar"}`) | JWT |
| POST | `/api/ia/generar` | Generar itinerario de viaje con Google Gemini (`destino`, `dias`, `tematica`). Devuelve `IaItinerarioResponseDTO` con `rawJson` (array de días: `dia`, `titulo`, `actividades`) | JWT |