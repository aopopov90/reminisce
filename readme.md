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
./gradlew jib --image us-east1-docker.pkg.dev/impactful-mode-268210/reminisce/reminisce
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
    --set-env-vars=PG_PASSWORD=changeit \
    --set-env-vars=SPRING_PROFILES_ACTIVE=staging
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

### Generating Liquibase changelog via diff-changelog

This operation requires two database instances to be available:
1. The local instance containing the latest version of schema (development)
2. The reference instance. This will be a cloud instance running the latest stable version of the schema

Firstly, ensure the dev database is up and running locally (via docker). This instance should be accessible on port 5432.
The `liquibase.command.url` property in the liquibase.properties file should be pointing to this instance: `jdbc:postgresql://localhost:5433/postgres`.

Next, connect to the cloud instance using cloud-sql-proxy, exposing connection on localhost:5433:
```bash
cloud-sql-proxy impactful-mode-268210:us-central1:reminisce --gcloud-auth --port 5433
```

Execute the `liquibase diff-changelog` command.
```bash
liquibase diff-changelog \
  --password=<local_pwd> \
  --reference-password=<cloudsql_pwd>
```

Sanitize the generated changelog file and place it under the changelog directory, incrementing the version count. Example:
`db/changelog/changelog-0.x/chnagelog-0.2.sql`
TODO: script to simplify this

### Cloud-sql-proxy
```bash
cloud-sql-proxy impactful-mode-268210:us-central1:reminisce --gcloud-auth --port 5433
```

### When app connects to CloudSQL directly

```bash
gcloud auth application-default login
```

### Workload Identity (WI) Provider Set-up

Create SA:
```bash
export PROJECT_ID="impactful-mode-268210"
export PROJECT_NUMBER="972321173961"
export SA_NAME="gh-reminisce"
export SA_ID="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
gcloud config set project ${PROJECT_ID}
gcloud iam service-accounts create "${SA_NAME}" --project "${PROJECT_ID}"
```

Grant required roles:
```bash
roles=(
  "roles/artifactregistry.writer"
  "roles/run.developer"
  "roles/cloudsql.client"
  "roles/cloudsql.instanceUser"
)

for role in "${roles[@]}"; do
  gcloud projects add-iam-policy-binding ${PROJECT_ID} \
    --member="serviceAccount:${SA_ID}" \
    --role=${role}
done 

# allow GH SA to act as a runtime SA
gcloud iam service-accounts add-iam-policy-binding "${PROJECT_NUMBER}-compute@developer.gserviceaccount.com" \
    --member "serviceAccount:${SA_ID}" \
    --role "roles/iam.serviceAccountUser"
```

Enable the IAM Credentials API:
```bash
gcloud services enable iamcredentials.googleapis.com --project "${PROJECT_ID}"
```

Create WI Pool:
```bash
gcloud iam workload-identity-pools create "gh-reminisce" \
  --location="global" \
  --display-name="GH Reminisce pool"
```

Store WI Pool ID
```bash
export WORKLOAD_IDENTITY_POOL_ID=`gcloud iam workload-identity-pools describe "gh-reminisce" \
                                  --location="global" \
                                  --format="value(name)"`
echo "WORKLOAD_IDENTITY_POOL_ID: ${WORKLOAD_IDENTITY_POOL_ID}"
```

Create WI Provider in that pool:
```bash
gcloud iam workload-identity-pools providers create-oidc "gh-reminisce-provider" \
  --location="global" \
  --workload-identity-pool="gh-reminisce" \
  --display-name="GH Reminisce WI provider" \
  --attribute-mapping="google.subject=assertion.sub,attribute.actor=assertion.actor,attribute.repository=assertion.repository" \
  --issuer-uri="https://token.actions.githubusercontent.com"
```

```bash
export REPO="aopopov90/reminisce"

gcloud iam service-accounts add-iam-policy-binding "${SA_ID}" \
  --role="roles/iam.workloadIdentityUser" \
  --member="principalSet://iam.googleapis.com/${WORKLOAD_IDENTITY_POOL_ID}/attribute.repository/${REPO}"
```

### Application user set-up

## User account

This account is accessed by the application to read and write data.
This script grants SELECT, INSERT, UPDATE, and DELETE privileges on all tables and USAGE and SELECT privileges on all sequences within the public schema.
```sql
-- Connect to PostgreSQL as a superuser (e.g., postgres)

-- Create a new user
CREATE USER reminisce_user WITH PASSWORD 'password';

-- Grant necessary privileges to the user for a specific database and schema
GRANT CONNECT ON DATABASE postgres TO reminisce_user;
GRANT USAGE ON SCHEMA public TO reminisce_user;

-- Grant privileges on all tables and sequences in the public schema
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO reminisce_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO reminisce_user;

-- Set default privileges for future object
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO reminisce_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO reminisce_user;
```

## Owner account

This is the account accessed by the GitHub workflow to execute liquibase.
The permissions are quite extensive as the account needs to manage and modify database objects.
```sql
-- Connect to PostgreSQL as a superuser (e.g., postgres)

-- Create the 'owner' user
CREATE USER reminisce_owner WITH PASSWORD 'password';

-- Grant necessary privileges to manage the schema
GRANT ALL PRIVILEGES ON SCHEMA public TO reminisce_owner;

-- Grant the ability to create, modify, and drop tables
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO reminisce_owner;

-- Grant the ability to create, modify, and drop sequences
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO reminisce_owner;

-- Grant the ability to create, modify, and drop functions and procedures
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO reminisce_owner;
```

Logging under the newly created 'owner' account and create default grants for the user.
This has to be done by the owner of the objects.
```sql
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO reminisce_user;
ALTER DEFAULT PRIVILEGES IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO reminisce_user;
```