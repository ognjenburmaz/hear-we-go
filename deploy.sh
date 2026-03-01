#!/bin/bash

#set -e  # stop on error

echo "[1/10] Starting Minikube with 6 Cores and ~6GB RAM..."
minikube start --cpus=6 --memory=5921 --driver=docker


echo "[2/10] Creating namespace 'hear-we-go'..."
kubectl create namespace hear-we-go 2>/dev/null || echo "Namespace already exists."


echo "[3/10] Fixing ScyllaDB Kernel limits (aio-max-nr)..."
minikube ssh "sudo sysctl -w fs.aio-max-nr=2097152"


echo "[4/10] Building Docker Images..."
docker compose build


echo "[5/10] Creating ConfigMaps and Secrets..."

kubectl create secret generic cert-secret \
  --from-file=./certs \
  -n hear-we-go \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create configmap app-config \
  --from-env-file=.env \
  -n hear-we-go \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl create configmap neo4j-init-config \
  --from-file=./neo4j/init/init.sh \
  --from-file=./neo4j/init/schema.cypher \
  -n hear-we-go \
  --dry-run=client -o yaml | kubectl apply -f -

echo "[5/10] ConfigMaps and Secrets ready."


echo "[6/10] Loading Images into Minikube (This may take a while)..."

services=(
  api-gateway
  discovery-service
  users-service
  content-service
  ratings-service
  subscriptions-service
  notification-service
  recommendation-service
  analytics-service
  frontend-client
)

count=0
total=${#services[@]}

for service in "${services[@]}"; do
  ((count++))
  echo "  $count/$total Loading image: hear-we-go-$service:latest"
  minikube image load hear-we-go-$service:latest
done


echo "[7/10] Deploying Infrastructure (DBs, Kafka, etc.)..."

count=0
for file in k8s/infra/*.yaml; do
  ((count++))
  echo "  Deploying infra piece $count: $file"
  kubectl apply -f "$file" -n hear-we-go
done


echo "Waiting for MongoDB to be ready for init script..."
kubectl wait --for=condition=ready pod -l app=streaming-mongo -n hear-we-go --timeout=120s


echo "[8/10] Initializing MongoDB Schema..."
sleep 10 # Extra wait to ensure MongoDB is fully ready

MONGO_POD=$(kubectl get pods -l app=streaming-mongo -n hear-we-go -o name | head -n 1)

if [ -z "$MONGO_POD" ]; then
  echo "Mongo pod not found!"
  exit 1
fi

MONGO_POD_NAME=${MONGO_POD#pod/}

kubectl cp ./backend/common-lib/src/main/resources/db/import_schema.js \
  hear-we-go/$MONGO_POD_NAME:/tmp/init.js

kubectl exec -it "$MONGO_POD_NAME" -n hear-we-go -- \
  mongosh users-db /tmp/init.js


echo "[9/10] Deploying Microservices..."

count=0
for file in k8s/services/*.yaml; do
  ((count++))
  echo "  Deploying service $count: $file"
  kubectl apply -f "$file" -n hear-we-go
done


echo "[10/10] SETUP COMPLETE!"
echo "--------------------------------------------------"
echo "IMPORTANT: Open a NEW terminal and run: minikube tunnel"
echo "--------------------------------------------------"