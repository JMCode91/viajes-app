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

### Fase 3: Gestión de Viajes e Invitaciones (Pendiente)
- [ ] Desarrollar CRUD de Viajes (solo lectura/escritura para usuarios pertenecientes al viaje).
- [ ] Implementar lógica de negocio: Enviar invitación a usuario por email/id y aceptar invitación.

### Fase 4: Logística, Calendario y Mapa (PostGIS) (Pendiente)
- [ ] Crear entidad `Actividad` (tipo: vuelo, hotel, excursión) vinculada a `Viaje`.
- [ ] Implementar geolocalización: Guardar coordenadas (latitud/longitud) adaptadas para futura compatibilidad con PostGIS.
- [ ] Desarrollar endpoints para listar actividades de un viaje ordenadas por fecha (Timeline).

### Fase 5: Módulos Secundarios y Archivos Seguros (Pendiente)
- [ ] Entidades y CRUD para `Gastos` (Presupuesto) y `Items` (Checklist).
- [ ] Entidad y CRUD para `Diccionario` (Vocabulario de supervivencia).
- [ ] **Módulo de Archivos Seguros:** Implementar servicio de subida/bajada de archivos (Local o S3) con validación estricta de pertenencia al viaje.

---

## 📊 Estado Actual del Proyecto
*Esta sección será actualizada automáticamente por el agente OpenCode al finalizar cada sesión.*

- **Última actualización:** 05/08/2026
- **Hito actual:** Fase 2 completada. Preparando Fase 3 (Gestión de Viajes e Invitaciones).
- **Completado en esta sesión (Fase 2 - Parte 3: Endpoints de Autenticación):**
  - DTOs en `com.app.viajes.dto`: `RegisterRequestDTO` (nombre, email, password con validaciones), `LoginRequestDTO` (email, password), `AuthResponseDTO` (token, email, nombre).
  - `AuthService` (interfaz) + `AuthServiceImpl` (`@Service`): `registrar` valida email existente, cifra password con BCrypt, asigna `Rol.USER` y devuelve token; `login` usa `AuthenticationManager`.
  - `AuthController` (`@RestController` en `/api/auth`): `POST /register` (201 Created) y `POST /login` (200 OK).
  - Manejo global de excepciones: `GlobalExceptionHandler` (`@RestControllerAdvice`) con formato JSON uniforme (timestamp, status, error, message), `ApiError` y `EmailAlreadyExistsException`.
  - Login con credenciales inválidas devuelve 401 con mensaje genérico "Credenciales invalidas" (sin revelar si falló email o password).
  - Actualizada la sección "Arquitectura de Seguridad" con el punto de **Prevención DoS y Agotamiento de Recursos** (límite de archivos Max 5MB + rate limiting en Nginx).
  - Compilación verificada con éxito: `mvn clean compile`.
- **Pendiente para la próxima sesión (Fase 3):**
  - Desarrollar CRUD de Viajes (solo lectura/escritura para usuarios pertenecientes al viaje).
  - Implementar lógica de negocio: enviar invitación por email/id y aceptar invitación.
- **Notas técnicas:**
  - La clave JWT del `application.properties` es de desarrollo; debe sobrescribirse vía variables de entorno en producción.
  - El rol se mapea como `ROLE_USER` / `ROLE_ADMIN` (SimpleGrantedAuthority).
  - Endpoints `/api/auth/**` son públicos (`permitAll`); el resto de rutas requieren JWT.
- **Últimos commits:** `feat(backend): capa de seguridad Fase 2 - Spring Security, JWT y filtro de autenticacion`.

---

## 🔌 Registro de Endpoints de la API
*El agente documentará aquí cada nuevo endpoint funcional que se vaya integrando.*

| Método | Ruta | Descripción | Seguridad (JWT / Roles) |
| :--- | :--- | :--- | :--- |
| POST | `/api/auth/register` | Registro de nuevo usuario. Devuelve 201 Created con token JWT | Público |
| POST | `/api/auth/login` | Inicio de sesión. Devuelve 200 OK con token JWT | Público |