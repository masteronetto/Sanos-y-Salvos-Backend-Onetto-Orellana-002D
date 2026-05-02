# Mapa de módulos y requisitos

## BFF
- Punto de entrada para la app móvil.
- Orquesta consultas entre usuarios, mascotas, reportes, geo y coincidencias.
- Expone respuestas agregadas al frontend.
- Actualmente expone la fachada de mapa OSM en `/api/v1/bff/map/*`.

## User Service
- RF01, RF02, RF04, RF11.
- HU01, HU02, HU04, HU19.
- Usuarios, autenticación, roles, tokens de dispositivo y supervisión administrativa.
- Puerto actual: 8081.

## Pet Service
- RF03, RF05, RF06, RF10, RF12.
- HU03, HU05, HU06, HU07, HU08, HU09, HU16, HU17, HU18.
- Registro de mascotas, asociación dueño-mascota, reportes perdidos/encontrados, historial y colaboración de entidades.
- Puerto actual: 8082.

## Geo Service
- RF07, parte de RF05 y RF06.
- HU10, HU11, HU12.
- Geolocalización, reportes por zona, consultas espaciales y datos para mapa.
- Puerto actual: 8083.
- Estado OSM: provider, layers, nearby reports y navigation link implementados.

## Match Service
- RF08, RF09.
- Épica 4 completa.
- Motor de coincidencias automáticas y disparo de notificaciones por coincidencia.
- Puerto actual: 8084.

## shared/contracts
- DTOs, eventos y modelos compartidos.

## shared/common
- Excepciones, respuestas de error y utilidades comunes.
