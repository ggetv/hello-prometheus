#!/bin/bash 

echo "Stopping Prometheus and Grafana..."
docker compose down
echo "Prometheus and Grafana stopped."

echo "Stopping demo app: java-old..."
cd apps/java-old
mvn clean
kill $(lsof -ti :8081)
echo "demo app: java-old stopped."

echo "Stopping demo app: java..."
cd apps/java
mv clean
kill $(lsof -ti :8082)
echo "demo app: java stopped."
