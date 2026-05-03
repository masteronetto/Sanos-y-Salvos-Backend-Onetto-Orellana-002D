# Backend - Sanos y Salvos

Backend de “Sanos y Salvos”, aplicación para la gestión de mascotas perdidas y encontradas. Este repositorio contiene únicamente la capa backend y la lógica de microservicios; el frontend vive en otro repositorio.

La base técnica queda orientada a Kotlin, Spring Boot y Xano como base de datos, con un servicio BFF como punto de entrada principal para la app móvil.

## Índice

- [Enfoque](#enfoque)
- [Servicios activos del proyecto](#servicios-activos-del-proyecto)
- [Estructura base propuesta](#estructura-base-propuesta)
- [Explicacion de carpetas](#explicacion-de-carpetas)
- [Mapeo funcional](#mapeo-funcional)
- [Pruebas en Postman - Endpoints de Autenticación](#pruebas-en-postman---endpoints-de-autenticación)
- [Integración Xano](#integración-xano)
- [Estado actual de OSM](#estado-actual-de-osm)
- [APIs gratuitas recomendadas](#apis-gratuitas-recomendadas)
- [Documento de arquitectura](#documento-de-arquitectura)
- [Troubleshooting](#troubleshooting)
- [Comandos para ejecutar](#comandos-para-ejecutar)

## Enfoque

La base propuesta está pensada para Kotlin con una arquitectura de microservicios. El servicio más importante de entrada para el frontend será el BFF, que centraliza y adapta respuestas de los servicios internos.

## Servicios activos del proyecto

| Servicio | Ruta | Puerto |
| --- | --- | --- |
| API Gateway / BFF | `apps/bff-service` | 8080 |
| Microservicio Usuario | `services/user-service` | 8081 |
| Microservicio Mascota | `services/pet-service` | 8082 |
| Microservicio GeoService | `services/geoservice` | 8083 |
| Microservicio Motor de Coincidencias | `services/match-service` | 8084 |

## Estructura base propuesta

- `apps/bff-service`
- `services/user-service`
- `services/pet-service`
- `services/geoservice`
- `services/match-service`
- `shared/common`
- `shared/contracts`
- `docs`

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

Usa las siguientes peticiones para probar `user-service` localmente.

- Base URL: `http://localhost:8081/api/v1/users`

Nota: para `register` usa un email que no exista ya en Xano. Si reutilizas el mismo email, el backend puede responder con `Duplicate record detected` aunque la petición HTTP devuelva `200 OK`.

### Registrar (POST `/register`)

Body (JSON):

```json
{
  "fullName": "Test User",
  "email": "test+unique@example.com",
  "password": "P@ssw0rd1",
  "role": "USER"
}
```

Esperado: `success: true` y `data` con `userId`, `role` y `token`.

### Colección de ejemplos para Postman / pruebas

#### URLs base

- User service: `http://localhost:8081/api/v1/users`

#### Encabezados (POST)

- `Content-Type: application/json`

#### Casos de prueba (REGISTER)

1. Registro - ADMIN  
   URL: `POST http://localhost:8081/api/v1/users/register`

   Body (raw JSON):

   ```json
   {
     "fullName": "Admin User",
     "email": "admin+local@example.com",
     "password": "P@ssw0rd!",
     "role": "ADMIN"
   }
   ```

2. Registro - USER  
   URL: `POST http://localhost:8081/api/v1/users/register`

   Body (raw JSON):

   ```json
   {
     "fullName": "Test User",
     "email": "user+local@example.com",
     "password": "P@ssw0rd1",
     "role": "USER"
   }
   ```

3. Registro - COLLABORATOR  
   URL: `POST http://localhost:8081/api/v1/users/register`

   Body (raw JSON):

   ```json
   {
     "fullName": "Clínica San Antón",
     "email": "contacto@sananton.com",
     "password": "P@ssw0rd1",
     "role": "COLLABORATOR",
     "collaboratorType": "VETERINARY_CLINIC"
   }
   ```

#### Casos de prueba (LOGIN)

- Login ADMIN  
  URL: `POST http://localhost:8081/api/v1/users/login`

  Body:

  ```json
  { "email": "admin+local@example.com", "password": "P@ssw0rd!" }
  ```

- Login USER  
  URL: `POST http://localhost:8081/api/v1/users/login`

  Body:

  ```json
  { "email": "user+local@example.com", "password": "P@ssw0rd1" }
  ```

- Login COLLABORATOR  
  URL: `POST http://localhost:8081/api/v1/users/login`

  Body:

  ```json
  { "email": "contacto@sananton.com", "password": "ColabPass" }
  ```

#### Notas útiles

- No enviar `uid` en el payload de registro: Xano (o el servicio autoritativo) generará `uid`.
- Si un email ya existe, la API devolverá error de duplicado; usa emails con `+local` o cambiando el sufijo para pruebas.
- Respuesta de registro esperada (ejemplo):

```json
{
  "data": { "uid": "U005", "role": "USER", "token": "eyJ..." },
  "message": "Registration successful"
}
```

#### Ejemplo rápido con curl (registro USER)

```bash
curl -X POST http://localhost:8081/api/v1/users/register \
  -H 'Content-Type: application/json' \
  -d '{"fullName":"Test User","email":"user+local@example.com","password":"P@ssw0rd1","role":"USER"}'
```

### Ejemplos adicionales de registro (POST `/register`)

- Admin (crear cuenta administrativa):

```json
{
  "fullName": "Admin User",
  "email": "admin@example.com",
  "password": "P@ssw0rd!",
  "role": "ADMIN"
}
```

- Colaborador (COLLABORATOR):

```json
{
  "fullName": "Colaborador Ejemplo",
  "email": "colab+unique@example.com",
  "password": "P@ssw0rd2",
  "role": "COLLABORATOR",
  "collaboratorType": "VETERINARY_CLINIC"
}
```

### Login (POST `/login`)

Body (JSON):

```json
{
  "email": "test+unique@example.com",
  "password": "P@ssw0rd1"
}
```

Esperado: `success: true` y `data` con `userId`, `role` y `token`.

### Health (GET `/health`)

URL: `GET http://localhost:8081/api/v1/users/health`

Esperado: `{ "service": "user-service", "status": "up" }`


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

### Comportamiento de registro en Xano

- Generación atómica de `uid`: Xano mantiene una tabla `counters` interna y usa transacciones para asignar `uid` secuenciales (U### / C###) sin riesgo de colisión aun cuando se eliminan usuarios.
- Prefijos por rol: si el `role` es `COLLABORATOR` el `uid` usa prefijo `C` (ej. `C001`); para `USER` o `ADMIN` se usa `U` (ej. `U001`).
- No enviar `uid` desde el backend: cualquier `uid` en el payload será ignorado; Xano asigna el siguiente `uid` oficial.
- Respuesta de registro: el endpoint `POST /register` devuelve `data` con los campos `uid`, `role` y `token` y un `message` claro. Ejemplo:

```json
{
  "data": {
    "uid": "U005",
    "role": "USER",
    "token": "eyJ..."
  },
  "message": "Registration successful"
}
```

- Restricciones y validaciones: Xano aplica índices únicos sobre `email` y `uid` y devuelve errores claros cuando hay duplicados (ej. email ya registrado).
- Nota para el backend: puedes enviar opcionalmente `role` en la petición de registro; si no se envía, Xano asignará `USER` por defecto.
- Para `role = COLLABORATOR`, enviar también `collaboratorType` con uno de estos valores: `VETERINARY_CLINIC`, `SHELTER`, `MUNICIPALITY`.

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

## Troubleshooting

### Error de login 403 `Invalid credentials`

- Síntoma: respuesta tipo `Authentication failed: 403 Forbidden`.
- Verificación rápida:
  1. Probar `POST /register` con email nuevo.
  2. Probar `POST /login` con el mismo email y password.
  3. Si falla, probar login directo en Xano para aislar si es backend o Xano.
- Causa ya observada en este proyecto: cambios en Xano pendientes de publicar o usuarios antiguos creados antes del ajuste de flujo de password.
- Acción recomendada:
  - Publicar cambios en Xano (`Review` + `Publish`).
  - Re-crear usuario o resetear password para cuentas antiguas.

### Error de registro por duplicado

- Síntoma: `Duplicate record detected` o `Email already registered`.
- Acción: usar emails únicos en pruebas (`+local`, timestamp, etc.).

### Puerto 8081 ocupado

- Síntoma: `bootRun` no levanta porque el puerto ya está en uso.
- Diagnóstico:

```powershell
netstat -ano | findstr ":8081"
```

- Liberar puerto (reemplaza PID):

```powershell
taskkill /PID 12345 /F
```

### JAVA_HOME / Gradle

- Si Gradle no usa Java 21, setea `JAVA_HOME` antes de compilar/ejecutar:

```powershell
$env:JAVA_HOME='C:\Users\onett\.jdk\jdk-21.0.10'
.\gradlew.bat :services:user-service:compileKotlin
```

## Comandos para ejecutar

### Compilar

```bash
./gradlew :services:user-service:compileKotlin
./gradlew :apps:bff-service:compileKotlin
```

### Ejecutar servicios

```bash
./gradlew :services:user-service:bootRun
./gradlew :apps:bff-service:bootRun
```
