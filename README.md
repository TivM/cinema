# Cinema - Reactive Java Application

Реактивное приложение для онлайн-кинотеатра на Spring Boot WebFlux.

## Технологии

- Java 21
- Spring Boot 3.5.9 (WebFlux)
- PostgreSQL + R2DBC
- Reactor (Mono/Flux)
- Server-Sent Events (SSE)

## Требования

- Java 21+
- Docker & Docker Compose
- Maven

## Запуск

### 1. Запуск базы данных

```bash
docker-compose up -d
```

### 2. Запуск приложения (разработка)

```bash
./mvnw spring-boot:run -pl cinema-app
```

### 3. Сборка JAR

```bash
./mvnw clean package -DskipTests
```

### 4. Запуск JAR

```bash
java -jar cinema-app/target/cinema-app-0.0.1-SNAPSHOT.jar
```

### 5. Одной командой (сборка + запуск)

```bash
./mvnw clean package -DskipTests && java -jar cinema-app/target/cinema-app-0.0.1-SNAPSHOT.jar
```

## Доступ

- **UI**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## API Endpoints

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/movies` | Каталог фильмов (с пагинацией) |
| GET | `/api/movies/{id}` | Информация о фильме |
| PUT | `/api/users/{userId}/ratings/{movieId}` | Оценить фильм |
| GET | `/api/users/{userId}/top10` | Топ-10 фильмов пользователя |
| GET | `/api/users/{userId}/top10/stream` | SSE стрим топ-10 |
| GET | `/api/notifications` | SSE стрим уведомлений |

## Архитектура

```
cinema/
├── cinema-domain/        # Доменные модели и порты
├── cinema-application/   # Сервисы и бизнес-логика
├── cinema-infrastructure/# Адаптеры (PostgreSQL)
└── cinema-app/           # Spring Boot приложение, контроллеры, UI
```

## Функциональности

- Каталог фильмов с пагинацией
- Оценка фильмов (1-10)
- Персональный топ-10
- Автогенерация фильмов каждые 30 сек
- Real-time уведомления через SSE

