# MS-Auth - Microservicio de Autenticación

Microservicio de autenticación y autorización construido con **Quarkus** que proporciona gestión de credenciales, generación de JWT y validación de tokens para la plataforma Grupo Cordillera.

## Características

- ✅ Autenticación con email y contraseña
- ✅ Generación de JWT (Access Token + Refresh Token)
- ✅ Validación y refresco de tokens
- ✅ Gestión segura de contraseñas (BCrypt)
- ✅ Revocación de tokens
- ✅ API REST documentada con Swagger UI
- ✅ Base de datos PostgreSQL
- ✅ Ejecutables nativos con GraalVM

## Tecnologías Utilizadas

- **Java 21**
- **Quarkus 3.34.5** - Framework Java ultrarrápido
- **PostgreSQL** - Base de datos relacional
- **Hibernate ORM + Panache** - Persistencia de datos
- **SmallRye JWT** - Manejo de tokens JWT
- **Jackson** - Serialización JSON
- **Hibernate Validator** - Validación de datos
- **BCrypt** - Encriptación de contraseñas

## Requisitos Previos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+
- Docker (opcional, para ejecutar en contenedores)

## Configuración Inicial

### 1. Base de Datos

Crear la base de datos con Docker Compose:

```bash
docker-compose up -d
```

### 2. Claves JWT

El proyecto requiere claves pública y privada para firmar los JWT. Estas deben ubicarse en `src/main/resources/`:

- `publicKey.pem`
- `privateKey.pem`

Para generar las claves:

```bash
# Generar clave privada
openssl genrsa -out privateKey.pem 2048

# Generar clave pública
openssl rsa -in privateKey.pem -pubout -out publicKey.pem
```

## Ejecutar la Aplicación

### Modo Desarrollo (con live reload)

```bash
./mvnw quarkus:dev
```

La aplicación estará disponible en `http://localhost:8081`  
Swagger UI: `http://localhost:8081/q/swagger-ui`  
Dev UI: `http://localhost:8081/q/dev`

## Endpoints de API

### 1. Login
**POST** `/auth/login`

Autentica un usuario y retorna access token + refresh token.

**Request:**
```json
{
  "email": "admin@cordillera.cl",
  "password": "123456"
}
```

**Response (200 OK):**
```json
{
  "usuarioId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "admin@cordillera.cl",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Errores:**
- `401 Unauthorized`: Email o contraseña inválidos
- `401 Unauthorized`: Usuario no activo

---

### 2. Refresh Token
**POST** `/auth/refresh`

Obtiene un nuevo access token usando el refresh token.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response (200 OK):**
```json
{
  "usuarioId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "admin@cordillera.cl",
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Errores:**
- `401 Unauthorized`: Refresh token inválido o expirado

---

### 3. Validar Token
**POST** `/auth/validate`

Valida un JWT y retorna información del usuario autenticado.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "valido": true,
  "usuarioId": "550e8400-e29b-41d4-a716-446655440000",
  "email": "admin@cordillera.cl"
}
```

**Errores:**
- `401 Unauthorized`: Token ausente o inválido

---

### 4. Logout
**POST** `/auth/logout`

Revoca el refresh token del usuario.

**Request:**
```json
{
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:** `204 No Content`

---

### 5. Health Check
**GET** `/auth/health`

Verifica que el microservicio esté activo.

**Response:**
```
MS-Auth funcionando
```

## 🔐 Estructura del Proyecto

```
src/main/java/cl/duoc/cordillera/
├── dto/                    # Data Transfer Objects
│   ├── LoginRequestDTO
│   ├── LoginResponseDTO
│   ├── RefreshTokenRequestDTO
│   └── TokenValidationResponseDTO
├── entity/                 # Entidades JPA
│   ├── Credencial
│   └── Token
├── enums/                  # Enumeraciones
│   ├── EstadoUsuario
│   └── TipoToken
├── repository/             # Acceso a datos
│   ├── CredencialRepository
│   └── TokenRepository
├── resource/               # REST Controllers
│   └── AuthResource
└── service/                # Lógica de negocio
    ├── AuthService
    └── JwtService
```
