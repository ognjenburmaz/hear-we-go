@echo off
setlocal enabledelayedexpansion

echo [1/10] Starting Minikube with 6 Cores and 8GB RAM...
minikube start --cpus=6 --memory=8192 --driver=docker

echo [2/10] Creating namespace 'hear-we-go'...
kubectl create namespace hear-we-go

echo [3/10] Fixing ScyllaDB Kernel limits (aio-max-nr)...
minikube ssh "sudo sysctl -w fs.aio-max-nr=2097152"

echo [4/10] Building Docker Images...
:: We build locally then load into minikube. 
:: Alternatively: call @FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env --shell cmd') DO %%i
docker-compose build

echo [5/10] Creating ConfigMaps and Secrets...
:: Create secrets from your certs folder
kubectl create secret generic cert-secret --from-file=./certs -n hear-we-go
:: Create configmap from your .env file
kubectl create configmap app-config --from-env-file=.env -n hear-we-go
:: Create configmap for neo4j init scripts
kubectl create configmap neo4j-init-config \
  --from-file=init.sh=./neo4j/init/init.sh \
  --from-file=schema.cypher=./neo4j/init/schema.cypher \
  -n hear-we-go --dry-run=client -o yaml | kubectl apply -f -

echo [6/10] Loading Images into Minikube (This may take a while)...
set "services=api-gateway discovery-service users-service content-service ratings-service subscriptions-service notification-service recommendation-service analytics-service frontend-client"
set count=0
for %%s in (%services%) do (
    set /a count+=1
    echo   !count!/9 Loading image: hear-we-go-%%s:latest
    minikube image load hear-we-go-%%s:latest
)

echo [7/10] Deploying Infrastructure (DBs, Kafka, etc.)...
set count=0
for %%f in (k8s\infra\*.yaml) do (
    set /a count+=1
    echo   Deploying infra piece !count!: %%f
    kubectl apply -f %%f -n hear-we-go
)

echo Waiting for MongoDB to be ready for init script...
kubectl wait --for=condition=ready pod -l app=streaming-mongo -n hear-we-go --timeout=120s

echo [8/10] Initializing MongoDB Schema...
:: Get the mongo pod name dynamically
for /f "tokens=*" %%i in ('kubectl get pods -l app=streaming-mongo -n hear-we-go -o name') do set MONGO_POD=%%i
kubectl cp ./backend/common-lib/src/main/resources/db/import_schema.js hear-we-go/%MONGO_POD:~4%:/tmp/init.js
kubectl exec -it %MONGO_POD% -n hear-we-go -- mongosh users-db /tmp/init.js

echo [9/10] Deploying Microservices...
set count=0
for %%f in (k8s\services\*.yaml) do (
    set /a count+=1
    echo   Deploying service !count!: %%f
    kubectl apply -f %%f -n hear-we-go
)

echo [10/10] SETUP COMPLETE!
echo --------------------------------------------------
echo IMPORTANT: Open a NEW terminal and run: minikube tunnel
echo This is required to access your LoadBalancers (Gateway/Frontend).
echo --------------------------------------------------
pause