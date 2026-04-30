# Backend - Sanos y Salvos

Backend de Sanos y Salvos para gestion de mascotas perdidas y encontradas.

Estado actual: el backend esta migrado a integraciones HTTP con Xano en los servicios de dominio, con compilacion y tests del workspace en estado exitoso.

## Stack tecnico

- Kotlin + Spring Boot 3
- Java 21
- Gradle multi-modulo
- OpenStreetMap para capacidades de mapa
- Xano como backend de datos y autenticacion via clientes HTTP

## Servicios activos

- BFF: apps/bff-service (puerto 8080)
- User Service: services/user-service (puerto 8081)
- Pet Service: services/pet-service (puerto 8082)
- Geo Service: services/geoservice (puerto 8083)
- Match Service: services/match-service (puerto 8084)

## Cambios relevantes de la migracion

- Migracion de servicios de dominio a clientes Xano (sin dependencia operativa de JPA en pet-service y match-service, y flujo simplificado en user-service)
- IDs secuenciales con prefijo en util comun:
  - Usuario: U001, U002, ...
  - Entidad colaboradora: E001, E002, ...
  - Mascota: M001, M002, ...
  - Reporte: R001, R002, ...
- Testes de integracion ajustados a rutas de health reales de cada servicio

## Endpoints de health

- GET /api/v1/users/health
- GET /api/v1/pets/health
- GET /api/v1/geo/health
- GET /api/v1/matches/health

## Endpoints principales actuales

User Service:
- POST /api/v1/users/register
- POST /api/v1/users/login

Pet Service:
- POST /api/v1/pets/create
- GET /api/v1/pets/list
- GET /api/v1/pets/list_by_owner/{ownerId}
- POST /api/v1/pets/{petId}/reports/lost
- POST /api/v1/pets/{petId}/reports/found

Match Service:
- POST /api/v1/matches/evaluate
- GET /api/v1/matches/pending
- GET /api/v1/matches/list
- PUT /api/v1/matches/accept/{id}
- PUT /api/v1/matches/reject/{id}

Geo Service:
- GET /api/v1/geo/map/provider
- GET /api/v1/geo/map/layers
- GET /api/v1/geo/map/reports/nearby
- GET /api/v1/geo/map/navigation/link

## Inicio rapido

Requisitos:
- Java 21+
- Docker y Docker Compose (solo si necesitas infraestructura local adicional)

Build completo:

./gradlew clean build

Ejecutar servicios individualmente:

./gradlew :services:user-service:bootRun
./gradlew :services:pet-service:bootRun
./gradlew :services:geoservice:bootRun
./gradlew :services:match-service:bootRun
./gradlew :apps:bff-service:bootRun

## Publicar cambios en nueva rama

Si ya tienes cambios listos y quieres publicarlos con un nuevo nombre de rama:

git checkout -b feature/xano-api-migration-final
git push -u origin feature/xano-api-migration-final

## Estructura del repositorio

- apps/
  - bff-service/
- services/
  - user-service/
  - pet-service/
  - geoservice/
  - match-service/
- shared/
  - contracts/
  - common/
- docs/
  - architecture.md
  - module-map.md
  - openstreetmap-integration.md
  - xano-openapi.json

## Documentacion

- docs/architecture.md
- docs/module-map.md
- docs/openstreetmap-integration.md
- docs/xano-openapi.json

## Notas

- Puede aparecer un warning de OpenJDK sobre class data sharing durante test/build. Es informativo y no bloquea el build.
- Para cambios de contratos API, mantener sincronia entre servicios y shared/contracts.
