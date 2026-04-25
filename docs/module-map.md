# Mapa de módulos y requisitos

## BFF
- Punto de entrada para la app móvil.
- Orquesta consultas entre usuarios, mascotas, reportes, geo y coincidencias.
- Expone respuestas agregadas al frontend.

## User Service
- RF01, RF02, RF04, RF11.
- HU01, HU02, HU04.
- Usuarios, autenticación, roles, validación básica y supervisión administrativa.

## Report Service
- RF03, RF05, RF06, RF12.
- HU06, HU07, HU08, HU09.
- Reportes de mascotas perdidas y encontradas, historial y evidencia fotográfica.

## Geo Service
- RF07, parte de RF05 y RF06.
- HU10, HU11, HU12.
- Geolocalización, reportes por zona, consultas espaciales y datos para mapa.

## Match Service
- RF08, RF09.
- Épica 4 completa.
- Motor de coincidencias automáticas y disparo de posibles alertas.

## Notification Service
- RF09.
- Alertas y notificaciones automáticas para coincidencias o eventos relevantes.

## Collaboration Service
- RF10.
- HU16, HU17, HU18, HU19.
- Clínicas veterinarias, refugios, municipalidades y validación operativa.

## shared/contracts
- DTOs, eventos y modelos compartidos.

## shared/common
- Excepciones, respuestas de error y utilidades comunes.
