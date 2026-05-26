URL Shortener

A production-grade URL shortener built with Java, Spring Boot, PostgreSQL, and Redis featuring custom aliases, analytics, expiration support, and Redis caching.

Features

* Create shortened URLs
* Custom alias support (6-character alphanumeric)
* Fast redirection with Redis cache-aside strategy
* URL analytics (click count, creation time, expiry)
* URL expiration handling
* Centralized exception handling
* Request validation
* Swagger API documentation

Tech Stack

* Java 17
* Spring Boot
* PostgreSQL
* Redis
* Spring Data JPA
* Hibernate
* ModelMapper
* Swagger / OpenAPI 3
* Maven

Architecture

Clean layered architecture:

* Controller → API endpoints
* Service → Business logic
* Repository → Data access
* DTO → Request/response contracts
* Entity → Database mapping
* Exception Handler → Centralized API error handling
* Config → Bean configuration

API Endpoints

Create Short URL

POST /api/urls/shorten

Request:

{
"originalUrl": "https://google.com",
"customAlias": "kamal1",
"expiresAt": "2026-12-31T23:59:59"
}

Response:

{
"shortUrl": "http://localhost:8080/api/urls/r/kamal1"
}

⸻

Redirect

GET /api/urls/r/{shortCode}

Redirects to original URL and increments click count.

⸻

Analytics

GET /api/urls/analytics/{shortCode}

Response:

{
"originalUrl": "https://google.com",
"shortUrl": "http://localhost:8080/api/urls/r/kamal1",
"clickCount": 10,
"createdAt": "2026-05-26T10:00:00",
"expiresAt": "2026-12-31T23:59:59"
}

Caching Flow

Request → Redis Cache → Cache Miss → PostgreSQL → Cache Store → Response

This reduces database load and improves redirect latency for read-heavy traffic.

Error Handling

Structured API error responses:

{
"timestamp": "2026-05-26T10:00:00",
"status": 400,
"error": "Bad Request",
"message": "Invalid URL format"
}

Run Locally

Clone Repository

git clone <repo-url>
cd url-shortener

Configure PostgreSQL and Redis

Update application.properties

Run

mvn spring-boot:run

Swagger:

http://localhost:8080/api/swagger-ui/index.html

Future Improvements

* Redis TTL sync with URL expiration
* Async click counting
* Deployment with cloud hosting
* Distributed short-code generation strategy

Author

Kamal Kant Rajput