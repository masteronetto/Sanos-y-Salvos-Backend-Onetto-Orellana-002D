# Backend-Sanos-y-Salvos

Backend de “Sanos y Salvos”, aplicación para la gestión de mascotas perdidas y encontradas. Este repositorio contiene únicamente la capa backend y la lógica de microservicios; el frontend vive en otro repositorio.

La base técnica queda orientada a Kotlin, Spring Boot y PostgreSQL, con un servicio BFF como punto de entrada principal para la app móvil.

## Enfoque

La base propuesta está pensada para Kotlin con una arquitectura de microservicios. El servicio más importante de entrada para el frontend será el BFF, que centraliza y adapta respuestas de los servicios internos.

## Estructura base propuesta

- apps/bff-service
- services/user-service
- services/report-service
- services/geoservice
- services/match-service
- services/notification-service
- services/collaboration-service
- shared/common
- shared/contracts
- docs

## Mapeo funcional

- `user-service`: registro, autenticación, perfiles, validación y administración básica de usuarios.
- `report-service`: reportes de mascotas perdidas y encontradas, evidencias e historial.
- `geoservice`: geolocalización, reportes cercanos y soporte para mapa interactivo.
- `match-service`: motor de coincidencias automáticas entre reportes.
- `notification-service`: alertas y notificaciones automáticas.
- `collaboration-service`: clínicas, refugios y municipalidades como entidades colaboradoras.
- `bff-service`: agregación y adaptación de datos para el frontend.

## APIs gratuitas recomendadas

- OpenStreetMap para la base cartográfica.
- Nominatim para geocodificación y reverse geocodificación.
- OSRM para cálculo de rutas.
- Overpass API para consultas geoespaciales y puntos de interés.

## Documento de arquitectura

Ver [docs/architecture.md](docs/architecture.md) para la propuesta completa de servicios, responsabilidades y flujo entre componentes.

Ver [docs/module-map.md](docs/module-map.md) para la relación entre requisitos, historias de usuario y módulos.
