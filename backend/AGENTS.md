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

### Fase 5: Módulos Secundarios y Archivos Seguros (Pendiente)
- [ ] Entidades y CRUD para `Gastos` (Presupuesto) y `Items` (Checklist).
- [ ] Entidad y CRUD para `Diccionario` (Vocabulario de supervivencia).
- [ ] **Módulo de Archivos Seguros:** Implementar servicio de subida/bajada de archivos (Local o S3) con validación estricta de pertenencia al viaje.

---

## 📊 Estado Actual del Proyecto
*Esta sección será actualizada automáticamente por el agente OpenCode al finalizar cada sesión.*

- **Última actualización:** 05/08/2026
- **Hito actual:** Fase 4 completada. Preparando Fase 5 (Módulos Secundarios y Archivos Seguros).
- **Completado en esta sesión (Fase 4: Logística, Calendario y Mapa):**
  - Enum `TipoActividad` (`VUELO`, `TREN`, `HOTEL`, `EXCURSION`, `RESTAURANTE`, `OTRO`) en `com.app.viajes.model.enums`.
  - Entidad `Actividad` (UUID id, @ManyToOne a Viaje, titulo, descripcion TEXT, tipoActividad, fechaHoraInicio/Fin LocalDateTime, latitud/longitud Double, urlDocumento): `com.app.viajes.model.Actividad`.
  - DTOs: `ActividadRequestDTO` (con validaciones) y `ActividadResponseDTO` (campos + id y viajeId).
  - `ActividadRepository`: `findByViajeIdOrderByFechaHoraInicioAsc` y `findByViajeIdAndFecha` (por día de `fechaHoraInicio`).
  - `ActividadService` (interfaz) + `ActividadServiceImpl` (`@Service`): verificación de pertenencia al viaje (`existsByUsuarioIdAndViajeId`) en cada método antes de operar.
  - `ActividadController` (`@RestController` en `/api/viajes/{viajeId}/actividades`): `POST /`, `GET /`, `GET /timeline?fecha=YYYY-MM-DD`, `DELETE /{actividadId}`.
  - Coordenadas guardadas como Double (latitud/longitud), preparadas para futura migración a columnas PostGIS.
  - Compilación verificada con éxito: `mvn clean compile`.
- **Pendiente para la próxima sesión (Fase 5):**
  - Entidades y CRUD para `Gastos` (Presupuesto) y `Items` (Checklist).
  - Entidad y CRUD para `Diccionario` (Vocabulario de supervivencia).
  - Módulo de Archivos Seguros: subida/bajada de archivos (Local o S3) con validación estricta de pertenencia al viaje y límite de tamaño Max 5MB.
- **Notas técnicas:**
  - La clave JWT del `application.properties` es de desarrollo; debe sobrescribirse vía variables de entorno en producción.
  - El rol se mapea como `ROLE_USER` / `ROLE_ADMIN` (SimpleGrantedAuthority).
  - Endpoints `/api/auth/**` son públicos (`permitAll`); el resto de rutas requieren JWT.
  - La autorización de viajes se basa en la tabla `usuarios_viajes` (pertenencia/propiedad de recursos).
  - Pendiente: previsión de Rate Limiting en Nginx (DoS) y límite de archivos 5MB (se aplicará en Fase 5).
- **Últimos commits:** `feat(backend): CRUD de viajes e invitaciones Fase 3 con control de acceso por pertenencia`.

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
| PUT | `/api/viajes/{id}/responder-invitacion` | Aceptar/Rechazar invitación pendiente | JWT |
| POST | `/api/viajes/{viajeId}/actividades` | Crear una actividad en el viaje | JWT |
| GET | `/api/viajes/{viajeId}/actividades` | Listar actividades del viaje ordenadas por fecha | JWT |
| GET | `/api/viajes/{viajeId}/actividades/timeline?fecha=YYYY-MM-DD` | Actividades de un día concreto (minicalendario) | JWT |
| DELETE | `/api/viajes/{viajeId}/actividades/{actividadId}` | Eliminar una actividad del viaje | JWT |