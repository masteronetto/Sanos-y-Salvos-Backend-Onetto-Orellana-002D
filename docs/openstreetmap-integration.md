# Integracion base OpenStreetMap

Esta fase deja operativo el backend para mapa base, capas y soporte visual de reportes cercanos y navegacion.

## Servicios involucrados
- `geoservice` (fuente de configuracion y datos de mapa, puerto 8083)
- `bff-service` (fachada para el frontend, puerto 8080)

## Endpoints en GeoService
Base: `http://localhost:8083/api/v1/geo/map`

- `GET /provider`
  - Retorna configuracion del proveedor de tiles OSM.

- `GET /layers`
  - Retorna capas habilitadas por defecto.

- `GET /reports/nearby?latitude={lat}&longitude={lon}&radiusMeters={m}&reportType={LOST|FOUND}`
  - Retorna marcadores cercanos para pintar sobre el mapa.

- `GET /navigation/link?fromLatitude={lat}&fromLongitude={lon}&toLatitude={lat}&toLongitude={lon}`
  - Retorna enlace de navegacion visual de OpenStreetMap.

## Endpoints en BFF
Base: `http://localhost:8080/api/v1/bff/map`

- `GET /provider`
- `GET /layers`
- `GET /reports/nearby`
- `GET /navigation/link`

El frontend debe consumir estos endpoints del BFF en lugar de llamar directamente a geoservice.

## Alcance implementado en esta fase
- Base cartografica para mostrar el mapa.
- Capas de calles, caminos, reportes cercanos y previsualizacion de navegacion.
- Soporte visual de reportes cercanos (actualmente con datos semilla).

## Variables de entorno
### GeoService
- `OSM_TILE_URL_TEMPLATE` (default: `https://tile.openstreetmap.org/{z}/{x}/{y}.png`)
- `OSM_ATTRIBUTION` (default: `© OpenStreetMap contributors`)
- `OSM_TERMS_URL` (default: `https://www.openstreetmap.org/copyright`)

### BFF
- `GEOSERVICE_BASE_URL` (default: `http://localhost:8083`)

## Notas de implementacion
- La capa de reportes cercanos en esta fase usa datos semilla para habilitar la visualizacion y el flujo de frontend.
- En la siguiente fase, `reports/nearby` debe seguir leyendo reportes reales desde XANO a través de `pet-service` y `geoservice`.
- Esta fase no incorpora aun geocodificacion, rutas calculadas, motor de coincidencias, notificaciones ni gestion de fotos.
