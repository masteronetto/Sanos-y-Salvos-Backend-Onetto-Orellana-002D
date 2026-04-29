# Backend-Sanos-y-Salvos

Backend de “Sanos y Salvos”, aplicación para la gestión de mascotas perdidas y encontradas. Este repositorio contiene únicamente la capa backend y la lógica de microservicios; el frontend vive en otro repositorio.

La base técnica queda orientada a Kotlin, Spring Boot y XANO como backend de datos, con un servicio BFF como punto de entrada principal para la app móvil.

## Enfoque

La base propuesta está pensada para Kotlin con una arquitectura de microservicios. El servicio más importante de entrada para el frontend será el BFF, que centraliza y adapta respuestas de los servicios internos.

## Migracion a XANO

Se completó la migración de persistencia desde PostgreSQL hacia XANO.

### Cambios principales realizados

- Se removieron dependencias de JPA/Hibernate y driver PostgreSQL en `user-service`, `pet-service`, `geoservice` y `match-service`.
- Se eliminó la configuración `spring.datasource` y `spring.jpa` de los microservicios de dominio.
- Se agregó configuración por servicio para `xano.base-url` y `xano.api-key`.
- Se retiró PostgreSQL de `docker-compose.yml`; se mantiene RabbitMQ como infraestructura local.
- Se incorporaron clientes HTTP a XANO en los servicios de dominio para operaciones de usuarios, mascotas, reportes, coincidencias y colaboradores.
- `geoservice` consume reportes cercanos desde XANO para el mapa y conserva fallback local si la API externa no responde.

### Base URL XANO

- `https://petfind.xano.io/api:v1`

### Endpoints XANO integrados en backend

- `GET /sanos-y-salvos-reports/search`
- `GET /sanos-y-salvos-pets/list_by_owner/{ownerId}`
- `GET /sanos-y-salvos-matches/pending`
- `GET /sanos-y-salvos-collaborators/list_by_type`
- `POST /sanos-y-salvos-webhooks/match-notification`
- `POST /sanos-y-salvos-auth/register`
- `POST /sanos-y-salvos-auth/login`

### Variables de entorno

Definir en cada microservicio de dominio:

- `XANO_BASE_URL` (default: `https://petfind.xano.io/api:v1`)
- `XANO_API_KEY` (sin default; recomendado por entorno)

En BFF:

- `GEOSERVICE_BASE_URL` (default: `http://localhost:8083`)

### Nota de build local

En este entorno, para compilar sin errores de versión JVM/Kotlin se utilizó Java 21:

- `JAVA_HOME=/usr/local/sdkman/candidates/java/21.0.10-ms PATH=/usr/local/sdkman/candidates/java/21.0.10-ms/bin:$PATH gradle --no-daemon :services:user-service:compileKotlin :services:pet-service:compileKotlin :services:geoservice:compileKotlin :services:match-service:compileKotlin`

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
- `docker-compose.yml`: servicios de infraestructura local (RabbitMQ).

## Mapeo funcional

- `user-service`: registro, autenticación, perfiles, validación y administración básica de usuarios.
- `pet-service`: registro de mascotas, reportes perdidos/encontrados, historial y colaboración con entidades.
- `geoservice`: geolocalización, reportes cercanos y soporte para mapa interactivo.
- `match-service`: motor de coincidencias y disparo de notificaciones de coincidencia.
- `bff-service`: agregación y adaptación de datos para el frontend.

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
