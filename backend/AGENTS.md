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

- **Última actualización:** 08/08/2026
- **Hito actual:** BACKEND COMPLETADO (Fases 1-5) + **HOTFIX DELETE viaje + Arquitectura Checklist (TareaChecklist) + INTEGRACIÓN CLOUDFLARE R2 (S3) + REFACTOR MULTI-DOCUMENTO 1:N CON BORRADO INTELIGENTE EN CASCADA** COMPLETADOS.
- **Completado en esta sesión (Borrado físico en cascada — purga de R2 al borrar Actividad/Viaje):**
  - **`ActividadServiceImpl.eliminarActividad`:** antes de borrar la actividad de la BD, itera sobre `actividad.getDocumentos()` y llama a `StorageService.eliminarDocumento(url)` por cada uno (purga física en Cloudflare R2). El borrado en cascada JPA (`cascade=ALL` + `orphanRemoval`) elimina después las filas de `documentos`.
  - **`ViajeServiceImpl.eliminarViaje`:** ahora inyecta `StorageService`. Antes del borrado de filas, obtiene todos los documentos del viaje con el nuevo método del repositorio `DocumentoRepository.findByActividadIdViajeId(viajeId)` y llama a `eliminarDocumento(url)` por cada uno (purga física). Después sigue con el borrado en cascada manual (`documentos` → `actividades` → `usuarios_viajes` → viaje).
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
- **Últimos commits:** `feat: Borrado en cascada R2 y UI multi-documento en el frontend`, `refactor: Soporte multi-documento 1:N y borrado inteligente en R2`, `feat: Endpoint eliminar viaje y arquitectura backend para Checklist`, `feat: Integración de Cloudflare R2 (S3) y endpoint para subida de documentos`.

---

## 🔌 Registro de Endpoints de la API
*El agente documentará aquí cada nuevo endpoint funcional que se vaya integrando.*

| Método | Ruta | Descripción | Seguridad (JWT / Roles) |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/register` | Registro de nuevo usuario. Devuelve 201 Created con token JWT | Público |
| POST | `/api/auth/login` | Inicio de sesión. Devuelve 200 OK con token JWT | Público |
| POST | `/api/viajes` | Crear un viaje (creador queda como PROPIETARIO) | JWT |
| GET | `/api/viajes` | Listar los viajes del usuario autenticado (ACEPTADOS) | JWT |
| POST | `/api/viajes/{id}/invitar` | Invitar a un usuario (solo PROPIETARIO) | JWT |
| PUT | `/api/viajes/{id}/responder-invitacion` | Aceptar/Rechazar invitación pendiente (o unirse por UUID) | JWT |
| GET | `/api/viajes/{id}/participantes` | Listar participantes con pertenencia ACEPTADA | JWT |
| DELETE | `/api/viajes/{id}` | Eliminar un viaje (solo PROPIETARIO, borrado en cascada manual) | JWT |
| POST | `/api/viajes/{viajeId}/actividades` | Crear una actividad en el viaje | JWT |
| GET | `/api/viajes/{viajeId}/actividades` | Listar actividades del viaje ordenadas por fecha | JWT |
| GET | `/api/viajes/{viajeId}/actividades/timeline?fecha=YYYY-MM-DD` | Actividades de un día concreto (minicalendario) | JWT |
| DELETE | `/api/viajes/{viajeId}/actividades/{actividadId}` | Eliminar una actividad del viaje | JWT |
| POST | `/api/viajes/{viajeId}/actividades/{actividadId}/documentos` | Subir documento de una actividad a Cloudflare R2 (multipart, `file`, máx 5MB). Crea el registro `Documento` y lo asocia a la actividad | JWT |
| DELETE | `/api/viajes/{viajeId}/actividades/{actividadId}/documentos/{documentoId}` | Borrar documento (204). Borra físicamente de R2 (`StorageService.eliminarDocumento`) y elimina el registro de la BD | JWT |
| POST | `/api/viajes/{viajeId}/checklist` | Crear una tarea de la checklist | JWT |
| GET | `/api/viajes/{viajeId}/checklist` | Listar tareas visibles (públicas o del usuario) | JWT |
| PUT | `/api/viajes/{viajeId}/checklist/{tareaId}/estado?completada=true` | Marcar/desmarcar tarea como completada | JWT |
| PUT | `/api/viajes/{viajeId}/checklist/{tareaId}/asignar?asignadoId=` | Asignar/desasignar usuario a una tarea | JWT |
| DELETE | `/api/viajes/{viajeId}/checklist/{tareaId}` | Eliminar una tarea de la checklist | JWT |
| POST | `/api/viajes/{viajeId}/gastos` | Registrar un gasto del viaje | JWT |
| GET | `/api/viajes/{viajeId}/gastos` | Listar gastos del viaje | JWT |
| DELETE | `/api/viajes/{viajeId}/gastos/{gastoId}` | Eliminar un gasto | JWT |
| POST | `/api/viajes/{viajeId}/archivos` | Subir un archivo (máx 5MB, whitelist de extensiones) | JWT |
| GET | `/api/viajes/{viajeId}/archivos/{nombreArchivo}` | Descargar un archivo como flujo de bytes | JWT |
| GET | `/api/diccionario` | Diccionario (opcional `?idioma=JA` y `?categoria=`) | JWT |
| POST | `/api/diccionario/traducir` | Traducir texto con DeepL (`texto`, `idiomaDestino`) | JWT |