# Backend - Sanos y Salvos

Backend de "Sanos y Salvos", aplicación para la gestión de mascotas perdidas y encontradas. Este repositorio contiene la capa backend, lógica de microservicios **y el código fuente del frontend Android** en un monorepo integrado.

**Stack técnico:**
- **Backend:** Kotlin, Spring Boot 3.3.5, PostgreSQL, RabbitMQ
- **Frontend:** Android (Jetpack Compose), Retrofit, OpenStreetMap
- **Mapas:** OpenStreetMap (gratuito, sin costos de licencia)
- **Arquitectura:** Microservicios con BFF como punto de entrada único

**Características clave:**
- ✅ Autenticación JWT centralizada
- ✅ CRUD de usuarios, mascotas, reportes, coincidencias, colaboradores
- ✅ Motor de coincidencias automático entre reportes perdidos/encontrados
- ✅ Integración con mapas OSM a través del BFF
- ✅ Webhooks para notificaciones de coincidencias
- ✅ Preparado para Android con DTOs compartidos Kotlin

## Enfoque

Monorepo Kotlin con arquitectura de microservicios orientada a:
1. **Backend escalable:** Cada dominio (usuarios, mascotas, reportes, coincidencias, colaboradores) en su propio microservicio.
2. **BFF único:** Punto de entrada centralizado para el frontend, simplifica consumo de APIs.
3. **Frontend integrado:** Android con Jetpack Compose, DTOs sincronizados del backend.
4. **Bajo costo:** OpenStreetMap en lugar de Google Maps o Mapbox.
5. **Fácil despliegue:** Docker Compose para levantar infra local, Gradle multi-módulo.

## Servicios activos del proyecto

- API Gateway / BFF: `apps/bff-service` (puerto 8080)
- Microservicio Usuario: `services/user-service` (puerto 8081)
- Microservicio Mascota: `services/pet-service` (puerto 8082)
- Microservicio GeoService: `services/geoservice` (puerto 8083)
- Microservicio Motor de Coincidencias: `services/match-service` (puerto 8084)

## Inicio rápido

### Requisitos
- Java 21+
- Docker y Docker Compose
- Gradle 8.0+ (opcional, el wrapper está incluido)

### Levantar infraestructura local

```bash
# Inicia PostgreSQL y RabbitMQ
docker-compose up -d
```

### Ejecutar el backend completo

**Opción 1: Ejecutar solo el BFF (recomendado para desarrollo)**
```bash
./gradlew :apps:bff-service:bootRun
```

**Opción 2: Ejecutar todos los microservicios**
```bash
# Terminal 1: User Service
./gradlew :services:user-service:bootRun

# Terminal 2: Pet Service
./gradlew :services:pet-service:bootRun

# Terminal 3: GeoService
./gradlew :services:geoservice:bootRun

# Terminal 4: Match Service
./gradlew :services:match-service:bootRun

# Terminal 5: BFF
./gradlew :apps:bff-service:bootRun
```

### Validar la estructura (sin ejecutar)

```bash
# Compilar todos los módulos
./gradlew clean build -q

# Mostrar errors (si los hay)
./gradlew clean build
```

### Endpoints disponibles

Una vez levantado el BFF, puedes consultar:

- **Auth**
  ```
  POST /api/v1/bff/auth/login
  POST /api/v1/bff/auth/register
  GET  /api/v1/bff/auth/me
  ```

- **Mapas (OSM)**
  ```
  GET  /api/v1/bff/map/provider
  GET  /api/v1/bff/map/layers
  GET  /api/v1/bff/map/reports/nearby?latitude=40.4&longitude=-3.7&radiusMeters=3000
  GET  /api/v1/bff/map/navigation/link?fromLatitude=...&fromLongitude=...&toLatitude=...&toLongitude=...
  ```

- **Reportes, Mascotas, Coincidencias**
  ```
  POST /api/v1/bff/pets/...
  POST /api/v1/bff/reports/...
  GET  /api/v1/bff/matches/...
  ```

Consulta [docs/xano-openapi.json](docs/xano-openapi.json) para el OpenAPI completo.

## Estructura base propuesta

```
/
├── apps/
│   └── bff-service/              # API Gateway (punto entrada único)
│       └── src/main/kotlin/com/sanosysalvos/bff/
│           ├── controller/       # Endpoints públicos (BFF)
│           ├── client/            # Clientes HTTP a microservicios internos
│           └── config/            # Configuración y propiedades
├── services/
│   ├── user-service/             # Autenticación, usuarios, roles
│   ├── pet-service/              # Mascotas, reportes
│   ├── geoservice/               # Mapas OSM, geolocalización
│   └── match-service/            # Motor de coincidencias
├── shared/
│   ├── contracts/                # DTOs compartidos (usado por backend y Android)
│   └── common/                   # Excepciones y utilidades comunes
├── docs/
│   ├── architecture.md           # Decisiones de arquitectura
│   ├── xano-openapi.json         # Especificación OpenAPI exportada de Xano
│   └── module-map.md             # Mapeo de módulos
└── docker-compose.yml            # PostgreSQL, RabbitMQ
```

**Nota:** El código Android será integrado en `apps/mobile-android/` una vez se confirme la configuración del backend.

## Explicación de carpetas

### Backend
- `apps/bff-service/`: API Gateway / Backend for Frontend
  - Recibe TODAS las peticiones del cliente (Android).
  - Orquesta llamadas a microservicios internos.
  - Adapta y compone respuestas.
  - **Puerto:** 8080

- `services/`: Microservicios especializados por dominio
  - `user-service`: Usuarios, autenticación, roles, permisos (puerto 8081)
  - `pet-service`: Mascotas, reportes perdidos/encontrados (puerto 8082)
  - `geoservice`: Mapas OSM, geolocalización, reportes cercanos (puerto 8083)
  - `match-service`: Motor de coincidencias, notificaciones (puerto 8084)

- `shared/`: Código reutilizable
  - `contracts/`: DTOs, modelos, enums compartidos entre servicios y Android
  - `common/`: Excepciones, utilidades, logging

### Frontend (futuro)
- `apps/mobile-android/`: Aplicación Android
  - Jetpack Compose para UI
  - Retrofit para HTTP + Interceptores
  - Consumir DTOs desde `shared/contracts`
  - OpenStreetMap integrado

### Documentación
- `docs/architecture.md`: Decisiones de diseño y rationale
- `docs/module-map.md`: Mapeo visual de módulos e integraciones
- `docs/xano-openapi.json`: Especificación completa de APIs (convertida de Xano)

## Mapeo funcional

### Flujo de petición Frontend → Backend

```
Android App (8080 via BFF)
    ↓
BFF Service (8080)
    ├→ User Service (8081) [auth, users]
    ├→ Pet Service (8082) [pets, reports]
    ├→ GeoService (8083) [maps, nearby reports]
    └→ Match Service (8084) [matches, notifications]
    ↓
PostgreSQL (5432) [datos persistentes]
RabbitMQ (5672) [mensajería async]
```

### Endpoints por servicio

**User Service:**
- `POST /api/v1/users/register`
- `POST /api/v1/users/login`
- `GET /api/v1/users/me`
- `GET /api/v1/users/{id}`
- etc.

**Pet Service:**
- `POST /api/v1/pets`
- `GET /api/v1/pets/list_by_owner/{ownerId}`
- `POST /api/v1/pets/{petId}/reports/lost`
- etc.

**GeoService:**
- `GET /api/v1/geo/map/provider`
- `GET /api/v1/geo/map/layers`
- `GET /api/v1/geo/map/reports/nearby`

**Match Service:**
- `POST /api/v1/matches/evaluate`
- `GET /api/v1/matches/pending`
- etc.

**BFF (punto público):**
Reexpose todos los anteriores bajo `/api/v1/bff/*` más lógica de agregación.

## Testing y Validación

### Compilar sin errores (sin ejecutar)

```bash
# Compilar todos los módulos
./gradlew clean build -q

# Si hay errores, verás el output
./gradlew clean build
```

### Tras subir la rama (proximos pasos)

1. Verificar que la compilación es limpia ✓
2. Adaptación del backend para reflejar endpoints Xano en el BFF
3. Pruebas de integración (Docker Compose + bootRun)
4. Preparación del cliente Android (DTOs + Retrofit)

## Variables de entorno

```bash
# Defaults en application.yml, pero puedes sobrescribir:
export DB_URL=jdbc:postgresql://localhost:5432/sanosysalvos
export DB_USERNAME=sanos
export DB_PASSWORD=sanos123
export GEOSERVICE_BASE_URL=http://localhost:8083
```

## Documentación

- [Arquitectura detallada](docs/architecture.md)
- [Mapeo de módulos](docs/module-map.md)
- [Integración OSM](docs/openstreetmap-integration.md)
- [OpenAPI Xano](docs/xano-openapi.json)
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
