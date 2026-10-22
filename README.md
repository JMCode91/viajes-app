# ✈️ Gestor de Viajes Colaborativo (Viajes.app)

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_3-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Angular](https://img.shields.io/badge/Angular_21-DD0031?style=for-the-badge&logo=angular&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-38B2AC?style=for-the-badge&logo=tailwind-css&logoColor=white)
![Cloudflare R2](https://img.shields.io/badge/Cloudflare_R2-F38020?style=for-the-badge&logo=cloudflare&logoColor=white)

Plataforma Full-Stack diseñada para la planificación y gestión de viajes grupales. Integra herramientas de logística, finanzas y multimedia en tiempo real bajo una arquitectura colaborativa.

El proyecto destaca por contar con un **Backend robusto y seguro** desarrollado nativamente en Java/Spring Boot, combinado con un **Frontend moderno y reactivo** construido mediante *Ingeniería Asistida por IA*, demostrando la capacidad de orquestar herramientas de Inteligencia Artificial para el desarrollo ágil de interfaces (Angular) mientras se mantiene el control arquitectónico absoluto en el lado del servidor.

---

## ✨ Funcionalidades Principales

- 🔐 **Arquitectura Multitenant & Roles:** Acceso seguro mediante JWT y verificación por correo. Sistema de invitaciones por código único (UUID) para unir participantes a un viaje con control estricto de roles (Propietario vs. Participante).
- 🗺️ **Itinerario Interactivo & Google Maps:** Planificación día a día con previsión meteorológica en tiempo real y geolocalización de planes (vuelos, hoteles, actividades) en un mapa interactivo con filtrado por días.
- 🤖 **Asistente de Viajes con IA:** Integración con modelos de IA Generativa para crear propuestas de itinerarios automatizados basados en el destino, fechas, ritmo de viaje y temática deseada.
- 💸 **Gestión Financiera & Utilidades:** Módulo de gastos compartidos para equilibrar cuentas entre los participantes, checklist de equipaje/tareas, traductor integrado y conversor de divisas en vivo apoyado en APIs financieras.
- 📸 **Galería en la Nube (Cloudflare R2):** Subida colaborativa de imágenes (hasta 5MB) optimizadas mediante proxy inverso, almacenamiento en contenedores S3-compatible y visualizador tipo *Lightbox* a pantalla completa.
- 🎨 **UX/UI Avanzada:** Interfaz reactiva con diseño *Glassmorphism* y motor de temas dinámicos gestionado globalmente (Modo Día, Modo Noche y Modo Imagen Fotográfica personalizable).

---

## 🛠️ Stack Tecnológico

### Backend (Desarrollo Nativo & Arquitectura)
* **Lenguaje:** Java 21
* **Framework:** Spring Boot 3
* **Seguridad:** Spring Security + JWT
* **Base de Datos:** PostgreSQL 15
* **Almacenamiento Cloud:** Cloudflare R2 (Object Storage S3-compatible)

### Frontend (Desarrollo Asistido por IA)
* **Framework:** Angular 21 (Standalone Components, Signals)
* **Estilos:** Tailwind CSS
* **Mapas:** Angular Google Maps (`@angular/google-maps`)

### Infraestructura & DevOps
* **Servidor:** Linux VPS (Hetzner)
* **Contenedores:** Docker (Base de datos aislada)
* **Proxy Inverso:** Nginx (Gestión de certificados SSL, límites de subida y redirección)
* **Construcción:** Maven & NPM (Empaquetado unificado de artefactos estáticos en `.jar`)

---

## 🚀 Instalación y Despliegue en Local

### Prerrequisitos
- Java 21 (JDK)
- Node.js y Angular CLI 21+
- Docker
- Claves de API (Google Maps, Proveedor de IA, Cloudflare R2)

### 1. Clonar el repositorio
~~~bash
git clone https://github.com/JMCode91/viajes-app.git
cd viajes-app
~~~

### 2. Levantar la Base de Datos (PostgreSQL)
~~~bash
docker run --name viajes-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=tu_password -e POSTGRES_DB=viajes -p 5432:5432 -d postgres:15
~~~

### 3. Configurar y Ejecutar el Backend
Configura tus variables de entorno o edita `backend/src/main/resources/application.properties`:

~~~properties
spring.datasource.url=jdbc:postgresql://localhost:5432/viajes
spring.datasource.username=postgres
spring.datasource.password=tu_password

jwt.secret=tu_clave_secreta_jwt

# Configuración Cloudflare R2 / S3
aws.accessKeyId=tu_access_key
aws.secretKey=tu_secret_key
aws.s3.endpoint=https://<account_id>.r2.cloudflarestorage.com
aws.s3.bucket=nombre_de_tu_bucket

# URL del cliente para enlaces de verificación
app.frontend.url=http://localhost:4200
~~~

Arranca el servidor Spring Boot:
~~~bash
cd backend
./mvnw spring-boot:run
~~~

### 4. Configurar y Ejecutar el Frontend
~~~bash
cd ../frontend
npm install
ng serve
~~~
La aplicación estará disponible en `http://localhost:4200`.

---

## 📦 Empaquetado para Producción

El proyecto permite servir el cliente de Angular directamente desde el artefacto de Spring Boot:

1. Compilar el proyecto Angular: `cd frontend && ng build`
2. Copiar los archivos generados en `dist/` dentro de `backend/src/main/resources/static/`
3. Generar el ejecutable con Maven: `cd ../backend && ./mvnw clean package`
4. Desplegar y ejecutar en el servidor Linux:
~~~bash
nohup java -jar viajes-app.jar > registro.log 2>&1 &
~~~

---

## 👨‍💻 Autor

**Javier Moreno Ferrero**  
*Desarrollador Backend / Full-Stack*  
- 🌐 Portfolio: [javier-moreno.dev](https://javier-moreno.dev)
- 💼 GitHub: [@JMCode91](https://github.com/JMCode91)