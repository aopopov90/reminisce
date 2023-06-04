## Database
```
docker run --name pg-reminisce -e POSTGRES_PASSWORD=1234 -d -p 5432:5432 postgres
```

## API

### Swagger
Dependency (for Spring Boot 3)
```
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'
```

Accessing
```
http://localhost:8080/swagger-ui/index.html
```