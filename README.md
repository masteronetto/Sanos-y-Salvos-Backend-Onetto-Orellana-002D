# Backend - Sanos y Salvos

Backend de “Sanos y Salvos”, aplicación para la gestión de mascotas perdidas y encontradas. Este repositorio contiene únicamente la capa backend y la lógica de microservicios; el frontend vive en otro repositorio.

La base técnica queda orientada a Kotlin, Spring Boot y Xano como base de datos, con un servicio BFF como punto de entrada principal para la app móvil.

## Enfoque

La base propuesta está pensada para Kotlin con una arquitectura de microservicios. El servicio más importante de entrada para el frontend será el BFF, que centraliza y adapta respuestas de los servicios internos.

## Servicios activos del proyecto

- API Gateway / BFF: `apps/bff-service` (puerto 8080)
- Microservicio Usuario: `services/user-service` (puerto 8081)
- Microservicio Mascota: `services/pet-service` (puerto 8082)
- Microservicio GeoService: `services/geoservice` (puerto 8083)
- Microservicio Motor de Coincidencias: `services/match-service` (puerto 8084)

## Estructura base propuesta

- apps/bff-service
- services/user-service
- services/pet-service
- services/geoservice
- services/match-service
- shared/common
- shared/contracts
- docs

## Explicacion de carpetas

- `apps/`: aplicaciones de entrada del sistema.
- `apps/bff-service/`: API Gateway/BFF que recibe las llamadas del frontend y orquesta los microservicios.
- `services/`: microservicios de dominio del backend.
- `services/user-service/`: registro, autenticación, roles y gestión de perfil de usuario.
- `services/pet-service/`: gestión de mascotas, reportes perdidos/encontrados e interacción con entidades colaboradoras.
- `services/geoservice/`: lógica geoespacial, integración de mapa OSM, capas y reportes cercanos.
- `services/match-service/`: evaluación de coincidencias entre reportes y disparo de notificaciones por coincidencia.
- `shared/`: código reutilizable entre módulos.
- `shared/contracts/`: DTOs, requests/responses, enums y contratos compartidos entre servicios.
- `shared/common/`: utilidades transversales como excepciones y modelos de error comunes.
- `docs/`: documentación técnica y funcional del proyecto.
- `build.gradle.kts`: configuración raíz de Gradle para el monorepo Kotlin.
- `settings.gradle.kts`: declaración de módulos incluidos en el build.
- `docker-compose.yml`: servicios de infraestructura local (Xano como base de datos y RabbitMQ).

## Mapeo funcional

- `user-service`: registro, autenticación, perfiles, validación y administración básica de usuarios.
- `pet-service`: registro de mascotas, reportes perdidos/encontrados, historial y colaboración con entidades.
- `geoservice`: geolocalización, reportes cercanos y soporte para mapa interactivo.
- `match-service`: motor de coincidencias y disparo de notificaciones de coincidencia.
- `bff-service`: agregación y adaptación de datos para el frontend.

## Pruebas en Postman - Endpoints de Autenticación

### POST /api/v1/users/register
Registrar un nuevo usuario en el sistema. El endpoint se conecta a la API de Xano para persistir los datos.

**URL:** `POST http://localhost:8081/api/v1/users/register`

**Body (JSON):**
```json
{
  "fullName": "Juan Pérez García",
  "email": "juan@example.com",
  "phone": "123456789",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User registered",
  "data": {
    "userId": "id-generado-por-xano",
    "role": "USER",
    "token": "jwt-token-xano"
  }
}
```

### POST /api/v1/users/login
Autenticar un usuario existente mediante email y contraseña.

**URL:** `POST http://localhost:8081/api/v1/users/login`

**Body (JSON):**
```json
{
  "email": "juan@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User authenticated",
  "data": {
    "userId": "id-del-usuario",
    "role": "USER",
    "token": "jwt-token-xano"
  }
}
```

### GET /api/v1/users/health
Verificar que el servicio de usuarios está activo y disponible.

**URL:** `GET http://localhost:8081/api/v1/users/health`

**Response (200 OK):**
```json
{
  "service": "user-service",
  "status": "up"
}
```

## Integración Xano

Los endpoints de autenticación (login, register, logout, me, refresh) están integrados directamente con la API de Xano.

**Base URL Xano:** `https://x8ki-letl-twmt.n7.xano.io/api:sanos-y-salvos-auth`

La configuración se puede personalizar mediante la variable de entorno:
```bash
export XANO_AUTH_BASE_URL=https://tu-url-xano-aqui
```

Xano proporciona:
- `POST /login` - Autenticación de usuario
- `POST /register` - Registro de nuevo usuario
- `POST /logout` - Cierre de sesión
- `GET /me` - Obtener datos del usuario autenticado
- `POST /refresh` - Renovar token JWT

Para consultar el Swagger de Xano: https://x8ki-letl-twmt.n7.xano.io/api:workspace:t1gH6k-I

## Estado actual de OSM

- Integración base implementada en `geoservice` y expuesta por `bff-service`.
- Se entrega configuración de proveedor de tiles, capas de mapa, reportes cercanos y enlace de navegación visual.
- Endpoints de consumo para frontend:
	- `GET /api/v1/bff/map/provider`
	- `GET /api/v1/bff/map/layers`
	- `GET /api/v1/bff/map/reports/nearby`
	- `GET /api/v1/bff/map/navigation/link`

## APIs gratuitas recomendadas

- OpenStreetMap para la base cartográfica.
- Nominatim para geocodificación y reverse geocodificación.
- OSRM para cálculo de rutas.
- Overpass API para consultas geoespaciales y puntos de interés.

## Documento de arquitectura

Ver [docs/architecture.md](docs/architecture.md) para la propuesta completa de servicios, responsabilidades y flujo entre componentes.

Ver [docs/module-map.md](docs/module-map.md) para la relación entre requisitos, historias de usuario y módulos.

Ver [docs/openstreetmap-integration.md](docs/openstreetmap-integration.md) para la integración base de mapa, capas y reportes cercanos con OpenStreetMap.
