# RevConnect P3 - Developer Instructions

This document provides step-by-step commit instructions for each developer to contribute to the microservices project.

## Project Setup

Before starting, ensure the shared infrastructure is committed first (this can be done by any team member):

```bash
git init
git add pom.xml .gitignore README.md docker-compose.yml
git add config-server/ discovery-server/ api-gateway/
git commit -m "feat: add shared infrastructure (config-server, discovery-server, api-gateway)"
```

---

## Developer 1: Srinivas Rao - Auth Service (Port 8081)

### Commit Order

**Commit 1: Base structure**
```bash
git add auth-service/pom.xml
git add auth-service/src/main/resources/application.yml
git add auth-service/src/main/java/com/revconnect/auth/AuthServiceApplication.java
git commit -m "feat(auth): add auth-service module with base configuration"
```

**Commit 2: Entity classes**
```bash
git add auth-service/src/main/java/com/revconnect/auth/entity/
git commit -m "feat(auth): add User and RefreshToken entities"
```

**Commit 3: DTOs**
```bash
git add auth-service/src/main/java/com/revconnect/auth/dto/
git commit -m "feat(auth): add authentication DTOs (LoginRequest, RegisterRequest, AuthResponse)"
```

**Commit 4: Repository layer**
```bash
git add auth-service/src/main/java/com/revconnect/auth/repository/
git commit -m "feat(auth): add UserRepository and RefreshTokenRepository"
```

**Commit 5: Security configuration**
```bash
git add auth-service/src/main/java/com/revconnect/auth/config/
git add auth-service/src/main/java/com/revconnect/auth/security/
git commit -m "feat(auth): add security configuration and JWT utilities"
```

**Commit 6: Service layer**
```bash
git add auth-service/src/main/java/com/revconnect/auth/service/
git commit -m "feat(auth): add AuthService, JwtService, and RefreshTokenService"
```

**Commit 7: Controller and Docker**
```bash
git add auth-service/src/main/java/com/revconnect/auth/controller/
git add auth-service/Dockerfile
git commit -m "feat(auth): add AuthController with login/register/refresh endpoints"
```

---

## Developer 2: Gopala Krishna - User Service (Port 8082)

### Commit Order

**Commit 1: Base structure**
```bash
git add user-service/pom.xml
git add user-service/src/main/resources/application.yml
git add user-service/src/main/java/com/revconnect/user/UserServiceApplication.java
git commit -m "feat(user): add user-service module with base configuration"
```

**Commit 2: Entity classes**
```bash
git add user-service/src/main/java/com/revconnect/user/entity/
git commit -m "feat(user): add UserProfile entity"
```

**Commit 3: DTOs**
```bash
git add user-service/src/main/java/com/revconnect/user/dto/
git commit -m "feat(user): add UserProfileRequest, UserProfileResponse, UserSummaryResponse DTOs"
```

**Commit 4: Repository layer**
```bash
git add user-service/src/main/java/com/revconnect/user/repository/
git commit -m "feat(user): add UserProfileRepository"
```

**Commit 5: Configuration**
```bash
git add user-service/src/main/java/com/revconnect/user/config/
git commit -m "feat(user): add security configuration"
```

**Commit 6: Service layer**
```bash
git add user-service/src/main/java/com/revconnect/user/service/
git commit -m "feat(user): add UserProfileService with profile management logic"
```

**Commit 7: Controller and Docker**
```bash
git add user-service/src/main/java/com/revconnect/user/controller/
git add user-service/Dockerfile
git commit -m "feat(user): add UserProfileController with profile endpoints"
```

---

## Developer 3: Vasanth - Post Service (Port 8083)

### Commit Order

**Commit 1: Base structure**
```bash
git add post-service/pom.xml
git add post-service/src/main/resources/application.yml
git add post-service/src/main/java/com/revconnect/post/PostServiceApplication.java
git commit -m "feat(post): add post-service module with base configuration"
```

**Commit 2: Entity classes**
```bash
git add post-service/src/main/java/com/revconnect/post/entity/
git commit -m "feat(post): add Post entity"
```

**Commit 3: DTOs**
```bash
git add post-service/src/main/java/com/revconnect/post/dto/
git commit -m "feat(post): add CreatePostRequest, PostResponse, FeedResponse DTOs"
```

**Commit 4: Feign clients**
```bash
git add post-service/src/main/java/com/revconnect/post/client/
git commit -m "feat(post): add Feign clients for user-service and interaction-service"
```

**Commit 5: Repository layer**
```bash
git add post-service/src/main/java/com/revconnect/post/repository/
git commit -m "feat(post): add PostRepository"
```

**Commit 6: Configuration**
```bash
git add post-service/src/main/java/com/revconnect/post/config/
git commit -m "feat(post): add security configuration"
```

**Commit 7: Service layer**
```bash
git add post-service/src/main/java/com/revconnect/post/service/
git commit -m "feat(post): add PostService with CRUD and feed logic"
```

**Commit 8: Controller and Docker**
```bash
git add post-service/src/main/java/com/revconnect/post/controller/
git add post-service/Dockerfile
git commit -m "feat(post): add PostController with post and feed endpoints"
```

---

## Developer 4: Sandeep - Network Service (Port 8084)

### Commit Order

**Commit 1: Base structure**
```bash
git add network-service/pom.xml
git add network-service/src/main/resources/application.yml
git add network-service/src/main/java/com/revconnect/network/NetworkServiceApplication.java
git commit -m "feat(network): add network-service module with base configuration"
```

**Commit 2: Entity classes**
```bash
git add network-service/src/main/java/com/revconnect/network/entity/
git commit -m "feat(network): add Connection entity with status enum"
```

**Commit 3: DTOs**
```bash
git add network-service/src/main/java/com/revconnect/network/dto/
git commit -m "feat(network): add ConnectionRequest, ConnectionResponse DTOs"
```

**Commit 4: Feign clients**
```bash
git add network-service/src/main/java/com/revconnect/network/client/
git commit -m "feat(network): add Feign clients for user-service and notification-service"
```

**Commit 5: Repository layer**
```bash
git add network-service/src/main/java/com/revconnect/network/repository/
git commit -m "feat(network): add ConnectionRepository"
```

**Commit 6: Configuration**
```bash
git add network-service/src/main/java/com/revconnect/network/config/
git commit -m "feat(network): add security configuration"
```

**Commit 7: Service layer**
```bash
git add network-service/src/main/java/com/revconnect/network/service/
git commit -m "feat(network): add ConnectionService with connection management logic"
```

**Commit 8: Controller and Docker**
```bash
git add network-service/src/main/java/com/revconnect/network/controller/
git add network-service/Dockerfile
git commit -m "feat(network): add ConnectionController with connection endpoints"
```

---

## Developer 5: Siva Sai - Interaction Service (Port 8085)

### Commit Order

**Commit 1: Base structure**
```bash
git add interaction-service/pom.xml
git add interaction-service/src/main/resources/application.yml
git add interaction-service/src/main/java/com/revconnect/interaction/InteractionServiceApplication.java
git commit -m "feat(interaction): add interaction-service module with base configuration"
```

**Commit 2: Entity classes**
```bash
git add interaction-service/src/main/java/com/revconnect/interaction/entity/
git commit -m "feat(interaction): add Like and Comment entities"
```

**Commit 3: DTOs**
```bash
git add interaction-service/src/main/java/com/revconnect/interaction/dto/
git commit -m "feat(interaction): add LikeRequest, CommentRequest, InteractionCountResponse DTOs"
```

**Commit 4: Feign clients**
```bash
git add interaction-service/src/main/java/com/revconnect/interaction/client/
git commit -m "feat(interaction): add Feign clients for user-service and notification-service"
```

**Commit 5: Repository layer**
```bash
git add interaction-service/src/main/java/com/revconnect/interaction/repository/
git commit -m "feat(interaction): add LikeRepository and CommentRepository"
```

**Commit 6: Configuration**
```bash
git add interaction-service/src/main/java/com/revconnect/interaction/config/
git commit -m "feat(interaction): add security configuration"
```

**Commit 7: Service layer**
```bash
git add interaction-service/src/main/java/com/revconnect/interaction/service/
git commit -m "feat(interaction): add LikeService, CommentService, InteractionService"
```

**Commit 8: Controllers and Docker**
```bash
git add interaction-service/src/main/java/com/revconnect/interaction/controller/
git add interaction-service/Dockerfile
git commit -m "feat(interaction): add LikeController, CommentController, InteractionController"
```

---

## Developer 6: Yakkanti - Notification Service (Port 8086)

### Commit Order

**Commit 1: Base structure**
```bash
git add notification-service/pom.xml
git add notification-service/src/main/resources/application.yml
git add notification-service/src/main/java/com/revconnect/notification/NotificationServiceApplication.java
git commit -m "feat(notification): add notification-service module with base configuration"
```

**Commit 2: Entity classes**
```bash
git add notification-service/src/main/java/com/revconnect/notification/entity/
git commit -m "feat(notification): add Notification entity with type enum"
```

**Commit 3: DTOs**
```bash
git add notification-service/src/main/java/com/revconnect/notification/dto/
git commit -m "feat(notification): add CreateNotificationRequest, NotificationResponse DTOs"
```

**Commit 4: Repository layer**
```bash
git add notification-service/src/main/java/com/revconnect/notification/repository/
git commit -m "feat(notification): add NotificationRepository"
```

**Commit 5: Configuration**
```bash
git add notification-service/src/main/java/com/revconnect/notification/config/
git commit -m "feat(notification): add security configuration"
```

**Commit 6: Service layer**
```bash
git add notification-service/src/main/java/com/revconnect/notification/service/
git commit -m "feat(notification): add NotificationService with notification management logic"
```

**Commit 7: Controller and Docker**
```bash
git add notification-service/src/main/java/com/revconnect/notification/controller/
git add notification-service/Dockerfile
git commit -m "feat(notification): add NotificationController with notification endpoints"
```

---

## Commit Message Guidelines

Follow conventional commits format:
- `feat(scope): description` - New feature
- `fix(scope): description` - Bug fix
- `docs(scope): description` - Documentation
- `refactor(scope): description` - Code refactoring
- `test(scope): description` - Adding tests

Scope should be the service name: `auth`, `user`, `post`, `network`, `interaction`, `notification`

---

## Service Dependencies

When committing, be aware of service dependencies:

| Service | Depends On |
|---------|-----------|
| auth-service | None (independent) |
| user-service | None (independent) |
| post-service | user-service, interaction-service |
| network-service | user-service, notification-service |
| interaction-service | user-service, notification-service |
| notification-service | None (independent) |

**Recommended commit order across team:**
1. Srinivas Rao (auth) - Can start immediately
2. Gopala Krishna (user) - Can start immediately
3. Yakkanti (notification) - Can start immediately
4. Sandeep (network) - After user-service and notification-service
5. Siva Sai (interaction) - After user-service and notification-service
6. Vasanth (post) - After user-service and interaction-service

---

## Testing Your Service

```bash
# Build the service
cd <service-directory>
mvn clean package

# Run the service
mvn spring-boot:run

# Test endpoints (example for auth-service)
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"password123","firstName":"Test","lastName":"User"}'
```

---

## Verification Checklist

Before pushing, verify:
- [ ] Service starts without errors
- [ ] Registers with Eureka (check http://localhost:8761)
- [ ] API endpoints respond correctly
- [ ] No hardcoded values (use application.yml)
- [ ] Proper error handling in place
