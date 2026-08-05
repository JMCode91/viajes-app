# 🌍 Proyecto: Travel Planner "All-in-One" - Documentación Core (Backend)

## 🎯 Visión General
Desarrollo de una aplicación web de gestión integral de viajes enfocada estrictamente en un diseño Mobile-First. El sistema permite la creación de viajes, sistema de invitaciones colaborativas, gestión logística mediante un calendario interactivo geolocalizado, control de presupuesto, listas de tareas y almacenamiento seguro de documentos (billetes, reservas) y galerías multimedia.
- **Objetivo MVP:** Despliegue en septiembre para pruebas en entorno real.

## 🔒 Arquitectura de Seguridad (Critical)
- **Autenticación:** JSON Web Tokens (JWT) sin estado.
- **Autorización:** Control de acceso basado en roles (RBAC) y propiedad de recursos. Un usuario solo puede acceder a la información y archivos de un viaje si su `Usuario_ID` está vinculado a dicho `Viaje_ID`.
- **Archivos Sensibles:** Los documentos no se sirven estáticamente. Se descargan a través de un endpoint protegido que devuelve el flujo de bytes previa validación del JWT y pertenencia al viaje.

---

## 🗺️ Roadmap Backend Inicial (Plan de Acción)

### Fase 1: Infraestructura Base y Base de Datos (Completada)
- [x] Crear `docker-compose.yml` para levantar PostgreSQL + PostGIS (incluyendo volumen para persistencia).
- [x] Inicializar proyecto Spring Boot con las dependencias base (Web, JPA, PostgreSQL, Validation, Lombok).
- [x] Configurar `application.properties` (o `.yml`) con credenciales de base de datos y propiedades de Hibernate (update/validate).

### Fase 2: Entidades Core y Seguridad (En Progreso)
- [x] Generar Entidad `Usuario` (id, email, password, nombre, rol).
- [x] Generar Entidad `Viaje` y la tabla intermedia de invitaciones/participantes.
- [ ] Implementar Spring Security: Configuración de SecurityFilterChain, PasswordEncoder (BCrypt).
- [ ] Implementar generación y validación de JWT (JwtUtil, JwtFilter).
- [ ] Desarrollar endpoints públicos: `/api/auth/register` y `/api/auth/login`.

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
- **Hito actual:** Fase 2 en progreso (modelado de datos completo; falta la capa de seguridad JWT).
- **Completado en esta sesión (Fase 2 - Parte 1: Modelado de Datos):**
  - Enum `Rol` (`USER`, `ADMIN`) en `com.app.viajes.model.enums`.
  - Enum `EstadoInvitacion` (`PENDIENTE`, `ACEPTADA`, `RECHAZADA`) en `com.app.viajes.model.enums`.
  - Entidad `Usuario` (UUID id, email único, password, nombre, rol): `com.app.viajes.model.Usuario`.
  - Entidad `Viaje` (UUID id, titulo, fechaInicio, fechaFin, presupuestoBase): `com.app.viajes.model.Viaje`.
  - Entidad intermedia `UsuarioViaje` (UUID id, @ManyToOne a Usuario y Viaje, estadoInvitacion por defecto PENDIENTE, rolEnViaje): `com.app.viajes.model.UsuarioViaje`.
  - Repositorios JPA: `UsuarioRepository`, `ViajeRepository`, `UsuarioViajeRepository`.
  - `UsuarioViajeRepository` incluye búsquedas clave para la seguridad por pertenencia a un viaje (`existsByUsuarioIdAndViajeId`, `findByUsuarioIdAndViajeId`).
  - Entidades usan Lombok `@Getter`/`@Setter` (no `@Data`) e IDs UUID para evitar recursividad en relaciones y enumeración de IDs.
  - Compilación verificada con éxito: `mvn clean compile`.
- **Pendiente para la próxima sesión (Fase 2 - Parte 2: Seguridad):**
  - Implementar Spring Security: SecurityFilterChain, PasswordEncoder (BCrypt).
  - Implementar generación y validación de JWT (JwtUtil, JwtFilter).
  - Desarrollar endpoints públicos: `/api/auth/register` y `/api/auth/login`.
- **Enviar a la base de datos por primera vez:** al levantar `docker-compose` y la app, Hibernate creará las tablas (ddl-auto=update).
- **Últimos commits:** `feat(backend): inicializar proyecto Spring Boot 3.x con Maven y configuracion de base de datos`.

---

## 🔌 Registro de Endpoints de la API
*El agente documentará aquí cada nuevo endpoint funcional que se vaya integrando.*

| Método | Ruta | Descripción | Seguridad (JWT / Roles) |
| :--- | :--- | :--- | :--- |
| TBD | TBD | TBD | TBD |