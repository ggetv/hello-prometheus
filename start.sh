#!/bin/bash 

echo "Starting Prometheus and Grafana..."
docker compose up -d
echo "Prometheus and Grafana started."

echo "Starting demo app: java-old..."
cd apps/java-old
mvn clean package
java -jar target/prometheus-java-demo-1.0-SNAPSHOT-jar-with-dependencies.jar &
echo "demo app: java-old started on port 8081..."

echo "Starting demo app: java..."
cd apps/java
mvn clean package
java -jar target/prometheus-java-demo-new-1.0-SNAPSHOT-jar-with-dependencies.jar &
echo "demo app: java started on port 8082..."
