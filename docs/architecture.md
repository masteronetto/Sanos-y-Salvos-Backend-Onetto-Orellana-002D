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

## Servicios propuestos
### BFF Service
Punto de entrada para el frontend. Agrega respuestas de varios microservicios, adapta payloads y reduce la complejidad del cliente.

### User Service
Gestión de autenticación, sesiones, permisos, perfiles y administración básica de usuarios.

### Report Service
Registro de mascotas perdidas y encontradas, historial, fotografías y metadatos de los reportes.

### GeoService
Geolocalización, ubicación de reportes en el mapa y consultas espaciales.

### Match Service
Motor de coincidencias automáticas entre reportes de mascotas perdidas y encontradas.

### Notification Service
Envío de notificaciones push, correos o alertas internas.

### Collaboration Service
Participación de clínicas veterinarias, refugios y municipalidades como entidades colaboradoras.

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

## Flujo de alto nivel
1. El frontend llama al BFF.
2. El BFF consulta servicios internos según el caso de uso.
3. Los servicios consumen APIs externas gratuitas cuando necesitan datos de mapas o rutas.
4. El BFF compone la respuesta final para el cliente.

## Estructura base del repositorio
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

## Siguientes pasos sugeridos
- Crear el Gradle multi-módulo.
- Definir los contratos entre servicios.
- Elegir la estrategia de persistencia por servicio.
- Integrar un proveedor de mapas gratuito como primera capa funcional.
- Definir la autenticación centralizada para el BFF y los servicios internos.
