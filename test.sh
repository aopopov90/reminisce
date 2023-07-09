cloud-sql-proxy impactful-mode-268210:us-central1:reminisce --gcloud-auth --health-check --port 5433 &
while true; do
  response=$(curl -s -w "%{http_code}" -o /dev/null "http://localhost:9090/readiness")
  if [ "$response" == "200" ]; then
    echo "Endpoint is ready"
    break
  fi
  echo "Waiting for endpoint to be ready..."
  sleep 5
done