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

### Containerizing

Build and push
```
gradle jib -image aopopov/reminisce
```

Deploy locally:
```
docker run --name reminisce -e pg_password=1234 -d -p 8080:8080 aopopov/reminisce
```

Note: lookup pg-reminisce container IP by inspecting the Bridge network:
```agsl
docker network inspect bridge
```

### Liquibase

Generate changelog
```bash
liquibase generatechangelog --password=<password>
```

Update
```bash
liquibase update --password=<password>
```