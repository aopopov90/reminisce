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
    --set-env-vars=PG_PASSWORD=changeit
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

### Workload Identity (WI) Provider Set-up

Create SA:
```bash
export PROJECT_ID="impactful-mode-268210"
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