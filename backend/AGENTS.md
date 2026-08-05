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

### Fase 1: Infraestructura Base y Base de Datos (Pendiente)
- [x] Crear `docker-compose.yml` para levantar PostgreSQL + PostGIS (incluyendo volumen para persistencia).
- [ ] Inicializar proyecto Spring Boot con las dependencias base (Web, JPA, PostgreSQL, Validation, Lombok).
- [ ] Configurar `application.properties` (o `.yml`) con credenciales de base de datos y propiedades de Hibernate (update/validate).

### Fase 2: Entidades Core y Seguridad (Pendiente)
- [ ] Generar Entidad `Usuario` (id, email, password, nombre, rol).
- [ ] Generar Entidad `Viaje` y la tabla intermedia de invitaciones/participantes.
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
- **Hito actual:** Fase 1 en progreso (infraestructura de base de datos lista).
- **Completado en esta sesión:**
  - Infraestructura Docker: `infra/docker-compose.yml` con PostgreSQL 16 + PostGIS (`postgis/postgis:16-3.4`).
  - Puertos: mapeo `5432:5432` (configurable vía `${POSTGRES_PORT}`).
  - Variables de entorno seguras para usuario, contraseña y base de datos (valores por defecto de desarrollo sobrescribibles con `.env`).
  - Volumen de persistencia mapeado a la carpeta local `infra/postgres_data`.
  - Script de inicialización `infra/postgis-init/init.sql` que habilita la extensión PostGIS (`CREATE EXTENSION postgis`).
  - Healthcheck de disponibilidad configurado.
- **Pendiente para la próxima sesión:**
  - Inicializar proyecto Spring Boot (Web, JPA, PostgreSQL, Validation, Lombok).
  - Configurar `application.properties` con credenciales y propiedades de Hibernate (update/validate).
- **Últimos commits:** `docs(core): inicializar repositorio y documentacion de agentes de desarrollo`.

---

## 🔌 Registro de Endpoints de la API
*El agente documentará aquí cada nuevo endpoint funcional que se vaya integrando.*

| Método | Ruta | Descripción | Seguridad (JWT / Roles) |
| :--- | :--- | :--- | :--- |
| TBD | TBD | TBD | TBD |