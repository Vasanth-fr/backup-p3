# RevConnect Microservices

A social networking platform built with Spring Boot microservices architecture.

## Architecture

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │    (8080)       │
                    └────────┬────────┘
                             │
         ┌───────────────────┼───────────────────┐
         │                   │                   │
         ▼                   ▼                   ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│Auth Service │     │User Service │     │Post Service │
│   (8081)    │     │   (8082)    │     │   (8083)    │
└─────────────┘     └─────────────┘     └─────────────┘
         │                   │                   │
         ▼                   ▼                   ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Network    │     │ Interaction │     │Notification │
│  Service    │     │  Service    │     │  Service    │
│   (8084)    │     │   (8085)    │     │   (8086)    │
└─────────────┘     └─────────────┘     └─────────────┘
```

## Services

| Service | Port | Description | Owner |
|---------|------|-------------|-------|
| Config Server | 8888 | Centralized configuration | Shared |
| Discovery Server | 8761 | Eureka service registry | Shared |
| API Gateway | 8080 | Request routing & JWT validation | Shared |
| Auth Service | 8081 | Authentication & JWT tokens | Srinivas Rao |
| User Service | 8082 | User profiles management | Gopala Krishna |
| Post Service | 8083 | Posts and feed | Vasanth |
| Network Service | 8084 | Connections/friends | Sandeep |
| Interaction Service | 8085 | Likes & comments | Siva Sai |
| Notification Service | 8086 | User notifications | Yakkanti |

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (optional)

## Running Locally

### 1. Build all services
```bash
mvn clean package -DskipTests
```

### 2. Start services in order
```bash
# Terminal 1: Config Server
cd config-server && mvn spring-boot:run

# Terminal 2: Discovery Server (wait for config server)
cd discovery-server && mvn spring-boot:run

# Terminal 3: API Gateway
cd api-gateway && mvn spring-boot:run

# Terminal 4-9: Business services (can start in parallel)
cd auth-service && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd post-service && mvn spring-boot:run
cd network-service && mvn spring-boot:run
cd interaction-service && mvn spring-boot:run
cd notification-service && mvn spring-boot:run
```

### Using Docker Compose
```bash
mvn clean package -DskipTests
docker-compose up --build
```

## API Endpoints

All requests go through the API Gateway at `http://localhost:8080`

### Auth Service
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT
- `POST /api/auth/refresh` - Refresh JWT token
- `POST /api/auth/logout` - Logout

### User Service
- `GET /api/users/profile` - Get current user profile
- `PUT /api/users/profile` - Update profile
- `GET /api/users/{userId}` - Get user by ID
- `GET /api/users/search?query=` - Search users

### Post Service
- `POST /api/posts` - Create post
- `GET /api/posts/{postId}` - Get post
- `PUT /api/posts/{postId}` - Update post
- `DELETE /api/posts/{postId}` - Delete post
- `GET /api/posts/feed` - Get feed

### Network Service
- `POST /api/network/connect` - Send connection request
- `PUT /api/network/connections/{id}/accept` - Accept request
- `GET /api/network/connections` - Get connections
- `GET /api/network/pending` - Get pending requests

### Interaction Service
- `POST /api/interactions/likes` - Like a post
- `DELETE /api/interactions/likes/{postId}` - Unlike
- `POST /api/interactions/comments` - Add comment
- `GET /api/interactions/comments/post/{postId}` - Get comments

### Notification Service
- `GET /api/notifications` - Get notifications
- `PUT /api/notifications/{id}/read` - Mark as read
- `PUT /api/notifications/read-all` - Mark all as read

## Technology Stack

- Spring Boot 3.2
- Spring Cloud (Config, Eureka, Gateway)
- Spring Security + JWT
- Spring Data JPA
- H2 Database (dev)
- OpenFeign
- Resilience4j
- Docker
