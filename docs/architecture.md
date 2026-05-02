# Arquitectura base del backend

Este repositorio contiene únicamente el backend de Sanos y Salvos. El frontend vive en otro repositorio y se consume a través del servicio BFF.

## Objetivo
Construir un backend en Kotlin orientado a microservicios para una app tipo Google Maps o Waze, usando APIs gratuitas o de bajo costo cuando sea posible.

## Decisiones base
- Kotlin como lenguaje principal.
- Gradle multi-módulo como base de construcción.
- Arquitectura de microservicios con un BFF como punto de entrada para el frontend.
- Separación por dominio para facilitar escalabilidad y mantenimiento.
- Uso de proveedores abiertos o gratuitos para mapas, geocodificación y rutas.
- Persistencia y CRUD delegados a XANO para usuarios, mascotas, reportes, coincidencias y colaboradores.

## Servicios propuestos
### BFF Service
Punto de entrada para el frontend. Agrega respuestas de varios microservicios, adapta payloads y reduce la complejidad del cliente. Puerto 8080.

### User Service
Gestión de autenticación, sesiones, permisos, perfiles, roles y recuperación de acceso. Puerto 8081.

### Pet Service
Registro y administración de mascotas, reportes perdidos/encontrados, historial y recepción de incidentes de entidades colaboradoras. Puerto 8082.

### GeoService
Geolocalización, ubicación de reportes en el mapa y consultas espaciales. Puerto 8083.

### Match Service
Motor de coincidencias automáticas entre reportes de mascotas perdidas y encontradas, incluyendo disparo de notificaciones. Puerto 8084.

## Módulos compartidos
### shared/contracts
DTOs, eventos y contratos comunes entre servicios.

### shared/common
Utilidades transversales, manejo de errores, logging y helpers reutilizables.

## APIs gratuitas recomendadas
- OpenStreetMap para base cartográfica.
- Nominatim para geocodificación y reverse geocodificación.
- OSRM para cálculo de rutas.
- Overpass API para consultas sobre puntos de interés y datos geoespaciales.

## Estado actual de implementación
- Integración base de OpenStreetMap disponible.
- `geoservice` publica configuración de tiles, capas de mapa, reportes cercanos y enlace de navegación visual.
- `bff-service` expone esos endpoints como fachada para el frontend.
- Geocodificación, rutas calculadas, coincidencias avanzadas, notificaciones externas y gestión de fotos quedan para fases siguientes.
- XANO aporta la capa de datos para users, pets, reports, matches y collaborators.

## Flujo de alto nivel
1. El frontend llama al BFF.
2. El BFF consulta servicios internos según el caso de uso.
3. Los servicios consumen APIs externas gratuitas cuando necesitan datos de mapas o rutas.
4. El BFF compone la respuesta final para el cliente.

## Estructura base del repositorio
- apps/bff-service
- services/user-service
- services/pet-service
- services/geoservice
- services/match-service
- shared/common
- shared/contracts
- docs

## Siguientes pasos sugeridos
- Crear el Gradle multi-módulo.
- Definir los contratos entre servicios.
- Integrar un proveedor de mapas gratuito como primera capa funcional.
- Ajustar los clientes XANO y las credenciales de despliegue por entorno.
