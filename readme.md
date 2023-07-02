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

```bash
gcloud auth application-default login
./gradlew jib -image us-east1-docker.pkg.dev/impactful-mode-268210/reminisce/reminisce
```

Deploy locally:
```
docker run --name reminisce -e pg_password=1234 -d -p 8080:8080 aopopov/reminisce
```

Note: lookup pg-reminisce container IP by inspecting the Bridge network:
```agsl
docker network inspect bridge
```

### CloudRun

```bash
gcloud run deploy reminisce \
    --image us-east1-docker.pkg.dev/impactful-mode-268210/reminisce/reminisce \
    --platform managed \
    --set-env-vars=pg_password=<password>
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

### Cloud-sql-proxy
```bash
cloud-sql-proxy impactful-mode-268210:us-central1:reminisce --gcloud-auth
```

### When app connects to CloudSQL directly

```bash
gcloud auth application-default login
```