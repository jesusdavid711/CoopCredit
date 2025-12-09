# CoopCredit - Microservices Platform

Sistema de gestión de solicitudes de crédito con arquitectura hexagonal.

## 🏗️ Arquitectura

- **credit-application-service** (Puerto 8080) - Servicio principal de aplicaciones de crédito
- **risk-central-mock-service** (Puerto 8081) - Servicio de evaluación de riesgo
- **PostgreSQL** (Puerto 5432) - Base de datos

## 🚀 Inicio Rápido con Docker

### Prerequisitos
- Docker 20+
- Docker Compose 2+

### Iniciar Todo el Sistema

```bash
# 1. Compilar ambos servicios
cd risk-central-mock-service && mvn clean package -DskipTests
cd ../credit-application-service && mvn clean package -DskipTests
cd ..

# 2. Levantar todos los servicios
docker-compose up -d

# 3. Ver logs
docker-compose logs -f
```

### Detener el Sistema

```bash
docker-compose down

# Para eliminar también los datos de PostgreSQL
docker-compose down -v
```

## 📍 Endpoints Principales

### Credit Application Service (8080)
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/v3/api-docs
- **Health:** http://localhost:8080/actuator/health
- **Metrics:** http://localhost:8080/actuator/prometheus

### Risk Central Service (8081)
- **Health:** http://localhost:8081/actuator/health
- **Risk Evaluation:** POST http://localhost:8081/risk-evaluation

## 🔐 Autenticación

El sistema usa JWT. Para acceder a los endpoints protegidos:

1. **Registrar usuario:**
```bash
POST http://localhost:8080/api/auth/register
{
  "username": "admin",
  "password": "admin123",
  "role": "ROLE_ADMIN"
}
```

2. **Login (obtener token):**
```bash
POST http://localhost:8080/api/auth/login
{
  "username": "admin",
  "password": "admin123"
}
```

3. **Usar el token en requests:**
```
Authorization: Bearer <token>
```

## 🛠️ Desarrollo Local (sin Docker)

### Prerequisitos
- Java 17
- Maven 3.8+
- PostgreSQL 15

### 1. Iniciar PostgreSQL

```bash
docker run -d \
  --name postgres-dev \
  -e POSTGRES_DB=coopcredit_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -p 5432:5432 \
  postgres:15-alpine
```

### 2. Iniciar Risk Central Service

```bash
cd risk-central-mock-service
mvn spring-boot:run
```

### 3. Iniciar Credit Application Service

```bash
cd credit-application-service
mvn spring-boot:run
```

## 📊 Base de Datos

Las migraciones Flyway se ejecutan automáticamente al iniciar.

**Tablas creadas:**
- `affiliates` - Afiliados
- `credit_applications` - Solicitudes de crédito
- `risk_evaluations` - Evaluaciones de riesgo
- `users` - Usuarios del sistema

**Usuario inicial:**
- Username: `admin`
- Password: `admin123`
- Role: `ROLE_ADMIN`

## 🧪 Testing

```bash
# Tests unitarios
mvn test

# Tests de integración
mvn verify
```

## 📦 Tecnologías

- **Spring Boot 3.5.7**
- **Java 17**
- **PostgreSQL 15**
- **Maven**
- **MapStruct** - Object mapping
- **JWT** - Autenticación
- **Flyway** - Migraciones
- **Swagger/OpenAPI** - Documentación API
- **Actuator + Prometheus** - Observabilidad

## 📁 Estructura del Proyecto

```
CoopCredit/
├── credit-application-service/     # Servicio principal
│   ├── src/main/java/
│   │   └── com/coopcredit/app/
│   │       ├── domain/             # Lógica de negocio
│   │       ├── application/        # Casos de uso
│   │       └── infrastructure/     # Adaptadores
│   └── src/main/resources/
│       └── db/migration/           # Scripts SQL Flyway
├── risk-central-mock-service/      # Servicio de riesgo
└── docker-compose.yml              # Orquestación
```

## 🔧 Troubleshooting

### Puerto ya en uso
```bash
# Ver qué proceso usa el puerto
lsof -i :8080

# Matar proceso
kill -9 <PID>
```

### Limpiar containers y volúmenes
```bash
docker-compose down -v
docker system prune -a
```

### Reconstruir imágenes
```bash
docker-compose build --no-cache
docker-compose up -d
```

## 👥 Contribuir

1. Fork el proyecto
2. Crea tu branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Add: nueva funcionalidad'`)
4. Push al branch (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

## 📝 Licencia

Este proyecto es parte de un ejercicio académico de CoopCredit.

## 🔗 Links Útiles

- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [Actuator Health](http://localhost:8080/actuator/health)
- [Prometheus Metrics](http://localhost:8080/actuator/prometheus)
